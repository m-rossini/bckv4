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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import br.com.auster.billcheckout.rules.RulesEngineProcessor;
import br.com.auster.om.invoice.BarCode;
import br.com.auster.om.invoice.InvoiceModelObject;

/**
 * This class insert objects into working memory using reflection. All objects
 * belonging to Object Model will be asserted by this class.
 * 
 * An object is said to be belong to object model if: It extends
 * InvoiceModelObject
 * 
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class ReflectiveInvoiceAssertionEngine implements AssertionEngine {

	private Logger	log	= Logger.getLogger(ReflectiveInvoiceAssertionEngine.class);

	private long assertedFacts=0L;
	/**
	 * Provides the concrete implementation
	 * 
	 * @param engine
	 * @param om
	 * @see br.com.auster.billcheckout.rules.assertion.AssertionEngine#assertObjects(br.com.auster.billcheckout.rules.RulesEngineProcessor,
	 *      br.com.auster.om.invoice.InvoiceModelObject)
	 */
	public void assertObjects(RulesEngineProcessor engine, InvoiceModelObject om) {
		log.debug("Asserting =>[" + om + "] of the Type " + om.getClass().getName());
		try {
			engine.assertFact(om);
			assertedFacts++;
			Method[] methods = om.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().startsWith("get")
						&& ((Collection.class.isAssignableFrom(methods[i].getReturnType())) || (Map.class
								.isAssignableFrom(methods[i].getReturnType())))) {
					Collection col = null;
					if (methods[i].getReturnType().isAssignableFrom(Map.class)) {
						Map map = (Map) methods[i].invoke(om, (Object[]) null);
						if (map != null) {
							col = map.values();
						}
					} else {
						col = (Collection) methods[i].invoke(om, (Object[]) null);
					}
					if (col != null) {
						for (Iterator it = col.iterator(); it.hasNext();) {
							Object entry = it.next();
							if (entry instanceof InvoiceModelObject) {
								assertObjects(engine, (InvoiceModelObject) entry);
							}
						}
					}
				} else if (methods[i].getName().startsWith("get")
						&& BarCode.class.isAssignableFrom(methods[i].getReturnType())) {
					engine.assertFact((InvoiceModelObject) methods[i].invoke(om, (Object[]) null));
					assertedFacts++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This implementation does not requires configuration and so, does nothing.
	 * 
	 * @param config
	 * @see br.com.auster.billcheckout.rules.assertion.AssertionEngine#configure(org.w3c.dom.Element)
	 */
	public void configure(Element config) {
		//DOES NOTHING
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