/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.persistence.FetchCriteria;
import br.com.auster.persistence.jdbc.JDBCQueryHelper;


/**
 * @author framos
 * @version $Id$
 */
public abstract class BaseReadOnlyDAO implements ReadOnlyDAO {

	
	
	// ---------------------------
	// Methods inherited from Read
	//		OnlyDAO interface 	
	// ---------------------------
	
	/**
	 * @see ReadOnlyDAO#selectAll(Connection)
	 */
	public Collection<CacheableVO> selectAll(Connection _conn) throws SQLException {
		return selectAll(_conn, null);
	}

	/**
	 * @see ReadOnlyDAO#selectAll(Connection, FetchCriteria)
	 */
	public Collection<CacheableVO> selectAll(Connection _conn, FetchCriteria _criteria) throws SQLException {
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = _conn.createStatement();
			String sql = JDBCQueryHelper.applyFetchParameters(_conn, getSelectAllStatement(), _criteria);
			rset = stmt.executeQuery(sql);
			Collection<CacheableVO> results = new LinkedList<CacheableVO>(); 
			while (rset.next()) {
				results.add(getObjectFromResultSet(rset));
			}
			return results;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
	/**
	 * @see ReadOnlyDAO#selectByKey(Connection, CacheableKey)
	 */
	public Collection<CacheableVO> selectByKey(Connection _conn, CacheableKey _key) throws SQLException {
		return selectBySomeKey(_conn, _key, true);
	}
	
	/**
	 * @see ReadOnlyDAO#selectByAlternateKey(Connection, CacheableKey)
	 */
	public Collection<CacheableVO> selectByAlternateKey(Connection _conn, CacheableKey _key) throws SQLException {
		return selectBySomeKey(_conn, _key, false);
	}
	

	
	// ---------------------------
	// Protected methods defined
	//		in this class
	// ---------------------------
	
	/**
	 * This method helps abstracting the SQL statement needed to select all rows from one or more tables,
	 * 	which represent a single implementation of {@link CacheableVO}.
	 */
	protected abstract String getSelectAllStatement();

	/**
	 * This method helps abstracting the SQL statement needed to select a specific row from one or more tables,
	 * 	which represent a single implementation of {@link CacheableVO}. This row is identified uniquely by the
	 *  {@code key} attribute of such {@link CacheableVO} implementation.
	 */
	protected abstract String getSelectByKeyStatement();
	
	/**
	 * This method helps abstracting the SQL statement needed to select a specific row from one or more tables,
	 * 	which represent a single implementation of {@link CacheableVO}. This row is identified uniquely by the
	 *  {@code alternateKey} attribute of such {@link CacheableVO} implementation.
	 */
	protected abstract String getSelectByAlternateKeyStatement();

	

	// ---------------------------
	// Private methods
	// ---------------------------

	/**
	 * This method eliminates the code duplicity needed to implement both {@link #selectByKey(Connection, CacheableKey)} and
	 *    {@link #selectByAlternateKey(Connection, CacheableKey)} methods.
	 */
	private final Collection<CacheableVO> selectBySomeKey(Connection _conn, CacheableKey _key, boolean _pkey) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			if (_pkey) {
				stmt = _conn.prepareStatement( getSelectByKeyStatement() );
				setKeyIntoStatement(stmt, _key, 1);
			} else {
				stmt = _conn.prepareStatement( getSelectByAlternateKeyStatement() );
				setAlternateKeyIntoStatement(stmt, _key, 1);
			}
			rset = stmt.executeQuery();
			Collection<CacheableVO> results = new LinkedList<CacheableVO>(); 
			while (rset.next()) {
				results.add(getObjectFromResultSet(rset));
			}
			return results;
		} finally {
			if (rset != null) { rset.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
}
