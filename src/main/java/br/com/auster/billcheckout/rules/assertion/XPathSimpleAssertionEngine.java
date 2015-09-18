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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import br.com.auster.billcheckout.rules.RulesEngineProcessor;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.om.invoice.InvoiceModelObject;

/**
 * This class receives a configuration of XPaths and looks for them on
 * the object model.
 * The list of xpaths are in an external file of the form:
 * <ROOT> <xpath expression="xpath-expression" ....
 * 
 * The xpath TAG can happen as many times needed.
 * 
 * This class will act without context except for the root element of 
 * the object model.
 * 
 * All paths on the list are evaluated on this root object context.
 * 
 * 
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class XPathSimpleAssertionEngine implements AssertionEngine {

	private Logger							log							= Logger
																									.getLogger(XPathSimpleAssertionEngine.class);

	public static final String	ATTR_XPATH_FILE	= "xpath-file";
	private static final String	ELMT_XPATH			= "xpath";
	private static final String	ATTR_EXPRESSION	= "expression";

	private String							fileName;
	private List<String>				xpathList;

	private long	assertedFacts;

	/**
	 * 
	 * @param engine
	 * @param om
	 * @see br.com.auster.billcheckout.rules.assertion.AssertionEngine#assertObjects(br.com.auster.billcheckout.rules.RulesEngineProcessor,
	 *      br.com.auster.om.invoice.InvoiceModelObject)
	 */
	public void assertObjects(RulesEngineProcessor engine, InvoiceModelObject om) {
		JXPathContext context = JXPathContext.newContext(om);
		context.setLenient(true);

		for (Iterator<String> it = this.xpathList.iterator(); it.hasNext();) {
			String expr = it.next();
			log.trace("About to process xpath [" + expr + "]");

			Iterator results = context.iterate(expr);
			if (results == null) {
				log.trace("Null results for XPATH:[" + expr + "]");
				continue;
			}

			try {
				while (results.hasNext()) {
					engine.assertFact(results.next());
					assertedFacts++;
				}
			} catch (Exception e) {
				log.fatal("Unable to assert facts.");
				throw new RuntimeException(e);
			}
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
			int qt = elements.getLength();

			this.xpathList = new ArrayList<String>();

			for (int i = 0; i < qt; i++) {
				Element item = (Element) elements.item(i);
				this.xpathList.add(DOMUtils.getAttribute(item, ATTR_EXPRESSION, true));
			}

			log.trace("List of XPATHS:" + this.xpathList);
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
