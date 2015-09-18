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
import java.sql.SQLException;
import java.util.Collection;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;

/**
 * 
 * @author framos
 * @version $Id$
 */
public interface UpdatableDAO extends ReadOnlyDAO {

	/**
	 * 
	 * @param _conn
	 * @param _vo
	 * @return
	 * @throws SQLException
	 */
	public int insert(Connection _conn, CacheableVO _vo) throws SQLException;
	
	/**
	 * 
	 * @param _conn
	 * @param _voCollection
	 * @return
	 * @throws SQLException
	 */
	public int insertAll(Connection _conn, Collection<CacheableVO> _voCollection) throws SQLException;

	/**
	 * 
	 * @param _conn
	 * @param _voList
	 * @return
	 * @throws SQLException
	 */
	public int updateAll(Connection _conn, Collection<CacheableVO> _voList) throws SQLException;

	/**
	 * 
	 * @param _conn
	 * @param _key
	 * @return
	 * @throws SQLException
	 */
	public int updateByKey(Connection _conn, CacheableVO _vo) throws SQLException;

	/**
	 * 
	 * @param _conn
	 * @param _key
	 * @return
	 * @throws SQLException
	 */
	public int updateByAlternateKey(Connection _conn, CacheableVO _vo) throws SQLException;

	/**
	 * 
	 * @param _conn
	 * @return
	 * @throws SQLException
	 */
	public int deleteAll(Connection _conn) throws SQLException;
	
	/**
	 * 
	 * @param _conn
	 * @param _key
	 * @return
	 * @throws SQLException
	 */
	public int deleteByKey(Connection _conn, CacheableKey _key) throws SQLException;

	/**
	 * 
	 * @param _conn
	 * @param _key
	 * @return
	 * @throws SQLException
	 */
	public int deleteByAlternateKey(Connection _conn, CacheableKey _key) throws SQLException;
	
	/**
	 * 
	 * @param _stmt
	 * @param _vo
	 * @return
	 * @throws SQLException
	 */
	public int setObjectInStatement(PreparedStatement _stmt, CacheableVO _vo) throws SQLException;
}
