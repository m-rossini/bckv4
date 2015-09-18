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

import br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO;
import br.com.auster.billcheckout.model.TaxType;
import br.com.auster.om.reference.PKEnabledEntity;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author pvieira
 *
 */
public class TaxTypeDAO extends BaseUpdatableDimensionDAO {

	protected static final int TAX_CODE_COL = 1;
	protected static final int TAX_NAME_COL = 2;
	protected static final int CUSTOM1_COL = 3;
	protected static final int CUSTOM2_COL = 4;
	protected static final int CUSTOM3_COL = 5;
	protected static final int OBJID_COL = 6;



	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createInsertStatement(java.sql.Connection)
	 */
	protected PreparedStatement createInsertStatement(Connection _conn) throws SQLException {

		String sql = "insert into bck_tax_type (tax_code, tax_name, custom_1, custom_2, custom_3, objid) "
			+ "values (?, ?, ?, ?, ?, " + JDBCSequenceHelper.translate(_conn, "bck_dimensions_uid") + ")";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createUpdateStatement(java.sql.Connection)
	 */
	protected PreparedStatement createUpdateStatement(Connection _conn) throws SQLException {

		String sql = "update bck_tax_type set tax_code = ?, tax_name = ?, custom_1 = ?, "
			+ "custom_2 = ?, custom_3 = ? where objid = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setFieldsExceptUID(java.sql.PreparedStatement, br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setFieldsExceptUID(PreparedStatement stmt, PKEnabledEntity obj) throws SQLException {

		TaxType taxType = (TaxType) obj;

		stmt.setString(TAX_CODE_COL, taxType.getTaxCode());
		stmt.setString(TAX_NAME_COL, taxType.getTaxName());
		stmt.setString(CUSTOM1_COL, taxType.getCustom1());
		stmt.setString(CUSTOM2_COL, taxType.getCustom2());
		stmt.setString(CUSTOM3_COL, taxType.getCustom3());

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
		String sql = "select objid from bck_tax_type where tax_code = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createDeleteStatement(Connection)
	 */
	protected PreparedStatement createDeleteStatement(Connection _conn) throws SQLException {
		String sql = "delete from bck_tax_type where tax_code = ?";
		return _conn.prepareStatement(sql);
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#setAlternateKey(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity obj) throws SQLException {
		_stmt.setString(1, ((TaxType) obj).getTaxCode());
	}

	protected String getSelectAllQuery() {
		return "select tax_code, tax_name, custom_1, custom_2, custom_3, objid from bck_tax_type";
	}

	protected PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException {
		TaxType type = new TaxType();
		type.setUid(_rset.getLong(OBJID_COL));
		type.setTaxCode(_rset.getString(TAX_CODE_COL));
		type.setTaxName(_rset.getString(TAX_NAME_COL));
		type.setCustom1(_rset.getString(CUSTOM1_COL));
		type.setCustom2(_rset.getString(CUSTOM2_COL));
		type.setCustom3(_rset.getString(CUSTOM3_COL));
		return type;
	}
}
