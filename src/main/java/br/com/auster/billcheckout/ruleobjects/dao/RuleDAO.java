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
package br.com.auster.billcheckout.ruleobjects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO;
import br.com.auster.billcheckout.ruleobjects.Rule;
import br.com.auster.om.reference.PKEnabledEntity;

/**
 * @author framos
 * @version $Id$
 */
public class RuleDAO extends BaseReadOnlyDimensionDAO {

	
	
	// ---------------------------
	// Class constants - SQL Statements
	// ---------------------------
	
	private static final String SQL_QUERYALL = 
		"select rule_code, rule_name, description, custom_1, custom_2, custom_3, objid from bck_rule";
	private static final String SQL_QUERY_OBJID = "select objid from bck_rule where rule_code = ?";
	private static final String SQL_QUERY_SINGLERULE = SQL_QUERYALL + " where rule_code = ?";
	private static final String SQL_QUERY_TEMPLATEFILE = 
		"select a.template_file from bck_rule_template a " +
		" join bck_rule b on a.rule_uid = b.objid where a.rule_code = ?";
	
	
	
	// ---------------------------
	// Class constants
	// ---------------------------

	protected static final int OBJID_COL = 7; 
	

	
	// ---------------------------
	// Public specialized methods
	// ---------------------------
	
	/**
	 * Loads the rule basic information (name, code, description, etc.) for a single rule code. If such rule is not
	 * 	 registered in the correct tables, then <code>null</code> is retured.
	 *   
	 * @param _ruleCode the rule unique code
	 * 
	 * @return Arule model instance, or <code>null</code>
	 * 
	 * @throws SQLException if any error related to database communication was detected 
	 */
	public Rule loadSingleRule(String _ruleCode) throws SQLException {
		if (this.isConnectionClosed()) {
			 // TODO i18n
			 throw new IllegalStateException("Cannot reset on a closed connection");
		 }
		 ResultSet rset = null;
		 PreparedStatement stmt = null;
		 try {
			 stmt = this.conn.prepareStatement(SQL_QUERY_SINGLERULE);
			 stmt.setString(1, _ruleCode);
			 rset = stmt.executeQuery();
			 if (rset.next()) {
				 return (Rule) loadFromResultset(rset);
			 }
		 } finally {
			 if (rset != null) { rset.close(); }
			 if (stmt != null) { stmt.close(); }
		 }
		 return null;
	}

	/**
	 * Loads the name (path and filename) of the template XSL file which should be used to build final
	 *   versions of the specified rule. If such rule is not template-enabled, then <code>null</code> is
	 *   returned.
	 *   
	 * @param _ruleCode the rule unique code
	 * 
	 * @return the XSL file path and name, or <code>null</code>
	 * 
	 * @throws SQLException if any error related to database communication was detected 
	 */
	public String loadRuleTemplate(String _ruleCode) throws SQLException {
		if (this.isConnectionClosed()) {
			 // TODO i18n
			 throw new IllegalStateException("Cannot reset on a closed connection");
		 }
		 ResultSet rset = null;
		 PreparedStatement stmt = null;
		 try {
			 stmt = this.conn.prepareStatement(SQL_QUERY_TEMPLATEFILE);
			 stmt.setString(1, _ruleCode);
			 rset = stmt.executeQuery();
			 if (rset.next()) {
				 return rset.getString(1);
			 }
		 } finally {
			 if (rset != null) { rset.close(); }
			 if (stmt != null) { stmt.close(); }
		 }
		 return null;
	}
	
	

	// ---------------------------
	// Protected interfaced methods
	// ---------------------------
	
	protected PreparedStatement createSelectUIDStatement(Connection _conn) throws SQLException {
		return _conn.prepareStatement(SQL_QUERY_OBJID);
	}
	
	protected void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException {
		_stmt.setString(1, ((Rule)_dimensionObj).getCode());
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#createKey(PKEnabledEntity)
	 */
	protected final Object createKey(PKEnabledEntity _dimensionObj) {
		return (_dimensionObj == null ? null : ((Rule)_dimensionObj).getKey());
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#getSelectAllQuery()
	 */
	protected final String getSelectAllQuery() { return SQL_QUERYALL; }
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#loadFromResultset(ResultSet)
	 */
	protected final PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException {
		Rule dm = new Rule();
		dm.setUid(_rset.getLong(OBJID_COL));
		int colCount = 1;
		dm.setCode(_rset.getString(colCount++));
		dm.setShortName(_rset.getString(colCount++));
		dm.setDescription(_rset.getString(colCount++));
		dm.setCustom1(_rset.getString(colCount++));
		dm.setCustom2(_rset.getString(colCount++));
		dm.setCustom3(_rset.getString(colCount++));
		return dm;
	}	
}
