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

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.com.auster.billcheckout.consequence.telco.TelcoConsequence;
import br.com.auster.billcheckout.consequence.telco.TimeDimension;
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
public class TimeDimensionTest extends TestCase {

	
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
			s.executeUpdate("delete from bck_time_dm");
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
			Calendar nextMonth =  Calendar.getInstance();
			nextMonth.add(Calendar.MONTH, 1);
			// configuring filter
			ImplTest test = new ImplTest();
			Element cfg1 = DOMUtils.openDocument("filter/filter.xml", false);
			test.configure(cfg1);
			// configuring facade
			HibernateTelcoDimensionsFacadeImpl facade = new HibernateTelcoDimensionsFacadeImpl(session);
			
			TimeDimension c1 = new TimeDimension(calendar.getTime());
			TimeDimension c2 = new TimeDimension(String.valueOf(nextMonth.get(Calendar.YEAR)),  
					                             String.valueOf(nextMonth.get(Calendar.MONTH)+1), 
					                             String.valueOf(nextMonth.get(Calendar.DAY_OF_MONTH)) );
			test.saveTime(session, facade, c1);
			test.saveTime(session, facade, c2);
			
			assertTrue(c1.getUid() > 0);
			assertTrue(c2.getUid() > c1.getUid());
			
			TimeDimension loaded1 = facade.getTime(String.valueOf(calendar.get(Calendar.YEAR)),  
                                                   String.valueOf(calendar.get(Calendar.MONTH)+1), 
                                                   String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
			assertNotNull(loaded1.getUid());
			assertEquals(c1.getUid(), loaded1.getUid());			

			TimeDimension loaded2 = facade.getTime(String.valueOf(nextMonth.get(Calendar.YEAR)),  
                    							   String.valueOf(nextMonth.get(Calendar.MONTH)+1), 
                    							   String.valueOf(nextMonth.get(Calendar.DAY_OF_MONTH)));
			assertNotNull(loaded2.getUid());
			assertEquals(c2.getUid(), loaded2.getUid());			
			
			TimeDimension c3 = new TimeDimension(calendar.getTime());
			assertEquals(0, c3.getUid());
			test.saveTime(session, facade, c3);
			assertEquals(0, c3.getUid());
			
			// changing hibernate session, facade and filter impls
			session.close();
			session = sf.openSession();
			test = new ImplTest();
			test.configure(cfg1);
			// configuring facade
			facade = new HibernateTelcoDimensionsFacadeImpl(session);			
			
			TimeDimension c11 = new TimeDimension(String.valueOf(calendar.get(Calendar.YEAR)),  
                    							  String.valueOf(calendar.get(Calendar.MONTH)+1), 
                    							  String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
			
			TimeDimension c12 = new TimeDimension(nextMonth.getTime());
			test.saveTime(session, facade, c11);
			test.saveTime(session, facade, c12);
			
			assertEquals(0, c11.getUid());
			assertEquals(0, c12.getUid());
			
			long uid = c1.getUid();
			test.saveTime(session, facade, c1);
			assertEquals(uid, c1.getUid());
			
			TimeDimension loaded11 = facade.getTime(String.valueOf(calendar.get(Calendar.YEAR)),  
					  								String.valueOf(calendar.get(Calendar.MONTH)+1), 
					  								String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
			assertNotNull(loaded11.getUid());
			assertEquals(c1.getUid(), loaded11.getUid());			

			Calendar lastMonth =  Calendar.getInstance();
			lastMonth.add(Calendar.MONTH, -1);
			TimeDimension c13 = new TimeDimension(lastMonth.getTime());
			test.saveTime(session, facade, c13);
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
			// configuring filter
			ImplTest test = new ImplTest();
			Element cfg1 = DOMUtils.openDocument("filter/filter.xml", false);
			test.configure(cfg1);
			// configuring facade
			HibernateTelcoDimensionsFacadeImpl facade = new HibernateTelcoDimensionsFacadeImpl(session);
			
			TimeDimension c1 = new TimeDimension(null);
			test.saveTime(session, facade, c1);
			
			assertTrue(c1.getUid() > 0);
			
			TimeDimension loaded1 = facade.getTime(null, null, null);
			assertNotNull(loaded1.getUid());
			assertEquals(c1.getUid(), loaded1.getUid());			

			TimeDimension c3 = new TimeDimension(null);
			assertEquals(0, c3.getUid());
			test.saveTime(session, facade, c3);
			assertEquals(0, c3.getUid());
			
			// changing hibernate session, facade and filter impls
			session.close();
			session = sf.openSession();
			test = new ImplTest();
			test.configure(cfg1);
			// configuring facade
			facade = new HibernateTelcoDimensionsFacadeImpl(session);			
			
			TimeDimension c11 = new TimeDimension(null);
			test.saveTime(session, facade, c11);
			
			assertEquals(0, c11.getUid());
			
			long uid = c1.getUid();
			test.saveTime(session, facade, c1);
			assertEquals(uid, c1.getUid());
			
			TimeDimension loaded11 = facade.getTime(null, null, null);
			assertNotNull(loaded11.getUid());
			assertEquals(c1.getUid(), loaded11.getUid());			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}	
	
	
	public void testAllPreviousTests() {
		allDatesSet();
		allNull();
		
		
	}
	
	protected static class ImplTest extends TelcoBillcheckoutPersistenceFilter {

		public ImplTest() {
			super("name");
		}
		
		public void saveTime(Session _session, HibernateTelcoDimensionsFacadeImpl _facade, TimeDimension _time) throws SQLException {
			TelcoConsequence c = new TelcoConsequence();
			c.setTime(_time);
			this.checkTime(_session, _facade, c);
		}
		
	}

	
	
	
}
