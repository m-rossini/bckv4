/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
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
 * Created on 12/12/2006
 */
package br.com.auster.billcheckout.rules;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.billcheckout.consequence.Consequence;
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.billcheckout.consequence.telco.hibernate.HibernateTelcoDimensionsFacadeImpl;
import br.com.auster.billcheckout.model.CarrierData;
import br.com.auster.billcheckout.model.ModelLoader;
import br.com.auster.billcheckout.model.cache.CarrierDataCache;
import br.com.auster.common.xml.DOMUtils;

import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.Invoice;


/**
 * @author framos
 * @version $Id$
 *
 */
public class CycleDatesRuleTest extends BaseRuleTest {

	
	
	private String[] RULES = { "src/main/resources/conf/rules/INIT-create-cycledates-objects.drl", 
			                   "src/main/resources/conf/rules/A13-cycle-dates.drl"};
	
	
	/**
	 * IN THE DATABASE:
	 
	BCK_PARM_CUST_TYPE 
	---------------------------------------
		1	 	L		 Large Account
		2		F		 Flat Account
		3		G		 Government Account

	   OBJID   CYCLE_CODE	ACC_TYPE   END_DATE     DUE_DATE    START_DATE  ISSUE_DATE  REF_DATE
       ------------------------------------------------------------------------------------------
		15        BC03 		  NULL     20/1/2007	4/2/2007	21/12/2006	22/1/2007	22/1/2007
	  	14        BC03 			2      20/1/2007	10/2/2007	21/12/2006	22/1/2007	22/1/2007
		13        BC03 			2      20/1/2007	6/2/2007	21/12/2006	22/1/2007	22/1/2007
		12        BC03 			1      20/1/2007	8/2/2007	21/12/2006	22/1/2007	22/1/2007
		11        BC03 			1      20/1/2007	6/2/2007	21/12/2006	22/1/2007	22/1/2007
		10        BC02 		  NULL     15/1/2007	28/1/2007	16/12/2006	17/1/2007	17/1/2007
		9         BC02 			2      15/1/2007	5/2/2007	16/12/2006	17/1/2007	17/1/2007
		8         BC02 			2      15/1/2007	1/2/2007	16/12/2006	17/1/2007	17/1/2007
		7         BC02 			1      15/1/2007	3/2/2007	16/12/2006	17/1/2007	17/1/2007
		6         BC02 			1      15/1/2007	1/2/2007	16/12/2006	17/1/2007	17/1/2007
		5         BC01 		  NULL     10/1/2007	23/1/2007	11/12/2006	12/1/2007	12/1/2007
		4         BC01 			2      10/1/2007	28/1/2007	11/12/2006	12/1/2007	12/1/2007
		3         BC01 			2      10/1/2007	25/1/2007	11/12/2006	12/1/2007	12/1/2007
		2         BC01 			1      10/1/2007	27/1/2007	11/12/2006	12/1/2007	12/1/2007
		1         BC01 			1      10/1/2007	25/1/2007	11/12/2006	12/1/2007	12/1/2007
	 */
	
    protected void createGlobals() throws Exception {
    	super.createGlobals();
    	try {
    		ModelLoader modelLoader = new ModelLoader();
    		modelLoader.configure(DOMUtils.openDocument("thresholds/model-caches.xml", false));
    		workingMemory.setGlobal( "modelLoader", modelLoader );
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail();
    	}
    }	
	
	/**
	 * This account/invoice is OK.
	 * Should be using OBJID = 1 from reference table.
	 */
	public void testAccount1() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			Account act = buildAccount("F", "10-01-2007", "25-01-2007", "12-01-2007");
			this.assertAccount(act);
			this.workingMemory.fireAllRules();
			// running over results list to check if consequences where ok
			assertEquals(0, this.results.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This account/invoice has a wrong ref./issue date
	 * Should be using OBJID = 1 from reference table.
	 */
	public void testAccount2() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			Account act = buildAccount("F", "10-01-2007", "25-01-2007", "13-01-2007");
			this.assertAccount(act);
			this.workingMemory.fireAllRules();
			// running over results list to check if consequences where ok
			HashMap<String, Integer> counters = new HashMap<String, Integer>();
			for (Consequence c : this.results) {
				String k = c.getDescription();
				if (! counters.containsKey(k)) {
					counters.put(k, new Integer(0));
				}
				Integer d = counters.get(k);
				counters.put(k, new Integer(d.intValue() + 1));
			}
			assertEquals(2, this.results.size());
			assertNotNull(counters.get("Data de emissão não coincide com a cadastrada para este ciclo."));
			assertEquals(1, counters.get("Data de emissão não coincide com a cadastrada para este ciclo.").intValue());
			assertNotNull(counters.get("Mês de referência não coincide com o cadastrado para este ciclo."));
			assertEquals(1, counters.get("Mês de referência não coincide com o cadastrado para este ciclo.").intValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This account/invoice is OK but no record in the reference table is defined for it.
	 * This is due to the incorrect dueDate.
	 */
	public void testAccount3() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			Account act = buildAccount("F", "10-01-2007", "22-01-2007", "12-01-2007");
			this.assertAccount(act);
			this.workingMemory.fireAllRules();
			// running over results list to check if consequences where ok
			HashMap<String, Integer> counters = new HashMap<String, Integer>();
			for (Consequence c : this.results) {
				String k = c.getDescription();
				if (! counters.containsKey(k)) {
					counters.put(k, new Integer(0));
				}
				Integer d = counters.get(k);
				counters.put(k, new Integer(d.intValue() + 1));
			}
			assertEquals(4, this.results.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This account/invoice is OK but no record in the reference table is defined for it.
	 * This is due to the incorrect account type.
	 */
	public void testAccount4() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			Account act = buildAccount("X", "10-01-2007", "25-01-2007", "12-01-2007");
			this.assertAccount(act);
			this.workingMemory.fireAllRules();
			// running over results list to check if consequences where ok
			assertEquals(4, this.results.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

		/**
		 * This is another account/invoice OK, but with a different account type
		 *
		 */
	public void testAccount5() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			Account act = buildAccount("L", "10-01-2007", "25-01-2007", "12-01-2007");
			this.assertAccount(act);
			this.workingMemory.fireAllRules();
			// running over results list to check if consequences where ok
			assertEquals(0, this.results.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * This account/invoice is OK and will use a record for NULL account type. OBJID = 10 
	 */
	public void testAccount6() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			Account act = buildAccount("X", "28-01-2007", "16-02-2007", "17-01-2007");
			this.assertAccount(act);
			this.workingMemory.fireAllRules();
			// running over results list to check if consequences where ok
			assertEquals(4, this.results.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * This account/invoice has the wrong ref/issue date. 
	 * Will use a record for NULL account type. OBJID = 10 
	 */
	public void testAccount7() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			Account act = buildAccount("X", "28-01-2007", "16-02-2007", "16-01-2007");
			this.assertAccount(act);
			this.workingMemory.fireAllRules();
			// running over results list to check if consequences where ok
			assertEquals(4, this.results.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}	

	
	// builds the account/invoice information used by each test case
	protected Account buildAccount(String _type, String _cutDate, String _dueDate, String _issueDate) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		
		Account acc = new Account();
		acc.setAccountName("John Smith");
		acc.setAccountNumber("10011001");
		acc.setAccountState("SP");
		acc.setAccountType(_type);
		
		Date cutDate = formatter.parse(_cutDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(cutDate);
		cal.add(Calendar.MONTH, -1);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date startDate = cal.getTime();
				
		Invoice inv = new Invoice();
		inv.setCycleStartDate(startDate);		
		inv.setCycleEndDate(cutDate);
		inv.setIssueDate(formatter.parse(_issueDate));
		inv.setDueDate(formatter.parse(_dueDate));

		acc.addInvoice(inv);
		return acc;
	}
}
