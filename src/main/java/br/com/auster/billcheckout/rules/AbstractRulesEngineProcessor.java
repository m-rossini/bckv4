/*
 * Copyright (c) 2004-2007 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 15/03/2007
 */
package br.com.auster.billcheckout.rules;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.agent.AgentEventListener;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.event.AgendaEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.rule.Package;
import org.w3c.dom.Element;

import br.com.auster.billcheckout.exceptions.BillcheckoutException;
import br.com.auster.common.interfaces.ResourceHolder;
import br.com.auster.common.io.IOEnumerationCollectionWrapper;
import br.com.auster.common.io.IOUtils;
import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * @author pvieira
 * 
 */
public abstract class AbstractRulesEngineProcessor implements
		RulesEngineProcessor {

	private static final Logger log = Logger
			.getLogger(AbstractRulesEngineProcessor.class);

	private static final I18n i18n = I18n
			.getInstance(AbstractRulesEngineProcessor.class);

	public static final String ELMT_RULES_ENGINE = "rules-engine";
	public static final String ATTR_MEMORY_LISTENER = "memoryListenerClass";
	public static final String ATTR_AGENDA_LISTENER = "agendaListenerClass";
	public static final String ATTR_RFLOW_LISTENER = "flowListenerClass";
	public static final String ATTR_AGENT_LISTENER = "agentListenerClass";
	public static final String ELMT_LISTENERS = "listeners";
	public static final String ELMT_RULEBASE_CONFIG = "RuleBaseConfig";
	public static final String ELMT_RULEAGENT_CONFIG = "RuleAgentConfig";
	public static final String ELMT_PACKAGEBUILDER_CONFIG = "PackageBuilderConfig";
	public static final String ATTR_PROPERTIES = "propertiesFile";
	public static final String ELMT_RULES_PACKAGES = "RulePackages";
	public static final String ELMT_RULE_PACKAGE = "RulePackage";
	public static final String ATTR_DRL_MASK = "drlMask";
	public static final String ATTR_DSL_MASK = "dslMask";
	public static final String ATTR_DRL_NAME = "drlName";
	public static final String ATTR_DSL_NAME = "dslName";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_BASE = "base";
	public static final String CTE_RESULTS_LIST = "results";
	public static final String CTE_RESULTS_MAP = "resultsMap";

	private static final ReentrantLock rulesListLock = new ReentrantLock();

	private String name;

	// Rule Engine Objects Properties
	protected Properties ruleBaseProps;
	protected Properties packageBuilderProps;

	protected RuleBase ruleBase = null;
	protected Map<String, RulePackageSourceHook> rulesList = null;
	protected Map appData;

	boolean sequential;

	// The Rule Engine Listeners Instances
	private AgentEventListener agentEventListener;
	private WorkingMemoryEventListener workingMemoryListener;
	private AgendaEventListener agendaEventListener;
	private RuleFlowEventListener ruleFlowEventListener;

	protected RuleBaseConfiguration rbConf;

	protected StatelessSession stateLessSession;

	protected StatefulSession stateFullSession;

	private ArrayList factList;

	protected StatelessSessionResult stateLessResults;

	private Map<String, Object> globals;

	// TODO Write Logging
	protected AbstractRulesEngineProcessor(String name) {
		this.name = name;
	}

	/**
	 * Builds <code>rulesList</code> from a rules source. It can return null
	 * or empty list, meaning that there are no rules source. This can happen
	 * when someone is extending this class for compiled rules.
	 * 
	 * @param config
	 * @return List of rules
	 */
	protected abstract Map<String, RulePackageSourceHook> buildRulesList(
			Element config);

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.common.rules.RulesEngineProcessor#configure(org.w3c.dom.Element)
	 */
	public void configure(Element config) throws Exception {
		try {
			configureListeners(config);
		} catch (InstantiationException e1) {
			log
					.error(
							"Unable to create Listener.Proceeding without it.See log messages for additional info.",
							e1);
		} catch (IllegalAccessException e1) {
			log
					.error(
							"Unable to access Listener.Proceeding without it.See log messages for additional info.",
							e1);
		} catch (ClassNotFoundException e1) {
			log
					.error(
							"Listener does not exist.Proceeding without it.See log messages for additional info.",
							e1);
		}

		try {
			this.ruleBaseProps = getProperties(config, ELMT_RULEBASE_CONFIG,
					ATTR_PROPERTIES);
			rbConf = new RuleBaseConfiguration(this.ruleBaseProps);
			this.sequential = rbConf.isSequential();

		} catch (IOException e) {
			log.fatal("Unable to load from CP the Rule Base properties file.");
			throw new RuntimeException(e);
		}

		try {
			this.packageBuilderProps = getProperties(config,
					ELMT_PACKAGEBUILDER_CONFIG, ATTR_PROPERTIES);
		} catch (IOException e) {
			log
					.fatal("Unable to load from CP the Package Builder properties file.");
			throw new RuntimeException(e);
		}

		this.rulesList = this.buildRulesList(config);
		if ((rulesList == null) || (rulesList.size() == 0)) {
			BillcheckoutException bckEX = new BillcheckoutException(i18n
					.getString("abstractRuleEngine.noRules"));
			bckEX.setErrorCode(1);
			throw bckEX;
		}
	}

	/**
	 * Utility method to get properties from a file. It first tries to open a
	 * file stream from path given, if not found, it tries to load from class
	 * path.
	 * 
	 * And if not found, unpredictable errors can occur.
	 * 
	 * @param config
	 * @throws IOException
	 */
	protected Properties getProperties(Element config, String elementName,
			String attributeName) throws IOException {
		Element element = DOMUtils.getElement(config, elementName, false);
		Properties results = new Properties();
		if (element != null) {
			String prop = DOMUtils.getAttribute(element, attributeName, true);
			try {
				results.load(IOUtils.openFileForRead(prop));
			} catch (FileNotFoundException e) {
				results.load(this.getClass().getResourceAsStream(prop));
			} catch (IOException e) {
				log.fatal("Unable to load properties file.");
				throw new RuntimeException(e);
			}
		}
		return results;
	}

	/**
	 * This method configures all listeners.
	 * 
	 * Currently supported are: Working Memory Agenda Rule Flow Agent
	 * 
	 * It expects the full class name of the listeners. Once found in the
	 * configuration it will try to instantiate it and save them as instance
	 * variables of this class. If not possible this method WILL throw the
	 * exceptions
	 * 
	 * @param config
	 *            We expect a TAG named dw:listeners. Inside it the appropriated
	 *            attributes names.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	protected void configureListeners(Element config)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Element element = DOMUtils.getElement(config, ELMT_LISTENERS, false);
		if (element != null) {
			String wmListener = DOMUtils.getAttribute(element,
					ATTR_MEMORY_LISTENER, false);
			String agListener = DOMUtils.getAttribute(element,
					ATTR_AGENDA_LISTENER, false);
			String rfListener = DOMUtils.getAttribute(element,
					ATTR_RFLOW_LISTENER, false);
			String atListener = DOMUtils.getAttribute(element,
					ATTR_AGENT_LISTENER, false);

			if (wmListener != null && !"".equals(wmListener)) {
				workingMemoryListener = ((Class<WorkingMemoryEventListener>) Class
						.forName(wmListener)).newInstance();
			}

			if (agListener != null && !"".equals(agListener)) {
				agendaEventListener = ((Class<AgendaEventListener>) Class
						.forName(agListener)).newInstance();
			}

			if (rfListener != null && !"".equals(rfListener)) {
				ruleFlowEventListener = ((Class<RuleFlowEventListener>) Class
						.forName(rfListener)).newInstance();
			}

			if (atListener != null && !"".equals(atListener)) {
				agentEventListener = ((Class<AgentEventListener>) Class
						.forName(atListener)).newInstance();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.common.rules.RulesEngineProcessor#init(java.util.Map)
	 */
	public void init(Map properties) throws Exception {
		log.debug("Rulebase about to initialize with properties: " + properties);
		this.ruleBase = this.getRuleBase();
		this.globals = properties;
		addPackages(properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.common.rules.RulesEngineProcessor#prepare(java.util.Map)
	 */
	public void prepare(Map appData) throws Exception {
		// EventManager em = null;
		if (this.sequential) {
			this.stateLessSession = this.ruleBase.newStatelessSession();
			// Add Listeners
			if (this.agendaEventListener != null) {
				stateLessSession.addEventListener(this.agendaEventListener);
			}
			if (this.ruleFlowEventListener != null) {
				stateLessSession.addEventListener(this.ruleFlowEventListener);
			}
			if (this.workingMemoryListener != null) {
				stateLessSession.addEventListener(this.workingMemoryListener);
			}
		} else {
			this.stateFullSession = this.ruleBase.newStatefulSession();
			// Add Listeners
			if (this.agendaEventListener != null) {
				stateFullSession.addEventListener(this.agendaEventListener);
			}
			if (this.ruleFlowEventListener != null) {
				stateFullSession.addEventListener(this.ruleFlowEventListener);
			}
			if (this.workingMemoryListener != null) {
				stateFullSession.addEventListener(this.workingMemoryListener);
			}
		}

		// Add configured Global Variables
		for (Iterator<Map.Entry<String, ?>> it = appData.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<String, ?> entry = (Entry<String, ?>) it.next();
			if (sequential) {
				this.stateLessSession.setGlobal(entry.getKey(), entry
						.getValue());
			} else {
				this.stateFullSession.setGlobal(entry.getKey(), entry
						.getValue());
			}
		}

		// Add Injected Globals
		if (sequential) {
			this.stateLessSession.setGlobal(CTE_RESULTS_LIST, new ArrayList());
			this.stateLessSession.setGlobal(CTE_RESULTS_MAP, new HashMap());
		} else {
			this.stateFullSession.setGlobal(CTE_RESULTS_LIST, new ArrayList());
			this.stateFullSession.setGlobal(CTE_RESULTS_MAP, new HashMap());
		}

	}

	public QueryResults getQueryResults(String name) {
		if (sequential) {
			return this.stateLessResults.getQueryResults(name);
		}
		return this.stateFullSession.getQueryResults(name);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.common.rules.RulesEngineProcessor#assertFact(java.lang.Object)
	 */
	public void assertFact(Object fact) {
		if (fact == null) {
			return;
		}

		if (sequential) {
			if (this.factList == null) {
				this.factList = new ArrayList();
			}
			this.factList.add(fact);
		} else {
			this.stateFullSession.insert(fact);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.common.rules.RulesEngineProcessor#fireRules()
	 */
	public void fireRules() {
		// TODO Provide Configuration for Agenda Filter
		// TODO Write and test a Basic Agenda Filter
		// TODO Check the stateLessResults returned by fire rules
		if (sequential) {
			this.stateLessResults = this.stateLessSession
					.executeWithResults(this.factList);
		} else {
			this.stateFullSession.fireAllRules();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.common.rules.RulesEngineProcessor#clear()
	 */
	public void clear() {
		this.appData = null;
		if (this.globals != null) {
			for (Entry<String, Object> entry : globals.entrySet()) {
				Object global = entry.getValue();
				if (global instanceof ResourceHolder) {
					ResourceHolder resourceHolder = (ResourceHolder) global;
					try {
						resourceHolder.releaseResources();
					} catch (Exception e) {
						log.warn("Error releasing resources of global named " + entry.getKey(), e);
					}
				}
			}
			this.globals = null;
		}
		if (sequential) {
			this.stateLessSession = null;
			this.factList = null;
		} else {
			if (stateFullSession != null) {
				this.stateFullSession.dispose();
			}
			this.stateFullSession = null;
		}
	}

	/***************************************************************************
	 * 
	 * This method creates a Rule Base. The default implementation creates one
	 * based upon the configuration of rule base properties as configured.
	 * 
	 * @see configure(Element config). Althrought it can be overriden to create
	 *      a rule base in other ways, like using rule agents, as
	 *      CompileRulesEngineProcessor does.
	 * 
	 * @return A RuleBase
	 * @throws IOException
	 */
	protected RuleBase getRuleBase() throws IOException {
		RuleBase rb = RuleBaseFactory.getInstance().newRuleBase(rbConf);
		return rb;
	}

	/**
	 * This method returns the injected Global results. For now it works only
	 * with state full session, once state less has no way to getting globals
	 * back.
	 * 
	 * @return A List with resultsMap Global Variable.
	 * 
	 * @see br.com.auster.billcheckout.rules.RulesEngineProcessor#getResults()
	 */
	public List getResults() {
		return (List) ((sequential) ? Collections.emptyList()
				: this.stateFullSession.getGlobal(CTE_RESULTS_LIST));
	}

	/**
	 * This method returns the injected Global resultsMap. For now it works only
	 * with state full session, once state less has no way to getting globals
	 * back.
	 * 
	 * @return A Map with resultsMap Global Variable.
	 * 
	 * @see br.com.auster.billcheckout.rules.RulesEngineProcessor#getResultsMap()
	 */
	public Map getResultsMap() {
		return (Map) ((sequential) ? Collections.emptyMap()
				: this.stateFullSession.getGlobal(CTE_RESULTS_MAP));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.common.rules.RulesEngineProcessor#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This method is a convenience method for adding packages. It is called
	 * with globals, and expects the DRL (Rule) list to be already populated.
	 * The Structure of the mentioned DRL list shall be: Map<String, List<File>>
	 * Where: String is a Unique Name that we suppose to be the package. List of
	 * DRL File Objects to be inserted.
	 * 
	 * This method will create a new Package Builder for each entry in the Map,
	 * so ALL the files in the list for a given Map Entry MUST belong to the
	 * same package.
	 * 
	 * All DRL Files MUST have at least on package declared on it.
	 * 
	 * Different map entries can have the same package.
	 * 
	 * @param globals
	 * @throws Exception
	 */
	protected void addPackages(Map globals) throws Exception {
		if (ruleBase == null) {
			throw new IllegalStateException("Rule base is null.");
		}
		log.debug("RuleSelection: Creating new PackageBuilderConfiguration");
		
		PackageBuilderConfiguration conf = new PackageBuilderConfiguration(
				this.packageBuilderProps);

		for (Iterator<Map.Entry<String, RulePackageSourceHook>> it = this.rulesList
				.entrySet().iterator(); it.hasNext();) {

			PackageBuilder builder = new PackageBuilder(conf);
			Entry<String, RulePackageSourceHook> entry = it.next();
			log.trace("Getting files for package:" + entry.getKey());

			List<File> drlList = entry.getValue().getDrlList();
			if (( drlList == null) || drlList.size() == 0) {
				//If no DRL found for the package, then ignore it.
				log.error(i18n.getString("drl.empty.package", entry.getKey()));
				continue;
			}
			IOEnumerationCollectionWrapper enumDRL = new IOEnumerationCollectionWrapper(drlList);
			enumDRL.setExpectedType(IOEnumerationCollectionWrapper.TYPE_INPUTSTREAM);

			IOEnumerationCollectionWrapper enumDSL = new IOEnumerationCollectionWrapper(entry.getValue().getDslList());
			enumDRL.setExpectedType(IOEnumerationCollectionWrapper.TYPE_INPUTSTREAM);

			SequenceInputStream seqDRL = new SequenceInputStream(enumDRL);
			BufferedInputStream bufDRL = new BufferedInputStream(seqDRL);
			InputStreamReader readerDRL = new InputStreamReader(bufDRL);

			if (enumDSL.getSize() > 0) {
				SequenceInputStream seqDSL = new SequenceInputStream(enumDSL);
				BufferedInputStream bufDSL = new BufferedInputStream(seqDSL);
				InputStreamReader readerDSL = new InputStreamReader(bufDSL);
				builder.addPackageFromDrl(readerDRL, readerDSL);
				readerDSL.close();
				bufDSL.close();
				seqDSL.close();
			} else {
				builder.addPackageFromDrl(new BufferedReader(readerDRL));
			}

			readerDRL.close();
			bufDRL.close();
			seqDRL.close();

			Package pack = builder.getPackage();

			// Add Globals to the Packages, and with that we are done with
			// globals....
			for (Iterator<Map.Entry<String, ?>> itGlobal = globals.entrySet()
					.iterator(); itGlobal.hasNext();) {
				Entry<String, ?> entryGlobal = itGlobal.next();
				pack.addGlobal(entryGlobal.getKey(), entryGlobal.getValue()
						.getClass());
			}

			// Add injected Globals
			pack.addGlobal(CTE_RESULTS_LIST, List.class);
			pack.addGlobal(CTE_RESULTS_MAP, Map.class);

			ruleBase.addPackage(pack);
		}

		return;
	}

	public void refresh(String reqId, Map<String, Object> map) {
			// do nothing
	}

}
