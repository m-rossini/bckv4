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
 * Created on 22/12/2006
 */
package br.com.auster.billcheckout.consequence.telco.sql;

import java.sql.Connection;

import junit.framework.TestCase;
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.common.io.IOUtils;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.persistence.jdbc.DBCPJDBCPersistenceService;

/**
 * @author framos
 * @version $Id$
 *
 */
public class CarrierDimensionDAOTest extends TestCase {

	
	protected DBCPJDBCPersistenceService service;
	
	protected void setUp() throws Exception {
		try {
			if (service == null) {
				Class.forName("org.apache.commons.dbcp.PoolingDriver");
				Class.forName("oracle.jdbc.driver.OracleDriver");
				service = new DBCPJDBCPersistenceService();
				service.init(DOMUtils.openDocument(IOUtils.openFileForRead("dao/carrierdao.xml")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testInsertCarrier() {
		try {
			Connection c = (Connection) service.openResourceConnection();
			CarrierDimension cd = new CarrierDimension();
			cd.setCarrierCode("XX");
			cd.setCarrierState("SP");
			cd.setCarrierCompany("Test Company");
			CarrierDimensionDAO dao = new CarrierDimensionDAO();
			dao.reset(c);
			dao.insert(cd);
			service.closeResourceConnection(c);
			c = (Connection) service.openResourceConnection();
			dao.reset(c);
			cd.setCustom1("TODAY");
			dao.update(cd);
			service.closeResourceConnection(c);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
