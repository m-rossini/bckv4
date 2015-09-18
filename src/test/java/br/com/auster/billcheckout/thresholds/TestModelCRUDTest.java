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
 * Created on 16/03/2007
 */
package br.com.auster.billcheckout.thresholds;

import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Expression;
import org.w3c.dom.Document;

import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.billcheckout.consequence.telco.GeographicDimension;
import br.com.auster.common.io.IOUtils;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;

/**
 * @author framos
 * @version $Id$
 *
 */
public class TestModelCRUDTest extends TestCase {

	

    /**
     * @see junit.framework.TestCase#setUp()
     */
	protected void setUp() throws Exception {
        LogFactory.configureLogSystem(DOMUtils.openDocument(IOUtils.openFileForRead("/log4j.xml")));
	}

	
	public void testAllOperations() {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(false);
			Document doc = dbf.newDocumentBuilder().parse(IOUtils.openFileForRead("/thresholds/hibernate-configuration.xml"));
			Configuration cfg = new Configuration();
			cfg.configure(doc);
			
			SessionFactory sf = cfg.buildSessionFactory();
			Session s = sf.openSession();
			s.connection().setAutoCommit(false);
			
			// initial remove operations
			removeFromAll(s);			

			Statement st = s.connection().createStatement();
			ResultSet rs = st.executeQuery("select objid from bck_carrier_dm");
			long carrierUid = -1L; 
			if (rs.next()) {
				carrierUid = rs.getLong(1);
			}
			rs.close();
			
			// insert operations
			UsageThreshold usage = new UsageThreshold();
			s.save(usage);
			OCCThreshold occ = new OCCThreshold();
			occ.setCarrier(new CarrierDimension(carrierUid));
			s.save(occ);
			NFThreshold nf = new NFThreshold();
			s.save(nf);
			InvoiceThreshold inv = new InvoiceThreshold();
			s.save(inv);
			s.flush();
			s.connection().commit();
		
			// select operations
			usage = (UsageThreshold) s.createCriteria(UsageThreshold.class).uniqueResult();
			assertNotNull(usage);
			occ = (OCCThreshold) s.createCriteria(OCCThreshold.class).uniqueResult();
			assertNotNull(occ);
			nf = (NFThreshold) s.createCriteria(NFThreshold.class).uniqueResult();
			assertNotNull(nf);
			inv = (InvoiceThreshold) s.createCriteria(InvoiceThreshold.class).uniqueResult();
			assertNotNull(inv);
			
			// update operations
			// update usage info
			usage.setCollectCall(true);
			usage.setTimePeriod(TimePeriod.ANY_DAY);
			usage.setDestination(DestinationType.MOBILE);
			usage.setLowerAmount(10);
			usage.setUpperAmount(20);
			usage.setUpperDuration(30);
			s.saveOrUpdate(usage);
			// update occ info
			occ.setHintMessage("My hint message");
			s.saveOrUpdate(occ);
			// update invoice info
			CustomerType ct = (CustomerType) s.createCriteria(CustomerType.class)
			                                  .add(Expression.eq("customerType", "G"))
			                                  .uniqueResult();
			inv.setCustomerType(ct);
			rs = st.executeQuery("select objid from bck_geo_dm");
			if (rs.next()) {
				inv.setUF(new GeographicDimension(rs.getLong(1)));
			}
			inv.setUpperAmount(1000);
			rs.close();
			s.saveOrUpdate(inv);
			st.close();
			// updating nf info
			nf.setLocalNF(false);
			nf.setUpperAmount(1000);
			nf.setLowerAmount(10);
			s.saveOrUpdate(nf);
			s.flush();
			s.connection().commit();

			
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	
	protected void removeFromAll(Session _session) throws Exception {
		Statement s = _session.connection().createStatement();
		s.executeUpdate("delete from bck_parm_invoice_threshold");
		s.executeUpdate("delete from bck_parm_usage_threshold");
		s.executeUpdate("delete from bck_parm_occ_threshold");
		s.executeUpdate("delete from bck_parm_nf_threshold");
		// deleting customer type table
//		s.executeUpdate("delete from bck_parm_cust_type");
		_session.connection().commit();
		
	}
}
