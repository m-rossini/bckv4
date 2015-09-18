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
package br.com.auster.billcheckout.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;



/**
 * @author framos
 * @version $Id$
 */
public abstract class BaseUpdatableDAO extends BaseReadOnlyDAO implements UpdatableDAO {

	
	
	// ---------------------------
	// Methods inherited from 
	//		UpdatableDAO interface 	
	// ---------------------------
	
	/**
	 * @see UpdatableDAO#insert(Connection, CacheableVO)
	 */
	public int insert(Connection _conn, CacheableVO _vo) throws SQLException {
		Collection<CacheableVO> collection = new LinkedList<CacheableVO>();
		collection.add(_vo);
		return insertAll(_conn, collection);
	}

	/**
	 * @see UpdatableDAO#insertAll(Connection, Collection)
	 */
	public int insertAll(Connection _conn, Collection<CacheableVO> _voList) throws SQLException {
		PreparedStatement stmt = null;
		int counter = 0;
		try {
			stmt = _conn.prepareStatement( getInsertStatement() );
			for (CacheableVO vo : _voList) {
				setObjectInStatement(stmt, vo);
				counter += stmt.executeUpdate();
			}
		} finally {
			if (stmt != null) { stmt.close(); }
		}
		return counter;
	}
	
	/**
	 * @see UpdatableDAO#updateAll(Connection) 
	 */
	public int updateAll(Connection _conn, Collection<CacheableVO> _voList) throws SQLException {
		// TODO i18n
		throw new UnsupportedOperationException("This method should only be implemented when really needed.");
	}

	/**
	 * @see UpdatableDAO#updateByKey(Connection, CacheableVO) 
	 */
	public int updateByKey(Connection _conn, CacheableVO _vo) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = _conn.prepareStatement( getUpdateByKeyStatement() );
			int lastPos = setObjectInStatement(stmt, _vo);
			setKeyIntoStatement(stmt, _vo.getKey(), lastPos);
			return stmt.executeUpdate();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}

	/**
	 * @see UpdatableDAO#updateByAlternateKey(Connection, CacheableVO) 
	 */
	public int updateByAlternateKey(Connection _conn, CacheableVO _vo) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = _conn.prepareStatement( getUpdateByAlternateKeyStatement() );
			int lastPos = setObjectInStatement(stmt, _vo);
			setAlternateKeyIntoStatement(stmt, _vo.getKey(), lastPos);
			return stmt.executeUpdate();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}
	
	/**
	 * @see UpdatableDAO#deleteAll(Connection)
	 */
	public int deleteAll(Connection _conn) throws SQLException {
		Statement stmt = null;
		try {
			stmt = _conn.createStatement();
			return stmt.executeUpdate( getDeleteAllStatement() );
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}
	
	/**
	 * @see UpdatableDAO#deleteByKey(Connection, br.com.auster.billcheckout.caches.CacheableKey)
	 */
	public int deleteByKey(Connection _conn, CacheableKey _key) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = _conn.prepareStatement( getDeleteByKeyStatement() );
			setKeyIntoStatement(stmt, _key, 0);
			return stmt.executeUpdate();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}

	/**
	 * @see UpdatableDAO#deleteByAlternateKey(Connection, CacheableKey)
	 */
	public int deleteByAlternateKey(Connection _conn, CacheableKey _key) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = _conn.prepareStatement( getDeleteByAlternateKeyStatement() );
			setAlternateKeyIntoStatement(stmt, _key, 0);
			return stmt.executeUpdate();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}	
	

	
	// ---------------------------
	// Protected methods defined
	//		in this class
	// ---------------------------
	
	/**
	 * This method helps abstracting the SQL statement needed to insert one {@link CacheableVO}
	 * into one or more tables, which represent the implementation of such value-object.
	 */
	protected abstract String getInsertStatement();
	
	/**
	 * This method helps abstracting the SQL statement needed to update one {@link CacheableVO}
	 * into one or more tables, which represent the implementation of such value-object. The row 
	 * is identified uniquely by the {@code key} attribute of this {@link CacheableVO} implementation.
	 */
	protected abstract String getUpdateByKeyStatement();
	
	/**
	 * This method helps abstracting the SQL statement needed to update one {@link CacheableVO}
	 * into one or more tables, which represent the implementation of such value-object. The row 
	 * is identified uniquely by the {@code alternateKey} attribute of this {@link CacheableVO} 
	 * implementation.
	 */
	protected abstract String getUpdateByAlternateKeyStatement();

	/**
	 * This method helps abstracting the SQL statement needed to delete all {@link CacheableVO}
	 * from one or more tables, which represent the implementation of such value-object.
	 */
	protected abstract String getDeleteAllStatement();
	
	/**
	 * This method helps abstracting the SQL statement needed to delete one {@link CacheableVO}
	 * from one or more tables, which represent the implementation of such value-object. The row 
	 * is identified uniquely by the {@code key} attribute of this {@link CacheableVO} implementation.
	 */
	protected abstract String getDeleteByKeyStatement();
	
	/**
	 * This method helps abstracting the SQL statement needed to delete one {@link CacheableVO}
	 * from one or more tables, which represent the implementation of such value-object. The row 
	 * is identified uniquely by the {@code alternateKey} attribute of this {@link CacheableVO} 
	 * implementation.
	 */
	protected abstract String getDeleteByAlternateKeyStatement();
}
