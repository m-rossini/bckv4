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
import java.util.Collection;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.persistence.FetchCriteria;

/**
 * 
 * @author framos
 * @version $Id$
 */
public interface ReadOnlyDAO {

	/**
	 * 
	 * @param _conn
	 * @return
	 * @throws SQLException
	 */
	public Collection<CacheableVO> selectAll(Connection _conn) throws SQLException;

	/**
	 * 
	 * @param _conn
	 * @param _criteria
	 * @return
	 * @throws SQLException
	 */
	public Collection<CacheableVO> selectAll(Connection _conn, FetchCriteria _criteria) throws SQLException;
	
	/**
	 * 
	 * @param _conn
	 * @param _key
	 * @return
	 * @throws SQLException
	 */
	public Collection<CacheableVO> selectByKey(Connection _conn, CacheableKey _key) throws SQLException;

	/**
	 * 
	 * @param _stmt
	 * @param _key
	 * @param _pos
	 * @throws SQLException
	 */
	public void setKeyIntoStatement(PreparedStatement _stmt, CacheableKey _key, int _pos) throws SQLException;

	/**
	 * 
	 * @param _conn
	 * @param _key
	 * @return
	 * @throws SQLException
	 */
	public Collection<CacheableVO> selectByAlternateKey(Connection _conn, CacheableKey _key) throws SQLException;
	
	/**
	 * 
	 * @param _stmt
	 * @param _key
	 * @param _pos
	 * @throws SQLException
	 */
	public void setAlternateKeyIntoStatement(PreparedStatement _stmt, CacheableKey _key, int _pos) throws SQLException;

	/**
	 * 
	 * @param _rset
	 * @return
	 * @throws SQLException
	 */
	public CacheableVO getObjectFromResultSet(ResultSet _rset) throws SQLException;
	
}
