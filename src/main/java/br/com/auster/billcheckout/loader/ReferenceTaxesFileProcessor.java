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
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.auster.billcheckout.consequence.telco.GeographicDimension;
import br.com.auster.billcheckout.consequence.telco.sql.GeographicDimensionDAO;
import br.com.auster.billcheckout.model.FiscalCode;
import br.com.auster.billcheckout.model.TaxRate;
import br.com.auster.billcheckout.model.TaxType;
import br.com.auster.billcheckout.model.dao.FiscalCodeDAO;
import br.com.auster.billcheckout.model.dao.TaxRateDAO;
import br.com.auster.billcheckout.model.dao.TaxTypeDAO;

/**
 * @author pvieira
 * 
 */
public class ReferenceTaxesFileProcessor extends DataFileProcessor {

	public static final int GEO_COUTRY = 0;
	public static final int GEO_REGION = 1;
	public static final int GEO_STATE = 2;
	public static final int GEO_CITY = 3;
	public static final int TAX_CODE = 4;
	public static final int TAX_NAME = 5;
	public static final int FISCAL_CODE = 6;
	public static final int CODE_DESCRIPTION = 7;
	public static final int DT_EFFECTIVE = 8;
	public static final int TAX_RATE = 9;

	public static final String DEFAULT_GEO_COUTRY = "Brasil";

	public void insertData(DataFileVO dataVO) throws SQLException {

		try {
			GeographicDimension geoDM = new GeographicDimension();
			
			String geoCounry = dataVO.getValue(GEO_COUTRY).length() == 0? 
					DEFAULT_GEO_COUTRY: 
					dataVO.getValue(GEO_COUTRY);

			geoDM.setCountry(geoCounry);
			geoDM.setRegion(dataVO.getValue(GEO_REGION));
			geoDM.setState(dataVO.getValue(GEO_STATE));
			geoDM.setCity(dataVO.getValue(GEO_CITY));
			GeographicDimensionDAO geoDimensionDAO = new GeographicDimensionDAO();
			long geoDMUid = geoDimensionDAO.getUID(this.getConnection(), geoDM);
			if (geoDMUid == 0) {
				throw new Exception("Cannot insert: " + geoDM);
			}

			TaxType taxType = new TaxType();
			taxType.setTaxCode(dataVO.getValue(TAX_CODE));
			taxType.setTaxName(dataVO.getValue(TAX_NAME));
			TaxTypeDAO taxTypeDAO = new TaxTypeDAO();
			long taxTypeUid = taxTypeDAO.getUID(this.getConnection(), taxType);
			if (taxTypeUid == 0) {
				throw new Exception("Cannot insert: " + taxType);
			}

			FiscalCode fiscalCode = new FiscalCode();
			fiscalCode.setFiscalCode(dataVO.getValue(FISCAL_CODE));
			fiscalCode.setCodeDescription(dataVO.getValue(CODE_DESCRIPTION));
			FiscalCodeDAO fiscalCodeDAO = new FiscalCodeDAO();
			long fiscalCodeUid = fiscalCodeDAO.getUID(this.getConnection(), fiscalCode);
			if (fiscalCodeUid == 0) {
				throw new Exception("Cannot insert: " + fiscalCode);
			}

			TaxRate taxRate = new TaxRate();
			taxRate.setGeoDimension(new GeographicDimension(geoDMUid));
			taxRate.setTaxType(new TaxType(taxTypeUid));
			taxRate.setFiscalCode(new FiscalCode(fiscalCodeUid));
			taxRate.setTaxRate(Float.parseFloat(dataVO.getValue(TAX_RATE)));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date dtEffective = dataVO.getValue(DT_EFFECTIVE).length() == 0?
					null: sdf.parse(dataVO.getValue(DT_EFFECTIVE));

			taxRate.setDtEffective(dtEffective);

			TaxRateDAO taxRateDAO = new TaxRateDAO();
			long taxRateUid = taxRateDAO.getUID(this.getConnection(), taxRate);
			if (taxRateUid == 0) {
				throw new Exception("Cannot insert: " + taxRate);
			}

		} catch (Exception ex) {
			throw new SQLException(ex.getMessage());
		}
		
	}

}
