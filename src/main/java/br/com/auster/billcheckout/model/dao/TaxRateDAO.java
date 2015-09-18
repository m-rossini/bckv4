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
 * Created on 16/08/2006
 */
package br.com.auster.billcheckout.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO;
import br.com.auster.billcheckout.model.TaxRate;
import br.com.auster.om.reference.PKEnabledEntity;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author pvieira
 *
 */
public class TaxRateDAO extends BaseUpdatableDimensionDAO {

	protected static final int TAX_RATE_COL = 1;
	protected static final int GEO_DM_COL = 2;
	protected static final int TAX_TYPE_COL = 3;
	protected static final int FISCAL_CODE_COL = 4;
	protected static final int DT_EFFECTIVE_COL = 5;
	protected static final int DT_EXPIRATION_COL = 6;
	protected static final int CUSTOM1_COL = 7;
	protected static final int CUSTOM2_COL = 8;
	protected static final int CUSTOM3_COL = 9;
	protected static final int OBJID_COL = 10;



	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createInsertStatement(java.sql.Connection)
	 */
	protected PreparedStatement createInsertStatement(Connection _conn) throws SQLException {

		String sql = "insert into bck_tax_rate (tax_rate, geo_uid, tax_type_uid, fiscal_code_uid, dt_effective, " 
			+ "dt_expiration, custom_1, custom_2, custom_3, objid) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, " + JDBCSequenceHelper.translate(_conn, "bck_dimensions_uid") + ")";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createUpdateStatement(java.sql.Connection)
	 */
	protected PreparedStatement createUpdateStatement(Connection _conn) throws SQLException {

		String sql = "update bck_tax_rate set tax_rate = ?, geo_uid = ?, tax_type_uid = ?, "
			+ "fiscal_code_uid = ?, dt_effective = ?, dt_expiration = ?, "
			+ "custom_1 = ?, custom_2 = ?, custom_3 = ? where objid = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setFieldsExceptUID(java.sql.PreparedStatement, br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setFieldsExceptUID(PreparedStatement stmt, PKEnabledEntity obj) throws SQLException {

		TaxRate taxRate = (TaxRate) obj;

		java.sql.Date dtEffective = taxRate.getDtEffective() == null ? new java.sql.Date(System.currentTimeMillis())
				: new java.sql.Date(taxRate.getDtEffective().getTime());
		java.sql.Date dtExpiration = taxRate.getDtExpiration() == null ? null
				: new java.sql.Date(taxRate.getDtExpiration().getTime());

		stmt.setFloat(TAX_RATE_COL, taxRate.getTaxRate());
		stmt.setLong(GEO_DM_COL, taxRate.getGeoDimension().getUid());
		stmt.setLong(TAX_TYPE_COL, taxRate.getTaxType().getUid());
		stmt.setLong(FISCAL_CODE_COL, taxRate.getFiscalCode().getUid());
		stmt.setDate(DT_EFFECTIVE_COL, dtEffective);
		stmt.setDate(DT_EXPIRATION_COL, dtExpiration);
		stmt.setString(CUSTOM1_COL, taxRate.getCustom1());
		stmt.setString(CUSTOM2_COL, taxRate.getCustom2());
		stmt.setString(CUSTOM3_COL, taxRate.getCustom3());

	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setUIDField(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setUIDField(PKEnabledEntity obj) throws SQLException {

		this.updateStmt.setLong(OBJID_COL, obj.getUid());
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#createSelectUIDStatement(java.sql.Connection)
	 */
	protected PreparedStatement createSelectUIDStatement(Connection _conn) throws SQLException {
		String sql = "select objid from bck_tax_rate where geo_uid = ? and tax_type_uid = ? and fiscal_code_uid = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createDeleteStatement
	 */
	protected PreparedStatement createDeleteStatement(Connection _conn) throws SQLException {
		String sql = "delete from bck_tax_rate where geo_uid = ? and tax_type_uid = ? and fiscal_code_uid = ?";
		return _conn.prepareStatement(sql);
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#setAlternateKey(PreparedStatement, PKEnabledEntity)
	 */
	protected void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity obj) throws SQLException {

		TaxRate taxRate = (TaxRate) obj;
		_stmt.setLong(1, taxRate.getGeoDimension().getUid());
		_stmt.setLong(2, taxRate.getTaxType().getUid());
		
		if (taxRate.getFiscalCode() != null) {
			_stmt.setLong(3, taxRate.getFiscalCode().getUid());
		} else {
			_stmt.setNull(3, Types.NUMERIC);
		}
	}

	protected String getSelectAllQuery() {
		return "select tax_rate, geo_uid, tax_type_uid, fiscal_code_uid, dt_effective, dt_expiration, custom_1, custom_2, custom_3, objid from bck_tax_rate";
	}

	protected PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException {
		TaxRate taxRate = new TaxRate();
		taxRate.setUid(_rset.getLong(OBJID_COL));
		taxRate.setTaxRate(_rset.getFloat(TAX_RATE_COL));
		taxRate.setGeoDimensionUid(_rset.getLong(GEO_DM_COL));
//		taxRate.setTaxType(taxType)
//		taxRate.setFiscalCode(fiscalCode)
		taxRate.setDtEffective(_rset.getDate(DT_EFFECTIVE_COL));
		taxRate.setDtExpiration(_rset.getDate(DT_EXPIRATION_COL));
		taxRate.setCustom1(_rset.getString(CUSTOM1_COL));
		taxRate.setCustom2(_rset.getString(CUSTOM2_COL));
		taxRate.setCustom3(_rset.getString(CUSTOM3_COL));
		return taxRate;
	}
}
