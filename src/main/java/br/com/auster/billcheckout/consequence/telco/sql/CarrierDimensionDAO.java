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
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.om.reference.PKEnabledEntity;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author framos
 * @version $Id$
 */
public class CarrierDimensionDAO extends BaseUpdatableDimensionDAO {

	
	
	protected static final int OBJID_COL = 7;

	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createInsertStatement(java.sql.Connection)
	 */
	protected PreparedStatement createInsertStatement(Connection _conn) throws SQLException {
		String sql = "insert into bck_carrier_dm (carrier_code, carrier_state, carrier_name, custom_1, custom_2, custom_3, objid) " 
			+ "values (?, ?, ?, ?, ?, ?, " + JDBCSequenceHelper.translate(_conn, "bck_dimensions_uid") + ")";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createUpdateStatement(java.sql.Connection)
	 */
	protected PreparedStatement createUpdateStatement(Connection _conn)	throws SQLException {
		String sql = "update bck_carrier_dm set carrier_code = ?, carrier_state = ?, carrier_name = ?, " 
			+ " custom_1 = ?, custom_2 = ?, custom_3 = ? where objid = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setFieldsExceptUID(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected final void setFieldsExceptUID(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException {
		CarrierDimension carrier = (CarrierDimension) _dimensionObj;
		int colCount=1;
		_stmt.setString(colCount++, carrier.getCarrierCode());
		_stmt.setString(colCount++, carrier.getCarrierState());
		_stmt.setString(colCount++, carrier.getCarrierCompany());
		_stmt.setString(colCount++, carrier.getCustom1());
		_stmt.setString(colCount++, carrier.getCustom2());
		_stmt.setString(colCount++, carrier.getCustom3());
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
		String sql = "select objid from bck_carrier_dm where carrier_code = ? and carrier_state = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#createDeleteStatement(java.sql.Connection)
	 */
	protected PreparedStatement createDeleteStatement(Connection _conn) throws SQLException {
		String sql = "delete from bck_carrier_dm where carrier_code = ? and carrier_state = ?";
		return _conn.prepareStatement(sql);
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#setAlternateKey(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException {
		_stmt.setString(1, ((CarrierDimension)_dimensionObj).getCarrierCode());
		_stmt.setString(2, ((CarrierDimension)_dimensionObj).getCarrierState());
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#createKey(PKEnabledEntity)
	 */
	protected final Object createKey(PKEnabledEntity _dimensionObj) {
		return (_dimensionObj == null ? null : ((CarrierDimension)_dimensionObj).getKey());
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#getSelectAllQuery()
	 */
	protected final String getSelectAllQuery() {
		return "select carrier_code, carrier_state, carrier_name, custom_1, custom_2, custom_3, objid from bck_carrier_dm";
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#loadFromResultset(ResultSet)
	 */
	protected final PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException {
		CarrierDimension dm = new CarrierDimension();
		dm.setUid(_rset.getLong(OBJID_COL));
		int colCount = 1;
		dm.setCarrierCode(_rset.getString(colCount++));
		dm.setCarrierState(_rset.getString(colCount++));
		dm.setCarrierCompany(_rset.getString(colCount++));
		dm.setCustom1(_rset.getString(colCount++));
		dm.setCustom2(_rset.getString(colCount++));
		dm.setCustom3(_rset.getString(colCount++));
		return dm;
	}
	
}
