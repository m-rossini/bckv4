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
import java.sql.SQLException;

import br.com.auster.om.reference.PKEnabledEntity;


/**
 * @author framos
 * @version $Id$
 */
public abstract class BaseUpdatableDimensionDAO extends BaseReadOnlyDimensionDAO implements UpdatableDimensionDAO {

	

	protected PreparedStatement updateStmt;
	protected PreparedStatement insertStmt;
	protected PreparedStatement deleteStmt;
	
	
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.ReadOnlyDimensionDAO#getUID(br.com.auster.om.reference.PKEnabledEntity)
	 */
	public long getUID(Connection _conn, PKEnabledEntity _dimensionObj) throws SQLException {
		long uid = 0;
		try {
			uid = super.getUID(_conn, _dimensionObj);
		} catch (IllegalStateException ise) {
			// first time for updatable DAOs might need to run reset 
			this.reset(_conn);
			uid = super.getUID(_conn, _dimensionObj);
		}
		return uid;
//		if (uid == 0) {
//			this.insert(_dimensionObj);
//		}
//		this.reset(_conn);
//		return super.getUID(_conn, _dimensionObj);
	}
	
	public boolean insert(PKEnabledEntity _dimensionObj) throws SQLException {
		 if (this.insertStmt == null) {
			 // TODO i18n
			 throw new IllegalStateException("cannot execute queries before running reset");
		 } else if (this.isConnectionClosed()) {
			 // TODO i18n
			 throw new IllegalStateException("cannot execute queries before on a closed connection");
		 }
		 setFieldsExceptUID(this.insertStmt, _dimensionObj);
		 this.insertStmt.addBatch();
		 this.insertStmt.clearParameters();
		 return true;		
	}
	
	public boolean update(PKEnabledEntity _dimensionObj) throws SQLException {
		 if (this.updateStmt == null) {
			 // TODO i18n
			 throw new IllegalStateException("cannot execute queries before running reset");
		 } else if (this.isConnectionClosed()) {
			 // TODO i18n
			 throw new IllegalStateException("cannot execute queries before on a closed connection");
		 }
		 setFieldsExceptUID(this.updateStmt, _dimensionObj);
		 setUIDField(_dimensionObj);
		 this.updateStmt.addBatch();
		 this.updateStmt.clearParameters();
		 return true;		
	}

	public boolean delete(PKEnabledEntity _dimensionObj) throws SQLException {
		 if (this.deleteStmt == null) {
			 // TODO i18n
			 throw new IllegalStateException("cannot execute queries before running reset");
		 } else if (this.isConnectionClosed()) {
			 // TODO i18n
			 throw new IllegalStateException("cannot execute queries before on a closed connection");
		 }
		 setAlternateKey(this.deleteStmt, _dimensionObj);
		 return this.deleteStmt.execute();
	}
	
	/**
	 * @see br.com.auster.billcheckout.consequence.sql.ReadOnlyDimensionDAO#reset(java.sql.Connection)
	 */
	public void reset(Connection _conn) throws SQLException {
		// only closed previous statements if current connection is not closed yet.
		// according to JDBC spec, statments are closed when the related connection is also closed
		if (! this.isConnectionClosed()) {
			if (this.selectStmt != null) {
				this.selectStmt.close();
			}
			// reseting insert statement
			if (this.insertStmt != null) {
				this.insertStmt.executeBatch();
				this.insertStmt.close();
			}
			// reseting update statement
			if (this.updateStmt != null) {
				this.updateStmt.executeBatch();
				this.updateStmt.close();
			}
			// reseting delete statement
			if (this.deleteStmt != null) {
				this.deleteStmt.executeBatch();
				this.deleteStmt.close();
			}
		}
		this.conn = _conn;
		// this is to enforce that the new connection is NOT closed yet
		if (this.isConnectionClosed()) {
			// TODO i18n
			throw new IllegalStateException("Cannot reset on a closed connection");
		}
		this.selectStmt = this.createSelectUIDStatement(this.conn);
		this.insertStmt = this.createInsertStatement(_conn);
		this.updateStmt = this.createUpdateStatement(_conn);
		this.deleteStmt = this.createDeleteStatement(_conn);
	}	

	
	protected abstract PreparedStatement createInsertStatement(Connection _conn) throws SQLException;
	protected abstract PreparedStatement createUpdateStatement(Connection _conn) throws SQLException;
	protected abstract PreparedStatement createDeleteStatement(Connection _conn) throws SQLException;
	
	protected abstract void setFieldsExceptUID(PreparedStatement _stmt, PKEnabledEntity _dimensionObj) throws SQLException;
	protected abstract void setUIDField(PKEnabledEntity _dimensionObj) throws SQLException;
}
