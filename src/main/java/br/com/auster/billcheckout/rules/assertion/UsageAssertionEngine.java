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

import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import br.com.auster.billcheckout.rules.RulesEngineProcessor;
import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.InvoiceModelObject;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.invoice.SectionDetail;
import br.com.auster.om.invoice.UsageDetail;


/**
 * This class is intended to assert only usage in working memory.
 * It has a huge mistake, because it is not multi purpose and it is hard coded
 * for one specific scenario, therefor it is deprecated and we stringly
 * recommend to use the @see XPathAssertionEngine to achieve the same and 
 * highly configurated usage.
 * 
 * @deprecated
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class UsageAssertionEngine implements AssertionEngine {

	private static Logger	log	= Logger.getLogger(UsageAssertionEngine.class);
	private long	assertedFacts;
	/**
	 * See class description
	 * @deprecated
	 * @param engine
	 * @param om
	 * @see br.com.auster.billcheckout.rules.assertion.AssertionEngine#assertObjects(br.com.auster.billcheckout.rules.RulesEngineProcessor, br.com.auster.om.invoice.InvoiceModelObject)
	 */
	public void assertObjects(RulesEngineProcessor engine, InvoiceModelObject om) {
		try {
			int counter = 0;
			if (om instanceof Account) {
				Account account = (Account) om;
				engine.assertFact(account);
				assertedFacts++;
				Collection invoices = account.getInvoices().values();
				for (Iterator invIt = invoices.iterator(); invIt.hasNext();) {
					Invoice inv = (Invoice) invIt.next();
					engine.assertFact(inv);
					assertedFacts++;
					for (Iterator secIt1 = inv.getSections().iterator(); secIt1.hasNext();) {
						Section topSection = (Section) secIt1.next();
						if ("300D".equals(topSection.getTag())) {
							for (Iterator secIt2 = topSection.getSubSections().iterator(); secIt2.hasNext();) {
								Section sbsSection = (Section) secIt2.next();
								if ("399D".equals(sbsSection.getTag())) {
									for (Iterator secIt3 = sbsSection.getSubSections().iterator(); secIt3.hasNext();) {
										Section usgSection = (Section) secIt3.next();
										for (Iterator usageIt = usgSection.getDetails().iterator(); usageIt.hasNext();) {
											SectionDetail detail = (SectionDetail) usageIt.next();
											if (detail instanceof UsageDetail) {
												engine.assertFact(detail);
												assertedFacts++;
												counter++;
											}
										}
									}
								}
							}
						}
					}
				}
				log.info("Total Asserted Usages:" + counter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	/**
	 * This implementation does not requires configuration and so, does nothing.
	 * 
	 * @deprecated
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
