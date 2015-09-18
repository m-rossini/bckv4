/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Created on 27/01/2006
 */

package br.com.auster.billcheckout.rules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import br.com.auster.billcheckout.rules.assertion.AssertionEngine;
import br.com.auster.common.stats.ProcessingStats;
import br.com.auster.common.stats.StatsMapping;
import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.dware.graph.ConnectException;
import br.com.auster.dware.graph.DefaultFilter;
import br.com.auster.dware.graph.FilterException;
import br.com.auster.dware.graph.ObjectProcessor;
import br.com.auster.dware.graph.Request;
import br.com.auster.om.filter.request.BillcheckoutRequestWrapper;
import br.com.auster.om.invoice.InvoiceModelObject;

/**
 * <p>
 * <b>Title:</b> InvoiceRulesEngineFilter
 * </p>
 * <p>
 * <b>Description:</b> A Rules Engine filter that instantiates a rules engine,
 * asserts Invoice OM objects and fire rules for them
 * </p>
 * <p>
 * <b>Copyright:</b> Copyright (c) 2006
 * </p>
 * <p>
 * <b>Company:</b> Auster Solutions
 * </p>
 * 
 * @author etirelli
 * @version $Id: InvoiceRulesEngineFilter.java 412 2007-03-26 13:57:01Z pvieira $
 */
public class InvoiceRulesEngineFilter extends DefaultFilter implements ObjectProcessor {
	//TODO Create a mechanism to get query results from filter and pass along in the processing flow, including query parameters.
	// ---------------------------
	// Class constants
	// ---------------------------

	private static final I18n			i18n															= I18n
																																			.getInstance(InvoiceRulesEngineFilter.class);
	private static final Logger		log																= Logger
																																			.getLogger(InvoiceRulesEngineFilter.class);

	public static final String		FILTER_CONFIG_ENGINE_PLUGIN				= "rules-engine";
	public static final String		FILTER_CONFIG_ENGINE_PLUGIN_CLASS	= "class-name";
	public static final String		CONFIG_INPUT_LIST									= "input-list-tag";
	public static final String		CONFIG_RESULTS_TAG								= "results-map-tag";
	public static final String		CONFIG_NAME_ATTR									= "name";

	public static final String		FILTER_CONFIG_GLOBALSLIST_ELEMENT	= "globals-list";
	public static final String		FILTER_CONFIG_GLOBAL_ELEMENT			= "global";
	public static final String		FILTER_CONFIG_GLOBAL_NAME					= "name";
	public static final String		FILTER_CONFIG_GLOBAL_CLASS				= "class-name";

	public static final String		ASSERTION_ELMT										= "assertionEngine";

	// ---------------------------
	// Instance variables
	// ---------------------------

	private RulesEngineProcessor	engine;
	private String								inputTag;
	private String								resultsTag;
	private ObjectProcessor				objProcessor;
	private Map<String, Object>		globals;

	private String								xpathFile;

	private Request								req;
	private AssertionEngine				assertionEngine;

	// ---------------------------
	// Constructor
	// ---------------------------

	public InvoiceRulesEngineFilter(String _name) {
		super(_name);
	}

	// ---------------------------
	// Public methods
	// ---------------------------

	/**
	 * <P>
	 * Walks through the <code>_configuration</code> parameter looking for the
	 * invoice rules engine plugin class.
	 * </P>
	 * 
	 * @param _configuration
	 *          the root configuration DOM element
	 * 
	 * @exception FilterException
	 *              if anything when wrong while reading the configuration
	 */
	public void configure(Element _configuration) {
		log.debug("Configuring InvoiceRulesEngineFilter");
		Element engineElement = DOMUtils.getElement(_configuration,
				FILTER_CONFIG_ENGINE_PLUGIN, true);
		String engineKlass = DOMUtils.getAttribute(engineElement,
				FILTER_CONFIG_ENGINE_PLUGIN_CLASS, true);

		// Loading Facade List
		configureGlobals(_configuration);

		try {
			this.engine = createEngine(engineKlass, this.filterName);
			configureEngine(engineElement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Element inputElem = DOMUtils.getElement(_configuration, CONFIG_INPUT_LIST, true);
		inputTag = DOMUtils.getAttribute(inputElem, CONFIG_NAME_ATTR, true);
		Element resultsElem = DOMUtils.getElement(_configuration, CONFIG_RESULTS_TAG, true);
		resultsTag = DOMUtils.getAttribute(resultsElem, CONFIG_NAME_ATTR, true);

		try {
			configureAssertion(_configuration);
		} catch (ClassNotFoundException e) {
			log.fatal("Unable to create Assertion Engine.");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			log.fatal("Unable to create Assertion Engine.");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			log.fatal("Unable to create Assertion Engine.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * Configure the Globals all rules can access. This method is part of an
	 * ordered method calls that can be done to configure the whole engine. This
	 * is part #4 and MUST be the LAST configure method to be called
	 * 
	 * @param _configuration
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private void configureAssertion(Element _configuration) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		// Handles Assertion type configuration.
		// If not present or wrong assumes by reflection.
		Element assertion = DOMUtils.getElement(_configuration, ASSERTION_ELMT, false);
		if (assertion == null) {
			String msg = "Assertion Configuration not found. Unable to assume a default one.";
			log.fatal(msg);
			throw new RuntimeException(msg);
		}

		String klassName = DOMUtils.getAttribute(assertion, FILTER_CONFIG_GLOBAL_CLASS, true);

		Class<?> klass = Class.forName(klassName);
		assertionEngine = (AssertionEngine) klass.newInstance();
		assertionEngine.configure(assertion);
	}

	/**
	 * Configure the Globals all rules can access. This method is part of an
	 * ordered method calls that can be done to configure the whole engine. This
	 * is part #3 and must be made AFTER configuring Globals, and AFTER engine
	 * creation once it depends on that.
	 * 
	 * @param engineElement
	 * @throws Exception
	 */
	private void configureEngine(Element engineElement) throws Exception {
		StatsMapping stats = ProcessingStats.starting(getClass(), "RulesEngineConfiguration");
		try {
			this.engine.configure(engineElement);
			this.engine.init(this.globals);
		} finally {
			stats.finished();
		}
	}

	/**
	 * Configure the Globals all rules can access. This method is part of an
	 * ordered method calls that can be done to configure the whole engine. This
	 * is part #2 and can be made before part 1 (Not recommended but possible)
	 * 
	 * @param engineKlass
	 * @param filterName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	public RulesEngineProcessor createEngine(String engineKlass, String filterName)
			throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		Class[] c = { String.class };
		Object[] o = { filterName };
		return (RulesEngineProcessor) Class.forName(engineKlass).getConstructor(c)
				.newInstance(o);
	}

	/*****************************************************************************
	 * Configure the Globals all rules can access. This method is part of an
	 * ordered method calls that can be done to configure the whole engine. This
	 * is part #1.
	 * 
	 * @param _configuration
	 *          Inside this element we expect the globals-list TAG
	 */
	public void configureGlobals(Element _configuration) {
		this.globals = new HashMap<String, Object>();
		Element globalsListElmt = DOMUtils.getElement(_configuration,
				FILTER_CONFIG_GLOBALSLIST_ELEMENT, false);
		if (globalsListElmt != null) {
			NodeList globalsList = DOMUtils.getElements(globalsListElmt,
					FILTER_CONFIG_GLOBAL_ELEMENT);
			for (int i = 0; globalsList.getLength() > i; i++) {
				Element currentGlobal = (Element) globalsList.item(i);
				String klassName = DOMUtils.getAttribute(currentGlobal,
						FILTER_CONFIG_GLOBAL_CLASS, true);
				String name = DOMUtils.getAttribute(currentGlobal, FILTER_CONFIG_GLOBAL_NAME,
						true);
				try {
					Class klass = Class.forName(klassName);
					Object globalInstance = klass.newInstance();
					log.info("Global Parameter Class [" + klass.getCanonicalName()
							+ "] successfully instantiated.");
					try {
						Method method = klass.getMethod("configure", new Class[] { Element.class });
						method.invoke(globalInstance, new Object[] { currentGlobal });
					} catch (NoSuchMethodException nsme) {
						log.warn("Global " + name + " does not have a configuration method.");
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (InvocationTargetException e) {
						throw new RuntimeException(e);
					}
					this.globals.put(DOMUtils.getAttribute(currentGlobal,
							FILTER_CONFIG_GLOBAL_NAME, true), globalInstance);
					log.debug("Created instance of class " + klassName + " named as " + name);
				} catch (ClassNotFoundException e) {
					log.warn("Global " + name + " with class " + klassName
							+ " not found. Ignoring it for now.");
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}

			}
		}

	}

	protected void processRules(Map<String, Object> inputMap) throws Exception {
		List objects = (List) inputMap.get(inputTag);
		inputMap.put(resultsTag, Collections.EMPTY_LIST);
		inputMap.put(resultsTag + "Map", Collections.EMPTY_MAP);
		Map appData = (this.globals != null) ? this.globals : new HashMap();
		for (Iterator i = objects.iterator(); i.hasNext();) {
			
			StatsMapping stats = ProcessingStats.starting(this.assertionEngine.getClass(), "assertObjects()");
			try {
				this.engine.prepare(appData);
			} finally {
				stats.finished();
			}
			
			InvoiceModelObject account = (InvoiceModelObject) i.next();

			long start = System.currentTimeMillis();
			log.debug("ProcessingRules for account [" + account + "]");

			stats = ProcessingStats.starting(this.assertionEngine.getClass(), "assertObjects()");
			try {
				this.assertionEngine.assertObjects(engine, account);
				log.trace("Counter;AssertedFacts;Quantity;;;" + this.assertionEngine.getAssertedFacts());
				
				// Asserts the DWARE request as a FACT
				this.engine.assertFact(new BillcheckoutRequestWrapper(this.req));
			} finally {
				stats.finished();
			}

			stats = ProcessingStats.starting(this.engine.getClass(), "fireRules()");
			try {
				this.engine.fireRules();
			} finally {
				stats.finished();
			}

			List results = this.engine.getResults();
			int size = (results == null) ? 0 : results.size();
			log.info("Filter named " + this.filterName + " processed [" + size
					+ "] results for account [" + account + "]");
			// log messages splitted to keep time control messages in the same format
			long time = (System.currentTimeMillis() - start);
			log.info(i18n.getString("allFilters.endProcessing",
					this.getClass().getSimpleName(), this.filterName, String.valueOf(time)));
			inputMap.put(resultsTag, results);
			inputMap.put(resultsTag + "Map", this.engine.getResultsMap());
		}
		StatsMapping stats = ProcessingStats.starting(this.objProcessor.getClass(), "processElement()");
		try {
			this.objProcessor.processElement(inputMap);
		} finally {
			stats.finished();
		}
	}

	public Object getInput(String filterName) throws ConnectException,
			UnsupportedOperationException {
		return this;
	}

	public Object getOutput(String _filterName, Object _output) throws ConnectException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the Output for this filter.
	 * 
	 */
	public void setOutput(String sourceName, Object objProcessor) {
		this.objProcessor = (ObjectProcessor) objProcessor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.dware.graph.DefaultFilter#prepare(br.com.auster.dware.graph.Request)
	 */
	@Override
	public void prepare(Request req) throws FilterException {
		if ((this.req != null) && (req != null)) {
			if ((req.getTransactionId() != null) && !req.getTransactionId().equals(this.req.getTransactionId())) {
				log.info("Refreshing rules Engine for new transaction: " + req.getTransactionId());
				this.engine.refresh(req.getTransactionId(), this.globals);
			}
		}
		this.req = req;
	}


	/**
	 * @inheritDoc
	 */
	public void processElement(Object map) throws FilterException {
		log.info(i18n.getString("allFilters.startProcessing",
				this.getClass().getSimpleName(), this.filterName));
		if (this.objProcessor != null) {
			StatsMapping stats = ProcessingStats.starting(this.getClass(), "processRules()");
			try {
				Map<String, Object> inputMap = (Map<String, Object>) map;
				this.processRules(inputMap);
			} catch (Exception ex) {
				log.error("Error processing rules", ex);
				throw new FilterException("Error processing rules", ex);
			} finally {
				stats.finished();
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	public void commit() {
		rollback();
	}

	/**
	 * @inheritDoc
	 */
	public void rollback() {
		try {
			if (this.engine != null) {
				this.engine.clear();
			}
		} catch (Exception e) {
			log.warn("Problemas during cleanup: " + e.getMessage());
		}
	}

}
