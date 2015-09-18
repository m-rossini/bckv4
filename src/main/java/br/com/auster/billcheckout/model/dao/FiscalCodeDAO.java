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
import br.com.auster.billcheckout.model.FiscalCode;
import br.com.auster.om.reference.PKEnabledEntity;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author pvieira
 *
 */
public class FiscalCodeDAO extends BaseUpdatableDimensionDAO {

	protected static final int FISCAL_CODE_COL = 1;
	protected static final int CODE_DESCRIPTION_COL = 2;
	protected static final int CUSTOM1_COL = 3;
	protected static final int CUSTOM2_COL = 4;
	protected static final int CUSTOM3_COL = 5;
	protected static final int OBJID_COL = 6;


	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createInsertStatement(java.sql.Connection)
	 */
	protected PreparedStatement createInsertStatement(Connection conn) throws SQLException {
		String sql = "insert into bck_fiscal_code (fiscal_code, code_description, custom_1, custom_2, custom_3, objid) "
			+ "values (?, ?, ?, ?, ?, " + JDBCSequenceHelper.translate(conn, "bck_dimensions_uid") + ")";
		return conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createUpdateStatement(java.sql.Connection)
	 */
	protected PreparedStatement createUpdateStatement(Connection conn) throws SQLException {
		String sql = "update bck_fiscal_code set fiscal_code = ?, code_description = ?, custom_1 = ?, "
			+ "custom_2 = ?, custom_3 = ? where objid = ?";
		return conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setFieldsExceptUID(java.sql.PreparedStatement, br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setFieldsExceptUID(PreparedStatement stmt, PKEnabledEntity obj) throws SQLException {
		FiscalCode fiscalCode = (FiscalCode) obj;
		stmt.setString(FISCAL_CODE_COL, fiscalCode.getFiscalCode());
		stmt.setString(CODE_DESCRIPTION_COL, fiscalCode.getCodeDescription());
		stmt.setString(CUSTOM1_COL, fiscalCode.getCustom1());
		stmt.setString(CUSTOM2_COL, fiscalCode.getCustom2());
		stmt.setString(CUSTOM3_COL, fiscalCode.getCustom3());

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
	protected PreparedStatement createSelectUIDStatement(Connection conn) throws SQLException {
		String sql = "select objid from bck_fiscal_code where fiscal_code = ?";
		return conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createDeleteStatement(Connection)
	 */
	protected PreparedStatement createDeleteStatement(Connection conn) throws SQLException {
		String sql = "delete from bck_fiscal_code where fiscal_code = ?";
		return conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#setAlternateKey(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity obj) throws SQLException {
		_stmt.setString(1, ((FiscalCode) obj).getFiscalCode());
	}

	protected String getSelectAllQuery() {
		return "select fiscal_code, code_description, custom_1, custom_2, custom_3, objid from bck_fiscal_code";
	}

	protected PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException {
		FiscalCode fiscalCode = new FiscalCode();
		fiscalCode.setUid(_rset.getLong(OBJID_COL));
		fiscalCode.setFiscalCode(_rset.getString(FISCAL_CODE_COL));
		fiscalCode.setCodeDescription(_rset.getString(CODE_DESCRIPTION_COL));
		fiscalCode.setCustom1(_rset.getString(CUSTOM1_COL));
		fiscalCode.setCustom2(_rset.getString(CUSTOM2_COL));
		fiscalCode.setCustom3(_rset.getString(CUSTOM3_COL));
		return fiscalCode;
	}
}
