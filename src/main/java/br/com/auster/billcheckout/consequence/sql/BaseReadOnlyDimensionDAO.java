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
package br.com.auster.billcheckout.consequence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import br.com.auster.om.reference.PKEnabledEntity;

/**
 * @author framos
 * @version $Id$
 */
public abstract class BaseReadOnlyDimensionDAO implements ReadOnlyDimensionDAO {

	
	protected Connection conn;
	protected PreparedStatement selectStmt;
	protected Map<Object, Long> cache;
	
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.ReadOnlyDimensionDAO#getUID(br.com.auster.om.reference.PKEnabledEntity)
	 */
	public long getUID(Connection _conn, PKEnabledEntity _dimensionObj) throws SQLException {
		 Long longUid = getFromCache(_dimensionObj);
		 if (longUid != null) {
			 return longUid.longValue();
		 }
		 if (this.selectStmt == null) {
			 // TODO i18n
			 throw new IllegalStateException("cannot execute queries before running reset");
		 } else if (this.isConnectionClosed()) {
			 // TODO i18n
			 throw new IllegalStateException("Cannot reset on a closed connection");
		 }
		 ResultSet rset = null;
		 try {
			 setAlternateKey(this.selectStmt, _dimensionObj);
			 rset = this.selectStmt.executeQuery();
			 if (rset.next()) {
				 long uid = rset.getLong(1);
				 putInCache(_dimensionObj, uid);
				 return uid;
			 }
		 } finally {
			 if (rset != null) { rset.close(); }
		 }
		 return 0;
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.ReadOnlyDimensionDAO#reset(java.sql.Connection)
	 */
	public void reset(Connection _conn) throws SQLException {
		// this is to prevent running commits on a closed connection
		if (! this.isConnectionClosed()) {
			if (this.selectStmt != null) {
				this.selectStmt.close();
			}
		}
		this.conn = _conn;
		// this is to enforce that the new connection is NOT closed yet
		if (this.isConnectionClosed()) {
			// TODO i18n
			throw new IllegalStateException("Cannot reset on a closed connection");
		}
		this.selectStmt = this.createSelectUIDStatement(this.conn);
	}

	public boolean isConnectionClosed() throws SQLException {
		if (this.conn == null) {
			return (this.selectStmt != null);
		}
		return this.conn.isClosed();  
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.ReadOnlyDimensionDAO#selectAll(Connection)
	 */
	public Collection<PKEnabledEntity> selectAll() throws SQLException {
		ResultSet rset = null;
		Statement stmt = null;
		if (this.conn == null) {
			 // TODO i18n
			 throw new IllegalStateException("cannot execute queries before running reset");
		}
		try {
			stmt = this.conn.createStatement();
			rset = stmt.executeQuery(getSelectAllQuery());
			Collection<PKEnabledEntity> result = new LinkedList<PKEnabledEntity>();
			while (rset.next()) {
				result.add(loadFromResultset(rset));
			}
			return result;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
	protected abstract void setAlternateKey(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException;	

	protected abstract PreparedStatement createSelectUIDStatement(Connection _conn) throws SQLException;

	protected abstract String getSelectAllQuery();
	
	protected abstract PKEnabledEntity loadFromResultset(ResultSet _rset) throws SQLException; 
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#getFromCache(PKEnabledEntity)
	 */
	protected Long getFromCache(PKEnabledEntity _dimensionObj) {
		return this.cache.get(createKey(_dimensionObj));
	}

	/**
	 * @see br.com.auster.billcheckout.consequence.sql.BaseReadOnlyDimensionDAO#putInCache(PKEnabledEntity, long)
	 */
	protected void putInCache(PKEnabledEntity _dimensionObj, long _value) {
		this.cache.put(createKey(_dimensionObj), new Long(_value));
	}
	
	protected Object createKey(PKEnabledEntity _dimensionObj) {
		if (_dimensionObj == null) {
			return null;
		} else {
			return new Long(_dimensionObj.getUid());
		}
	}
}
