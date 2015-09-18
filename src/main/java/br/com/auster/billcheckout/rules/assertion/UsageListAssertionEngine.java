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
import br.com.auster.om.invoice.UsageDetail;

/**
 * This class corrects the UsageAssertionEngine Behavior and gives
 * a direct path for usage assertion
 *
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class UsageListAssertionEngine implements AssertionEngine {

	private static Logger	log	= Logger.getLogger(UsageListAssertionEngine.class);
	private long	assertedFacts;
	
	
	/**
	 * 
	 * @param engine
	 * @param om
	 * @see br.com.auster.billcheckout.rules.assertion.AssertionEngine#assertObjects(br.com.auster.billcheckout.rules.RulesEngineProcessor, br.com.auster.om.invoice.InvoiceModelObject)
	 */
	public void assertObjects(RulesEngineProcessor engine, InvoiceModelObject om) {
		try {
			if (om instanceof Account) {
				Account account = (Account) om;
				engine.assertFact(account);
				assertedFacts++;
				Collection invoices = account.getInvoices().values();
				for (Iterator invIt = invoices.iterator(); invIt.hasNext();) {
					Invoice inv = (Invoice) invIt.next();
					engine.assertFact(inv);
					assertedFacts++;
					for (Iterator itL1 = inv.getSections().iterator();itL1.hasNext();) {
						//L1 Sections are Section just below Invoice.
						Section l1Section = (Section) itL1.next();
						for (Iterator itL2=l1Section.getSubSections().iterator();itL2.hasNext();) {
							//L2 Section are Contract Info Sections
							Section l2Section = (Section) itL2.next();
							for (Iterator itL3=l2Section.getSubSections().iterator();itL3.hasNext();) {
								//L3 Are SubSections
								Section l3Section = (Section) itL3.next();
								for (Iterator itUsg=l3Section.getDetails().iterator();itUsg.hasNext();) {
									engine.assertFact( (UsageDetail)itUsg.next());
									assertedFacts++;
								}
							}
						}
					}
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
