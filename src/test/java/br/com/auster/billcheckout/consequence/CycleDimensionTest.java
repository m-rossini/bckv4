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
 * Created on 20/08/2006
 */
package br.com.auster.billcheckout.consequence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.com.auster.billcheckout.consequence.telco.CycleDimension;
import br.com.auster.billcheckout.consequence.telco.TelcoConsequence;
import br.com.auster.billcheckout.consequence.telco.hibernate.HibernateTelcoDimensionsFacadeImpl;
import br.com.auster.billcheckout.filter.telco.TelcoBillcheckoutPersistenceFilter;
import br.com.auster.common.io.IOUtils;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;


/**
 * @author framos
 * @version $Id$
 *
 */
public class CycleDimensionTest extends TestCase {

	
	protected SessionFactory sf;

    /**
     * @see junit.framework.TestCase#setUp()
     */
	protected void setUp() throws Exception {
        LogFactory.configureLogSystem(DOMUtils.openDocument(IOUtils.openFileForRead("/log4j.xml")));
        
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
		Document doc = dbf.newDocumentBuilder().parse(IOUtils.openFileForRead("/hibernate-configuration.xml"));
		Configuration cfg = new Configuration();
		cfg.configure(doc);
		sf = cfg.buildSessionFactory();
		Connection c = null;
		try  {
			c = sf.openSession().connection();
			Statement s = c.createStatement();
			s.executeUpdate("delete from bck_consequence");
			s.executeUpdate("delete from bck_consequence_attr");
			s.executeUpdate("delete from bck_cycle_dm");
			s.close();
			if (!c.getAutoCommit()) { c.commit(); }			
		} finally {
			if (c != null) { c.close(); }
		}
	}

	public void testAllDatesSet() {
		allDatesSet();
	}
	
	protected void allDatesSet() {

		try {
			Session session = sf.openSession();
			// creating first cycle info
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();
			calendar.add(Calendar.MONTH, 1);
			Date nextMonth = calendar.getTime();
			// configuring filter
			ImplTest test = new ImplTest();
			Element cfg1 = DOMUtils.openDocument("filter/filter.xml", false);
			test.configure(cfg1);
			// configuring facade
			HibernateTelcoDimensionsFacadeImpl facade = new HibernateTelcoDimensionsFacadeImpl(session);
			
			CycleDimension c1 = new CycleDimension("20", now, now, now);
			CycleDimension c2 = new CycleDimension("20", now, now, nextMonth);
			test.saveCycle(session, facade, c1);
			test.saveCycle(session, facade, c2);
			
			assertTrue(c1.getUid() > 0);
			assertTrue(c2.getUid() > c1.getUid());
			
			CycleDimension loaded1 = facade.getCycle("20", now, now);
			assertNotNull(loaded1.getUid());
			assertEquals(c1.getUid(), loaded1.getUid());			

			CycleDimension loaded2 = facade.getCycle("20", now, nextMonth);
			assertNotNull(loaded2.getUid());
			assertEquals(c2.getUid(), loaded2.getUid());			
			
			CycleDimension c3 = new CycleDimension("20", now, now, now);
			assertEquals(0, c3.getUid());
			test.saveCycle(session, facade, c3);
			assertEquals(0, c3.getUid());
			
			// changing hibernate session, facade and filter impls
			session.close();
			session = sf.openSession();
			test = new ImplTest();
			test.configure(cfg1);
			// configuring facade
			facade = new HibernateTelcoDimensionsFacadeImpl(session);			
			
			CycleDimension c11 = new CycleDimension("20", now, now, now);
			CycleDimension c12 = new CycleDimension("20", now, now, nextMonth);
			test.saveCycle(session, facade, c11);
			test.saveCycle(session, facade, c12);
			
			assertEquals(0, c11.getUid());
			assertEquals(0, c12.getUid());
			
			long uid = c1.getUid();
			test.saveCycle(session, facade, c1);
			assertEquals(uid, c1.getUid());
			
			CycleDimension loaded11 = facade.getCycle("20", now, now);
			assertNotNull(loaded11.getUid());
			assertEquals(c1.getUid(), loaded11.getUid());			

			CycleDimension c13 = new CycleDimension("20", nextMonth, now, nextMonth);
			test.saveCycle(session, facade, c13);
			assertTrue(c13.getUid() > c2.getUid());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}


	public void testCutDateNull() {
		cutDateNull();
	}
	
	protected void cutDateNull() {

		try {
			Session session = sf.openSession();
			// creating first cycle info
			Calendar calendar = Calendar.getInstance();
			Date now = null;
			calendar.add(Calendar.MONTH, 1);
			Date nextMonth = calendar.getTime();
			// configuring filter
			ImplTest test = new ImplTest();
			Element cfg1 = DOMUtils.openDocument("filter/filter.xml", false);
			test.configure(cfg1);
			// configuring facade
			HibernateTelcoDimensionsFacadeImpl facade = new HibernateTelcoDimensionsFacadeImpl(session);
			
			CycleDimension c1 = new CycleDimension("20", now, now, now);
			CycleDimension c2 = new CycleDimension("20", now, now, nextMonth);
			test.saveCycle(session, facade, c1);
			test.saveCycle(session, facade, c2);
			
			assertTrue(c1.getUid() > 0);
			assertTrue(c2.getUid() > c1.getUid());
			
			CycleDimension loaded1 = facade.getCycle("20", now, now);
			assertNotNull(loaded1.getUid());
			assertEquals(c1.getUid(), loaded1.getUid());			

			CycleDimension loaded2 = facade.getCycle("20", now, nextMonth);
			assertNotNull(loaded2.getUid());
			assertEquals(c2.getUid(), loaded2.getUid());			
			
			CycleDimension c3 = new CycleDimension("20", now, now, now);
			assertEquals(0, c3.getUid());
			test.saveCycle(session, facade, c3);
			assertEquals(0, c3.getUid());
			
			// changing hibernate session, facade and filter impls
			session.close();
			session = sf.openSession();
			test = new ImplTest();
			test.configure(cfg1);
			// configuring facade
			facade = new HibernateTelcoDimensionsFacadeImpl(session);			
			
			CycleDimension c11 = new CycleDimension("20", now, now, now);
			CycleDimension c12 = new CycleDimension("20", now, now, nextMonth);
			test.saveCycle(session, facade, c11);
			test.saveCycle(session, facade, c12);
			
			assertEquals(0, c11.getUid());
			assertEquals(0, c12.getUid());
			
			long uid = c1.getUid();
			test.saveCycle(session, facade, c1);
			assertEquals(uid, c1.getUid());
			
			CycleDimension loaded11 = facade.getCycle("20", now, now);
			assertNotNull(loaded11.getUid());
			assertEquals(c1.getUid(), loaded11.getUid());			

			CycleDimension c13 = new CycleDimension("20", nextMonth, now, nextMonth);
			test.saveCycle(session, facade, c13);
			assertTrue(c13.getUid() > c2.getUid());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}
	
	public void testDueDateNull() {
		dueDateNull();
	}
	
	protected void dueDateNull() {
		try {
			Session session = sf.openSession();
			// creating first cycle info
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();;
			Date nextMonth = null;
			// configuring filter
			ImplTest test = new ImplTest();
			Element cfg1 = DOMUtils.openDocument("filter/filter.xml", false);
			test.configure(cfg1);
			// configuring facade
			HibernateTelcoDimensionsFacadeImpl facade = new HibernateTelcoDimensionsFacadeImpl(session);
			
			CycleDimension c1 = new CycleDimension("20", now, now, now);
			CycleDimension c2 = new CycleDimension("20", now, now, nextMonth);
			test.saveCycle(session, facade, c1);
			test.saveCycle(session, facade, c2);
			
			assertTrue(c1.getUid() > 0);
			assertTrue(c2.getUid() > c1.getUid());
			
			CycleDimension loaded1 = facade.getCycle("20", now, now);
			assertNotNull(loaded1.getUid());
			assertEquals(c1.getUid(), loaded1.getUid());			

			CycleDimension loaded2 = facade.getCycle("20", now, nextMonth);
			assertNotNull(loaded2.getUid());
			assertEquals(c2.getUid(), loaded2.getUid());			
			
			CycleDimension c3 = new CycleDimension("20", now, now, now);
			assertEquals(0, c3.getUid());
			test.saveCycle(session, facade, c3);
			assertEquals(0, c3.getUid());
			
			// changing hibernate session, facade and filter impls
			session.close();
			session = sf.openSession();
			test = new ImplTest();
			test.configure(cfg1);
			// configuring facade
			facade = new HibernateTelcoDimensionsFacadeImpl(session);			
			
			CycleDimension c11 = new CycleDimension("20", now, now, now);
			CycleDimension c12 = new CycleDimension("20", now, now, nextMonth);
			test.saveCycle(session, facade, c11);
			test.saveCycle(session, facade, c12);
			
			assertEquals(0, c11.getUid());
			assertEquals(0, c12.getUid());
			
			long uid = c1.getUid();
			test.saveCycle(session, facade, c1);
			assertEquals(uid, c1.getUid());
			
			CycleDimension loaded11 = facade.getCycle("20", now, now);
			assertNotNull(loaded11.getUid());
			assertEquals(c1.getUid(), loaded11.getUid());			

			CycleDimension c13 = new CycleDimension("20", nextMonth, now, nextMonth);
			test.saveCycle(session, facade, c13);
			assertTrue(c13.getUid() > c2.getUid());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}
	
	public void testCycleCodeNull() {
		cycleCodeNull();
	}
	
	protected void cycleCodeNull() {
		try {
			Session session = sf.openSession();
			// creating first cycle info
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();;
			calendar.add(Calendar.MONTH, 1);
			Date nextMonth = calendar.getTime();
			// configuring filter
			ImplTest test = new ImplTest();
			Element cfg1 = DOMUtils.openDocument("filter/filter.xml", false);
			test.configure(cfg1);
			// configuring facade
			HibernateTelcoDimensionsFacadeImpl facade = new HibernateTelcoDimensionsFacadeImpl(session);
			
			CycleDimension c1 = new CycleDimension(null, now, now, now);
			CycleDimension c2 = new CycleDimension(null, now, now, nextMonth);
			test.saveCycle(session, facade, c1);
			test.saveCycle(session, facade, c2);
			
			assertTrue(c1.getUid() > 0);
			assertTrue(c2.getUid() > c1.getUid());
			
			CycleDimension loaded1 = facade.getCycle(null, now, now);
			assertNotNull(loaded1.getUid());
			assertEquals(c1.getUid(), loaded1.getUid());			

			CycleDimension loaded2 = facade.getCycle(null, now, nextMonth);
			assertNotNull(loaded2.getUid());
			assertEquals(c2.getUid(), loaded2.getUid());			
			
			CycleDimension c3 = new CycleDimension(null, now, now, now);
			assertEquals(0, c3.getUid());
			test.saveCycle(session, facade, c3);
			assertEquals(0, c3.getUid());
			
			// changing hibernate session, facade and filter impls
			session.close();
			session = sf.openSession();
			test = new ImplTest();
			test.configure(cfg1);
			// configuring facade
			facade = new HibernateTelcoDimensionsFacadeImpl(session);			
			
			CycleDimension c11 = new CycleDimension(null, now, now, now);
			CycleDimension c12 = new CycleDimension(null, now, now, nextMonth);
			test.saveCycle(session, facade, c11);
			test.saveCycle(session, facade, c12);
			
			assertEquals(0, c11.getUid());
			assertEquals(0, c12.getUid());
			
			long uid = c1.getUid();
			test.saveCycle(session, facade, c1);
			assertEquals(uid, c1.getUid());
			
			CycleDimension loaded11 = facade.getCycle(null, now, now);
			assertNotNull(loaded11.getUid());
			assertEquals(c1.getUid(), loaded11.getUid());			

			CycleDimension c13 = new CycleDimension(null, nextMonth, now, nextMonth);
			test.saveCycle(session, facade, c13);
			assertTrue(c13.getUid() > c2.getUid());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}
	
	public void testAllNull() {
		allNull();
	}
	
	protected void allNull() {
		try {
			Session session = sf.openSession();
			// creating first cycle info
			Date now = null;
			Date nextMonth = null;
			// configuring filter
			ImplTest test = new ImplTest();
			Element cfg1 = DOMUtils.openDocument("filter/filter.xml", false);
			test.configure(cfg1);
			// configuring facade
			HibernateTelcoDimensionsFacadeImpl facade = new HibernateTelcoDimensionsFacadeImpl(session);
			
			CycleDimension c1 = new CycleDimension(null, now, now, now);
			CycleDimension c2 = new CycleDimension(null, now, now, nextMonth);
			test.saveCycle(session, facade, c1);
			test.saveCycle(session, facade, c2);
			
			assertTrue(c1.getUid() > 0);
			assertEquals(0, c2.getUid());
			
			CycleDimension loaded1 = facade.getCycle(null, now, now);
			assertNotNull(loaded1.getUid());
			assertEquals(c1.getUid(), loaded1.getUid());			

			CycleDimension loaded2 = facade.getCycle(null, now, nextMonth);
			assertNotNull(loaded2.getUid());
			assertEquals(c1.getUid(), loaded2.getUid());			
			
			CycleDimension c3 = new CycleDimension(null, now, now, now);
			assertEquals(0, c3.getUid());
			test.saveCycle(session, facade, c3);
			assertEquals(0, c3.getUid());
			
			// changing hibernate session, facade and filter impls
			session.close();
			session = sf.openSession();
			test = new ImplTest();
			test.configure(cfg1);
			// configuring facade
			facade = new HibernateTelcoDimensionsFacadeImpl(session);			
			
			CycleDimension c11 = new CycleDimension(null, now, now, now);
			CycleDimension c12 = new CycleDimension(null, now, now, nextMonth);
			test.saveCycle(session, facade, c11);
			test.saveCycle(session, facade, c12);
			
			assertEquals(0, c11.getUid());
			assertEquals(0, c12.getUid());
			
			long uid = c1.getUid();
			test.saveCycle(session, facade, c1);
			assertEquals(uid, c1.getUid());
			
			CycleDimension loaded11 = facade.getCycle(null, now, now);
			assertNotNull(loaded11.getUid());
			assertEquals(c1.getUid(), loaded11.getUid());			

			CycleDimension c13 = new CycleDimension(null, nextMonth, now, nextMonth);
			test.saveCycle(session, facade, c13);
			assertEquals(0, c13.getUid());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}	
	
	
	public void testAllPreviousTests() {
		allDatesSet();
		allNull();
		cycleCodeNull();
		
		
	}
	
	protected static class ImplTest extends TelcoBillcheckoutPersistenceFilter {

		public ImplTest() {
			super("name");
		}
		
		public void saveCycle(Session _session, HibernateTelcoDimensionsFacadeImpl _facade, CycleDimension _cycle) throws SQLException {
			TelcoConsequence c = new TelcoConsequence();
			c.setCycle(_cycle);
			this.checkCycle(_session, _facade, c);
		}
		
	}

	
	
	
}
