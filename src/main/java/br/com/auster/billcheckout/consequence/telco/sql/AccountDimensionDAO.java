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
 * Created on 02/11/2006
 */
package br.com.auster.billcheckout.consequence.telco.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO;
import br.com.auster.billcheckout.consequence.telco.AccountDimension;
import br.com.auster.om.reference.PKEnabledEntity;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author framos
 * @version $Id$
 */
public class AccountDimensionDAO extends BaseUpdatableDimensionDAO {

	
	
	protected static final int OBJID_COL = 7;
	

	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createInsertStatement(java.sql.Connection)
	 */
	protected PreparedStatement createInsertStatement(Connection _conn) throws SQLException {
		String sql = "insert into bck_account_dm (account_type, holding_number, account_number, custom_1, custom_2, custom_3, objid) " +
				     "values ( ?, ?, ?, ?, ?, ?, "+ JDBCSequenceHelper.translate(_conn, "bck_dimensions_uid") +")";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createUpdateStatement(java.sql.Connection)
	 */
	protected PreparedStatement createUpdateStatement(Connection _conn)	throws SQLException {
		String sql = "update bck_account_dm set account_type = ?, holding_number = ?, account_number = ?, custom_1 = ?, " +
		             "custom_2 = ?, custom_3 = ? where objid = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setFieldsExceptUID(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setFieldsExceptUID(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException {
		AccountDimension acc = (AccountDimension) _dimensionObj;
		int colCount=1;
		_stmt.setString(colCount++, acc.getAccountType());
		_stmt.setString(colCount++, acc.getHoldingNumber());
		_stmt.setString(colCount++, acc.getAccountNumber());
		_stmt.setString(colCount++, acc.getCustom1());
		_stmt.setString(colCount++, acc.getCustom2());
		_stmt.setString(colCount++, acc.getCustom3());
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setUIDField(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setUIDField(PKEnabledEntity _dimensionObj) throws SQLException {
		this.updateStmt.setLong(OBJID_COL, _dimensionObj.getUid());	
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#createSelectUIDStatement(java.sql.Connection)
	 */
	protected PreparedStatement createSelectUIDStatement(Connection _conn) throws SQLException {
		String sql = "select for update objid from bck_account_dm where account_number = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createDeleteStatement(Connection)
	 */
	protected PreparedStatement createDeleteStatement(Connection _conn) throws SQLException {
		String sql = "delete from bck_account_dm where account_number = ?";
		return _conn.prepareStatement(sql);
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#setAlternateKey(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException {
		_stmt.setString(1, ((AccountDimension)_dimensionObj).getAccountNumber());
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#createKey(PKEnabledEntity)
	 */
	protected final Object createKey(PKEnabledEntity _dimensionObj) {
		return (_dimensionObj == null ? null : ((AccountDimension)_dimensionObj).getKey());
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#getSelectAllQuery()
	 */
	protected final String getSelectAllQuery() {
		return "select account_type, holding_number, account_number, custom_1, custom_2, custom_3, objid from bck_account_dm";
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#loadFromResultset(ResultSet)
	 */
	protected final PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException {
		AccountDimension dm = new AccountDimension();
		dm.setUid(_rset.getLong(OBJID_COL));
		int colCount = 1;
		dm.setAccountType(_rset.getString(colCount++));
		dm.setHoldingNumber(_rset.getString(colCount++));
		dm.setAccountNumber(_rset.getString(colCount++));
		dm.setCustom1(_rset.getString(colCount++));
		dm.setCustom2(_rset.getString(colCount++));
		dm.setCustom3(_rset.getString(colCount++));
		return dm;
	}
}
