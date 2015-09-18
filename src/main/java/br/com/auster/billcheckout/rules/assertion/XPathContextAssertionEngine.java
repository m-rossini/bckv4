/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
 * Created on 28/08/2007
 */
package br.com.auster.billcheckout.rules.assertion;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import br.com.auster.billcheckout.rules.RulesEngineProcessor;
import br.com.auster.common.stats.ProcessingStats;
import br.com.auster.common.stats.StatsMapping;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.om.invoice.InvoiceModelObject;

/**
 * This class asserts OM Objects to Working Memories using XPATH.
 * XPATHs are implemented using apache commnons-jxpath.
 * 
 * The XPATH configuration passed during configure method and it is not released.
 * The xpaths must be contextualized, meaning that the next evaluation will be done
 * taking the CURRENT graph of objects state/positioning.
 * This has the advantage of not allowing the jxpath getting into an endless loop,
 * when there are circular references.
 * 
 *  Once OM in terms of structure (For instance in a XML Schema) is quite simple,
 *  there is not hurt on configuring the whole tree.
 * 
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class XPathContextAssertionEngine implements AssertionEngine {

	private Logger							log							= Logger
																									.getLogger(XPathContextAssertionEngine.class);

	public static final String	ATTR_XPATH_FILE	= "xpath-file";
	public static final String	ELMT_XPATH			= "xpath";
	public static final String	ATTR_EXPRESSION	= "expression";
	public static final String	ATTR_ASSERT	= "assert";

	private String							fileName;
	private NodeList						elements;

	private long	assertedFacts;

	/**
	 * 
	 * @param engine
	 * @param om
	 * @see br.com.auster.billcheckout.rules.assertion.AssertionEngine#assertObjects(br.com.auster.billcheckout.rules.RulesEngineProcessor,
	 *      br.com.auster.om.invoice.InvoiceModelObject)
	 */
	public void assertObjects(RulesEngineProcessor engine, InvoiceModelObject om) {
		selectObjects(engine, om, this.elements);
	}

	/**
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param engine
	 * @param om
	 * @param elmt
	 */
	protected void selectObjects(RulesEngineProcessor engine, InvoiceModelObject om,
			NodeList elmts) {

		int qt = elmts.getLength();
		if (qt==0) {return;}
		
		JXPathContext context = JXPathContext.newContext(om);
		context.setLenient(true);
		log.trace("Context set in filter for object:" + om);

		for (int i = 0; i < qt; i++) {
			// Look for expression attr, which is optional.
			Element currentElmt = (Element) elmts.item(i);
			String expr = DOMUtils.getAttribute(currentElmt, ATTR_EXPRESSION, true);
			boolean assertExpression = DOMUtils.getBooleanAttribute(currentElmt, ATTR_ASSERT, true);
			Iterator results = context.iterate(expr);
			try {
				log.trace("Iterating over results fo expression:[" + expr + "]");
				handleIteration(results,engine,currentElmt,assertExpression);
			} catch (Exception e) {
				log.fatal("Unable to assert facts.");
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * </p>
	 * @param engine 
	 * @param currentElmt 
	 * @param assertExpression 
	 * @throws Exception 
	 * 
	 */
	private void handleIteration(Iterator results, RulesEngineProcessor engine, Element currentElmt, boolean assertExpression ) throws Exception {
		while (results.hasNext()) {
			Object obj = results.next();
			InvoiceModelObject tobeAsserted=null;
			if (obj instanceof InvoiceModelObject) {
				tobeAsserted = (InvoiceModelObject) obj;						
			} else if (obj instanceof Map) {
				Map map = (Map) obj;
				handleIteration(map.values().iterator(), engine, currentElmt, assertExpression);
			}
			
			if (assertExpression) {
				StatsMapping stats = ProcessingStats.starting(engine.getClass(), "assertFact()");
				try {
					engine.assertFact(tobeAsserted);
					assertedFacts++;
				} finally {
					stats.finished();
				}
			}
			selectObjects(engine, tobeAsserted, DOMUtils.getElements(currentElmt,
					ELMT_XPATH));
		}
		
	}

	/**
	 * Look for a file where all xpath for looking for to be inserted objects will
	 * be found.
	 * 
	 * @param config
	 * @see br.com.auster.billcheckout.rules.assertion.AssertionEngine#configure(org.w3c.dom.Element)
	 */
	public void configure(Element config) {
		this.fileName = DOMUtils.getAttribute(config, ATTR_XPATH_FILE, true);

		try {
			Element document = DOMUtils.openDocument(fileName, false);
			NodeList elements = DOMUtils.getElements(document, ELMT_XPATH);
			this.elements = elements;
		} catch (ParserConfigurationException e) {
			log.fatal("Unable to read xpaths file.");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (SAXException e) {
			log.fatal("Unable to read xpaths file.");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.fatal("Unable to read xpaths file.");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (GeneralSecurityException e) {
			log.fatal("Unable to read xpaths file.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return the value of a attribute <code>assertedFacts</code>.
	 * @return return the value of <code>assertedFacts</code>.
	 */
	public long getAssertedFacts() {
		return this.assertedFacts;
	}

	/**
	 * Set the value of attribute <code>assertedFacts</code>.
	 * @param assertedFacts
	 */
	public void setAssertedFacts(long assertedFacts) {
		this.assertedFacts = assertedFacts;
	}
}
