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
import br.com.auster.billcheckout.model.CarrierData;
import br.com.auster.om.reference.PKEnabledEntity;
import br.com.auster.persistence.jdbc.JDBCSequenceHelper;

/**
 * @author pvieira
 *
 */
public class CarrierDataDAO extends BaseUpdatableDimensionDAO {

	protected static final int CARRIER_FULL_NAME_COL = 1;
	protected static final int CARRIER_TAX_ID_COL = 2;
	protected static final int CARRIER_STATE_ENROLL_COL = 3;
	protected static final int CARRIER_CITY_ENROLL_COL = 4;
	protected static final int ADDR_STREET_COL = 5;
	protected static final int ADDR_NUMBER_COL = 6;
	protected static final int ADDR_COMPLEMENT_COL = 7;
	protected static final int ADDR_ZIP_COL = 8;
	protected static final int ADDR_CITY_COL = 9;
	protected static final int ADDR_WEB_COL = 10;
	protected static final int ADDR_EMAIL_COL = 11;
	protected static final int CARRIER_DM_COL = 12;
	protected static final int CUSTOM1_COL = 13;
	protected static final int CUSTOM2_COL = 14;
	protected static final int CUSTOM3_COL = 15;
	protected static final int OBJID_COL = 16;



	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createInsertStatement(java.sql.Connection)
	 */
	protected PreparedStatement createInsertStatement(Connection conn)
			throws SQLException {
		String sql = "insert into bck_carrier_data (carrier_full_name, carrier_tax_id, carrier_state_enroll_nbr, "
			+ "carrier_city_enroll_nbr, addr_street, addr_number, addr_complement, "
			+ "addr_zip, addr_city, addr_web, addr_email, carrier_dm_uid, "
			+ "custom_1, custom_2, custom_3, objid) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + JDBCSequenceHelper.translate(conn, "bck_dimensions_uid") + ")";

		return conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createUpdateStatement(java.sql.Connection)
	 */
	protected PreparedStatement createUpdateStatement(Connection conn)
			throws SQLException {

		String sql = "update bck_carrier_data set carrier_full_name = ?, carrier_tax_id = ?, carrier_state_enroll_nbr = ?, "
			+ "carrier_city_enroll_nbr = ?, addr_street = ?, addr_number = ?, addr_complement = ?, "
			+ "addr_zip = ?, addr_city = ?, addr_web = ?, addr_email = ?, carrier_dm_uid = ?, "
			+ "custom_1 = ?, custom_2 = ?, custom_3 = ?"
			+ "where objid = ?";

		return conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#setFieldsExceptUID(java.sql.PreparedStatement, br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setFieldsExceptUID(PreparedStatement stmt, PKEnabledEntity obj) throws SQLException {

		CarrierData carrierData = (CarrierData) obj;

		stmt.setString(CARRIER_FULL_NAME_COL, carrierData.getFullName());
		stmt.setString(CARRIER_TAX_ID_COL, carrierData.getTaxId());
		stmt.setString(CARRIER_STATE_ENROLL_COL, carrierData.getStateEnrollNumber());
		stmt.setString(CARRIER_CITY_ENROLL_COL, carrierData.getCityEnrollNumber());
		stmt.setString(ADDR_STREET_COL, carrierData.getAddressStreet());
		stmt.setString(ADDR_NUMBER_COL, carrierData.getAddressNumber());
		stmt.setString(ADDR_COMPLEMENT_COL, carrierData.getAddressComplement());
		stmt.setString(ADDR_ZIP_COL, carrierData.getAddressZip());
		stmt.setString(ADDR_CITY_COL, carrierData.getAddressCity());
		stmt.setString(ADDR_WEB_COL, carrierData.getAddressWeb());
		stmt.setString(ADDR_EMAIL_COL, carrierData.getAddressEmail());
		stmt.setLong(CARRIER_DM_COL, carrierData.getCarrierDimension().getUid());
		stmt.setString(CUSTOM1_COL, carrierData.getCustom1());
		stmt.setString(CUSTOM2_COL, carrierData.getCustom2());
		stmt.setString(CUSTOM3_COL, carrierData.getCustom3());

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
		String sql = "select objid from bck_carrier_data where carrier_dm_uid = ?";
		return conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseUpdatableDimensionDAO#createDeleteStatement(Connection)
	 */
	protected PreparedStatement createDeleteStatement(Connection conn) throws SQLException {
		String sql = "delete from bck_carrier_data where carrier_dm_uid = ?";
		return conn.prepareStatement(sql);
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#setAlternateKey(PreparedStatement, br.com.auster.om.reference.PKEnabledEntity)
	 */
	protected void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity obj) throws SQLException {
		_stmt.setLong(1, ((CarrierData) obj).getCarrierDimension().getUid());
	}

	protected String getSelectAllQuery() {
		return "select carrier_full_name, carrier_tax_id, carrier_state_enroll_nbr, " + 
		       "carrier_city_enroll_nbr, addr_street, addr_number, addr_complement, " +
		       "addr_zip, addr_city, addr_web, addr_email, carrier_dm_uid, custom_1, " + 
		       "custom_2, custom_3, objid from bck_carrier_data";
	}

	
	protected PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException {
		CarrierData carrierData = new CarrierData();
		carrierData.setUid(_rset.getLong(OBJID_COL));
		int col = 1;
		carrierData.setFullName(_rset.getString(col++));
		carrierData.setTaxId(_rset.getString(col++));
		carrierData.setStateEnrollNumber(_rset.getString(col++));
		carrierData.setCityEnrollNumber(_rset.getString(col++));
		carrierData.setAddressStreet(_rset.getString(col++));
		carrierData.setAddressNumber(_rset.getString(col++));
		carrierData.setAddressComplement(_rset.getString(col++));
		carrierData.setAddressZip(_rset.getString(col++));
		carrierData.setAddressCity(_rset.getString(col++));
		carrierData.setAddressWeb(_rset.getString(col++));
		carrierData.setAddressEmail(_rset.getString(col++));
		carrierData.setCarrierDimensionUid(_rset.getLong(col++));
		carrierData.setCustom1(_rset.getString(col++));
		carrierData.setCustom2(_rset.getString(col++));
		carrierData.setCustom3(_rset.getString(col++));
		
		return carrierData;
	}
}
