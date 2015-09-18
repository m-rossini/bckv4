package br.com.auster.billcheckout.rules;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import br.com.auster.billcheckout.consequence.Consequence;
import br.com.auster.billcheckout.thresholds.ThresholdLoader;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;

/**
 * @author framos
 * @version $Id$
 *
 */
public class NFThresholdRule extends BaseRuleTest {

	
	private String[] RULES = { "src/main/resources/conf/rules/INIT-create-dimension-objects.drl",
			                   "src/main/resources/conf/rules/A11-nf-threshold.drl"};
	
	
	
	
    protected void createGlobals() throws Exception {
    	super.createGlobals();
    	// load database driver class
		Class.forName("oracle.jdbc.driver.OracleDriver");
		// adding threshold loader appData into working memory
    	ThresholdLoader thresholdLoader = new ThresholdLoader();
    	thresholdLoader.configure(DOMUtils.openDocument("thresholds/threshold-caches.xml", false));
    	this.workingMemory.setGlobal("thresholdLoader", thresholdLoader);
    	
    	this.insertInThresholdTable();
   	}	
	
    protected void insertInThresholdTable() throws Exception {
    	Connection conn = null;
    	Statement stmt = null;
    	try {
    		conn = DriverManager.getConnection("jdbc:oracle:thin:@mccoy:1521:TEST01", "test", "test");
    		stmt = conn.createStatement();
    		stmt.executeUpdate("delete from bck_parm_nf_threshold");
    		stmt.executeUpdate("insert into bck_parm_nf_threshold values (1, 'Y', 30, 3000, null, null, null, null)"); 
    		stmt.executeUpdate("insert into bck_parm_nf_threshold values (2, 'F', 50, 1800, null, null, null, null)");
    		conn.commit();
    	} finally {
    		if (stmt != null) { stmt.close(); }
    		if (conn != null) { conn.close(); }
    	}
    }
    
	public void testAccount1() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			double[] ld = {400.00};
			Account acc = buildAccount(200.00, ld);
			this.assertAccount(acc);
			this.workingMemory.fireAllRules();
			assertEquals(0, this.results.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testAccount2() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			Account acc = buildAccount(200.00, null);
			this.assertAccount(acc);
			this.workingMemory.fireAllRules();
			assertEquals(0, this.results.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testAccount3() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			double[] ld = {2000.00, 400.00, 24.00};
			Account acc = buildAccount(2000.00, ld );
			this.assertAccount(acc);
			this.workingMemory.fireAllRules();
			Map<String, Integer> info = new HashMap<String, Integer>();
			for (Consequence c : this.results) {
				if (info.get(c.getRelatedRule().getCode()) == null) {
					info.put(c.getRelatedRule().getCode(), new Integer(0));
				}
				Integer it = info.get(c.getRelatedRule().getCode());
				info.put(c.getRelatedRule().getCode(), new Integer(it.intValue()+1));
				assertEquals("Longa Distância", c.getAttributes().getAttributeValue1());
			}
			assertEquals(2, this.results.size());
			assertNotNull(info.get("A11-1"));
			assertEquals(1, info.get("A11-1").intValue());
			assertNotNull(info.get("A11-2"));
			assertEquals(1, info.get("A11-2").intValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testAccount4() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			double[] ld = { 400.00, 240.00 };
			Account acc = buildAccount(4000.00, ld );
			this.assertAccount(acc);
			this.workingMemory.fireAllRules();
			assertEquals(1, this.results.size());
			for (Consequence c : this.results) {
				assertEquals("A11-1", c.getRelatedRule().getCode());
				assertEquals("Local", c.getAttributes().getAttributeValue1());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testAccount5() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			double[] ld = {25.00, 300.00};
			Account acc = buildAccount(-1.00, ld);
			this.assertAccount(acc);
			this.workingMemory.fireAllRules();
			assertEquals(1, this.results.size());
			for (Consequence c : this.results) {
				assertEquals("A11-2", c.getRelatedRule().getCode());
				assertEquals("Longa Distância", c.getAttributes().getAttributeValue1());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	public void testAccount6() {
		try {
			// firing rules
			this.startupRuleEngine(RULES);
			double[] ld = {4000.00, 400.00, 24.00};
			Account acc = buildAccount(4000.00, ld );
			this.assertAccount(acc);
			this.workingMemory.fireAllRules();
			Map<String, Integer> info = new HashMap<String, Integer>();
			for (Consequence c : this.results) {
				if (info.get(c.getRelatedRule().getCode()) == null) {
					info.put(c.getRelatedRule().getCode(), new Integer(0));
				}
				Integer it = info.get(c.getRelatedRule().getCode());
				info.put(c.getRelatedRule().getCode(), new Integer(it.intValue()+1));
			}
			assertEquals(3, this.results.size());
			assertNotNull(info.get("A11-1"));
			assertEquals(2, info.get("A11-1").intValue());
			assertNotNull(info.get("A11-2"));
			assertEquals(1, info.get("A11-2").intValue());
			info.clear();
			for (Consequence c : this.results) {
				if (info.get(c.getAttributes().getAttributeValue1()) == null) {
					info.put(c.getAttributes().getAttributeValue1(), new Integer(0));
				}
				Integer it = info.get(c.getAttributes().getAttributeValue1());
				info.put(c.getAttributes().getAttributeValue1(), new Integer(it.intValue()+1));
			}
			assertNotNull(info.get("Local"));
			assertEquals(1, info.get("Local").intValue());
			assertNotNull(info.get("Longa Distância"));
			assertEquals(2, info.get("Longa Distância").intValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}	
	
	// builds the account/invoice information used by each test case
	protected Account buildAccount(double _localAmount, double[] _nfAmounts) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		
		Account acc = new Account();
		acc.setAccountName("John Smith");
		acc.setAccountNumber("10011001");
		acc.setAccountState("SP");
		acc.setAccountType("F");
		
		Invoice inv = new Invoice();
		inv.setCycleCode("00");
		inv.setCycleStartDate(formatter.parse("20-03-2007"));
		inv.setCycleEndDate(formatter.parse("19-04-2007"));
		inv.setIssueDate(formatter.parse("21-04-2007"));
		inv.setDueDate(formatter.parse("29-04-2007"));

		double totalAmount = 0;
		if (_localAmount > 0) {
			Receipt rcpt = new Receipt();
			rcpt.setCarrierCode("00");
			rcpt.setCarrierName("MyCarrier");
			rcpt.setCarrierState("SP");
			rcpt.setTotalAmount(_localAmount);
			rcpt.setLocal(true);
			inv.addReceipt(rcpt);
			totalAmount += _localAmount;
		}
		
		if (_nfAmounts != null) {
			for (int i=0; i < _nfAmounts.length; i++) {
				Receipt rcpt = new Receipt();
				rcpt.setCarrierCode("00");
				rcpt.setCarrierName("MyCarrier");
				rcpt.setCarrierState("SP");
				rcpt.setTotalAmount(_nfAmounts[i]);
				rcpt.setLocal(false);
				inv.addReceipt(rcpt);
				totalAmount += _nfAmounts[i];
			}
		}
		acc.addInvoice(inv);
		inv.setTotalAmount(totalAmount);
		return acc;
	}
	
		
}
