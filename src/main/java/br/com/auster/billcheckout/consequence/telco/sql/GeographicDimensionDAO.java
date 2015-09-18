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
import br.com.auster.billcheckout.consequence.telco.GeographicDimension;
import br.com.auster.om.reference.PKEnabledEntity;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author framos
 * @version $Id$
 */
public class GeographicDimensionDAO extends BaseUpdatableDimensionDAO {


	protected static final int OBJID_COL = 7;
	
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createInsertStatement(java.sql.Connection)
	 */
	protected PreparedStatement createInsertStatement(Connection _conn) throws SQLException {
		String sql = "insert into bck_geo_dm (geo_country, geo_state, geo_city, custom_1, custom_2, custom_3, objid) " 
			+ "values (?, ?, ?, ?, ?, ?, " + JDBCSequenceHelper.translate(_conn, "bck_dimensions_uid") + ")";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createUpdateStatement(java.sql.Connection)
	 */
	protected PreparedStatement createUpdateStatement(Connection _conn)	throws SQLException {
		String sql = "update bck_geo_dm set geo_country = ?, geo_state = ?, geo_city = ?, " 
			+ " custom_1 = ?, custom_2 = ?, custom_3 = ? where objid = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setFieldsExceptUID(br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setFieldsExceptUID(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException {
		GeographicDimension geo = (GeographicDimension) _dimensionObj;
		int colCount=1;
		_stmt.setString(colCount++, geo.getCountry());
		_stmt.setString(colCount++, geo.getState());
		_stmt.setString(colCount++, geo.getCity());
		_stmt.setString(colCount++, geo.getCustom1());
		_stmt.setString(colCount++, geo.getCustom2());
		_stmt.setString(colCount++, geo.getCustom3());
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
		String sql = "select objid from bck_geo_dm where geo_country = ? and geo_state = ? and geo_city = ?";
		return _conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createDeleteStatement(Connection)
	 */
	protected PreparedStatement createDeleteStatement(Connection _conn) throws SQLException {
		String sql = "delete from bck_geo_dm where geo_country = ? and geo_state = ? and geo_city = ?";
		return _conn.prepareStatement(sql);
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#setAlternateKey(PreparedStatement, PKEnabledEntity)
	 */
	protected void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException {
		_stmt.setString(1, ((GeographicDimension)_dimensionObj).getCountry());
		_stmt.setString(2, ((GeographicDimension)_dimensionObj).getState());
		_stmt.setString(3, ((GeographicDimension)_dimensionObj).getCity());
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#createKey(PKEnabledEntity)
	 */
	protected final Object createKey(PKEnabledEntity _dimensionObj) {
		if (_dimensionObj == null) {
			return null;
		}
		return ((GeographicDimension)_dimensionObj).getKey();
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#getSelectAllQuery()
	 */
	protected final String getSelectAllQuery() {
		return "select geo_country, geo_state, geo_city, custom_1, custom_2, custom_3, objid from bck_geo_dm";
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#loadFromResultset(ResultSet)
	 */
	protected final PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException {
		GeographicDimension dm = new GeographicDimension();
		dm.setUid(_rset.getLong(OBJID_COL));
		int colCount = 1;
		dm.setCountry(_rset.getString(colCount++));
		dm.setState(_rset.getString(colCount++));
		dm.setCity(_rset.getString(colCount++));
		dm.setCustom1(_rset.getString(colCount++));
		dm.setCustom2(_rset.getString(colCount++));
		dm.setCustom3(_rset.getString(colCount++));
		return dm;
	}
}
