/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 21/10/2006
 */
package br.com.auster.billcheckout.loader;

import java.sql.SQLException;

import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.billcheckout.consequence.telco.sql.CarrierDimensionDAO;
import br.com.auster.billcheckout.model.CarrierData;
import br.com.auster.billcheckout.model.dao.CarrierDataDAO;

/**
 * @author pvieira
 * 
 */
public class CarrierDataFileProcessor extends DataFileProcessor {

	public static final int CARRIER_CODE = 0;
	public static final int CARRIER_STATE = 1;
	public static final int CARRIER_NAME = 2;
	public static final int CARRIER_FULL_NAME = 3;
	public static final int CARRIER_TAX_ID = 4;
	public static final int CARRIER_STATE_ENROLL_NBR = 5;
	public static final int CARRIER_CITY_ENROLL_NBR = 6;
	public static final int ADDR_STREET = 7;
	public static final int ADDR_NUMBER = 8;
	public static final int ADDR_COMPLEMENT = 9;
	public static final int ADDR_ZIP = 10;
	public static final int ADDR_CITY = 11;
	public static final int ADDR_WEB = 12;
	public static final int ADDR_EMAIL = 13;

	public void insertData(DataFileVO dataVO) throws SQLException {

		try {
			CarrierDimension carrierDM = new CarrierDimension();
			
			carrierDM.setCarrierCode(dataVO.getValue(CARRIER_CODE));
			carrierDM.setCarrierState(dataVO.getValue(CARRIER_STATE));
			carrierDM.setCarrierCompany(dataVO.getValue(CARRIER_NAME));
			CarrierDimensionDAO carrierDimensionDAO = new CarrierDimensionDAO();
			long carrierDMUid = carrierDimensionDAO.getUID(this.getConnection(), carrierDM);
			if (carrierDMUid == 0) {
				throw new Exception("Cannot insert: " + carrierDM);
			}

			CarrierData carrierData = new CarrierData();
			carrierData.setFullName(dataVO.getValue(CARRIER_FULL_NAME));
			carrierData.setTaxId(dataVO.getValue(CARRIER_TAX_ID));
			carrierData.setStateEnrollNumber(dataVO.getValue(CARRIER_STATE_ENROLL_NBR));
			carrierData.setCityEnrollNumber(dataVO.getValue(CARRIER_CITY_ENROLL_NBR));
			carrierData.setAddressStreet(dataVO.getValue(ADDR_STREET));
			carrierData.setAddressNumber(dataVO.getValue(ADDR_NUMBER));
			carrierData.setAddressComplement(dataVO.getValue(ADDR_COMPLEMENT));
			carrierData.setAddressZip(dataVO.getValue(ADDR_ZIP));
			carrierData.setAddressCity(dataVO.getValue(ADDR_CITY));
			carrierData.setAddressWeb(dataVO.getValue(ADDR_WEB));
			carrierData.setAddressEmail(dataVO.getValue(ADDR_EMAIL));
			carrierData.setCarrierDimension(new CarrierDimension(carrierDMUid));
			
			CarrierDataDAO carrierDataDAO = new CarrierDataDAO();
			long carrierDataUid = carrierDataDAO.getUID(this.getConnection(), carrierData);
			if (carrierDataUid == 0) {
				throw new Exception("Cannot insert: " + carrierData);
			}

		} catch (Exception ex) {
			throw new SQLException(ex.getMessage());
		}
		
	}

}
