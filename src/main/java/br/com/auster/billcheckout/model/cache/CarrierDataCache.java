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
 * Created on 27/12/2006
 */
package br.com.auster.billcheckout.model.cache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.billcheckout.caches.ReferenceDataCache;
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.billcheckout.model.CarrierData;

/**
 * @author framos
 * @version $Id$
 *
 */
public class CarrierDataCache extends ReferenceDataCache {


	
	protected static final String SELECT_STATEMENT = 
		"select a.OBJID, a.CARRIER_FULL_NAME, a.CARRIER_TAX_ID, a.CARRIER_STATE_ENROLL_NBR, " +
		"a.CARRIER_CITY_ENROLL_NBR, a.ADDR_STREET, a.ADDR_NUMBER, a.ADDR_COMPLEMENT, a.ADDR_ZIP, " +
		"a.ADDR_CITY, a.ADDR_WEB, a.ADDR_EMAIL, a.CUSTOM_1, a.CUSTOM_2, a.CUSTOM_3, " +
		"a.CARRIER_DM_UID, b.CARRIER_CODE, b.CARRIER_STATE " +
		"from BCK_CARRIER_DATA a " + 
		" join BCK_CARRIER_DM b on a.CARRIER_DM_UID = b.OBJID ";
	
	protected static final String SELECT_LAZY_STATEMENT = 
		SELECT_STATEMENT + 
		" where a.OBJID = ?";
	
	protected static final String SELECT_ALTERNATE_LAZY_STATEMENT = 
		SELECT_STATEMENT + 
		" where b.CARRIER_CODE = ? and b.CARRIER_STATE = ?";
	
	
	
	
	/**
	 * @see br.com.auster.billcheckout.caches.ReferenceDataCache#createVO(java.sql.ResultSet)
	 */
	protected CacheableVO createVO(ResultSet _rset) throws SQLException {
		if (_rset == null) { return null; }
		CarrierData dt = new CarrierData();
		int col = 1;
		dt.setUid(_rset.getLong(col++));
		dt.setFullName(_rset.getString(col++));
		dt.setTaxId(_rset.getString(col++));
		dt.setStateEnrollNumber(_rset.getString(col++));
		dt.setCityEnrollNumber(_rset.getString(col++));
		dt.setAddressStreet(_rset.getString(col++));
		dt.setAddressNumber(_rset.getString(col++));
		dt.setAddressComplement(_rset.getString(col++));
		dt.setAddressZip(_rset.getString(col++));
		dt.setAddressCity(_rset.getString(col++));
		dt.setAddressWeb(_rset.getString(col++));
		dt.setAddressEmail(_rset.getString(col++));
		dt.setCustom1(_rset.getString(col++));
		dt.setCustom2(_rset.getString(col++));
		dt.setCustom3(_rset.getString(col++));
		CarrierDimension dm = new CarrierDimension();
		dm.setUid(_rset.getLong(col++));
		dm.setCarrierCode(_rset.getString(col++));
		dm.setCarrierState(_rset.getString(col++));
		dt.setCarrierDimension(dm);
		return dt;
	}

	/**
	 * @see br.com.auster.billcheckout.caches.ReferenceDataCache#getAlternateLazySQL()
	 */
	protected String getAlternateLazySQL() {
		return SELECT_ALTERNATE_LAZY_STATEMENT;
	}

	/**
	 * @see br.com.auster.billcheckout.caches.ReferenceDataCache#getLazySQL()
	 */
	protected String getLazySQL() {
		return SELECT_LAZY_STATEMENT;
	}

	/**
	 * @see br.com.auster.billcheckout.caches.ReferenceDataCache#getNonLazySQL()
	 */
	protected String getNonLazySQL() {
		return SELECT_STATEMENT;
	}

	/**
	 * @see br.com.auster.billcheckout.caches.ReferenceDataCache#setAlternateLazySQLParameters(java.sql.PreparedStatement, br.com.auster.billcheckout.caches.CacheableKey)
	 */
	protected void setAlternateLazySQLParameters(PreparedStatement _stmt, CacheableKey _key) throws SQLException {
		CarrierData.InnerAlternateKey key = (CarrierData.InnerAlternateKey) _key;
		_stmt.setString(1, key.getCarrierCode());
		_stmt.setString(2, key.getState());
	}

	/**
	 * @see br.com.auster.billcheckout.caches.ReferenceDataCache#setLazySQLParameters(java.sql.PreparedStatement, br.com.auster.billcheckout.caches.CacheableKey)
	 */
	protected void setLazySQLParameters(PreparedStatement _stmt, CacheableKey _key) throws SQLException {
		CarrierData.InnerKey key = (CarrierData.InnerKey) _key;
		_stmt.setLong(1, key.getUid());
	}

}
