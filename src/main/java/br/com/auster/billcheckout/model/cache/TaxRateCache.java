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
import br.com.auster.billcheckout.consequence.telco.GeographicDimension;
import br.com.auster.billcheckout.model.FiscalCode;
import br.com.auster.billcheckout.model.TaxRate;
import br.com.auster.billcheckout.model.TaxType;

/**
 * @author framos
 * @version $Id$
 *
 */
public class TaxRateCache extends ReferenceDataCache {


	
	protected static final String SELECT_STATEMENT = 
		"select a.OBJID, a.TAX_RATE, a.CUSTOM_1, a.CUSTOM_2, a.CUSTOM_3, " +
		"a.GEO_UID, a.TAX_TYPE_UID, a.FISCAL_CODE_UID, b.GEO_STATE, c.TAX_CODE, " +
		"c.TAX_NAME, d.FISCAL_CODE, d.CODE_DESCRIPTION " +
		"from BCK_TAX_RATE a " + 
		" join BCK_GEO_DM b on a.GEO_UID = b.OBJID " +
		" join BCK_TAX_TYPE c on a.TAX_TYPE_UID = c.OBJID " +
		" left outer join BCK_FISCAL_CODE d on a.FISCAL_CODE_UID = d.OBJID ";
	
	protected static final String SELECT_LAZY_STATEMENT = 
		SELECT_STATEMENT + 
		" where a.GEO_UID = ? and a.TAX_TYPE_UID = ? and ( a.FISCAL_CODE_UID = ? or a.FISCAL_CODE_UID is null)";
	
	protected static final String SELECT_ALTERNATE_LAZY_STATEMENT = 
		SELECT_STATEMENT + 
		" where b.GEO_STATE = ? and c.TAX_CODE = ? and ( d.FISCAL_CODE = ? or d.FISCAL_CODE is null)";
	
	
	
	
	/**
	 * @see br.com.auster.billcheckout.caches.ReferenceDataCache#createVO(java.sql.ResultSet)
	 */
	protected CacheableVO createVO(ResultSet _rset) throws SQLException {
		if (_rset == null) { return null; }
		TaxRate tr = new TaxRate();
		int col = 1;
		tr.setUid(_rset.getLong(col++));
		tr.setTaxRate(_rset.getFloat(col++));
		tr.setCustom1(_rset.getString(col++));
		tr.setCustom2(_rset.getString(col++));
		tr.setCustom3(_rset.getString(col++));
		// related objects
		GeographicDimension gd = new GeographicDimension(_rset.getLong(col++));
		TaxType tt = new TaxType(_rset.getLong(col++));
		FiscalCode fc = new FiscalCode(_rset.getLong(col++));
		gd.setState(_rset.getString(col++));
		tr.setGeoDimension(gd);
		tt.setTaxCode(_rset.getString(col++));
		tt.setTaxName(_rset.getString(col++));
		tr.setTaxType(tt);
		fc.setFiscalCode(_rset.getString(col++));
		fc.setCodeDescription(_rset.getString(col++));
		tr.setFiscalCode(fc);
		return tr;
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
		TaxRate.InnerAlternateKey key = (TaxRate.InnerAlternateKey) _key;
		_stmt.setString(1, key.getGeographics());
		_stmt.setString(2, key.getTaxType());
		_stmt.setString(3, key.getFiscalCode());
	}

	/**
	 * @see br.com.auster.billcheckout.caches.ReferenceDataCache#setLazySQLParameters(java.sql.PreparedStatement, br.com.auster.billcheckout.caches.CacheableKey)
	 */
	protected void setLazySQLParameters(PreparedStatement _stmt, CacheableKey _key) throws SQLException {
		TaxRate.InnerKey key = (TaxRate.InnerKey) _key;
		_stmt.setLong(1, key.getGeographics());
		_stmt.setLong(2, key.getTaxType());
		_stmt.setLong(3, key.getFiscalCode());
	}

}
