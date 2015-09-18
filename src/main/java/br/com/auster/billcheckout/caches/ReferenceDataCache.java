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
 * Created on 10/11/2006
 */
package br.com.auster.billcheckout.caches;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.common.stats.ProcessingStats;
import br.com.auster.common.stats.StatsMapping;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.om.reference.facade.ConfigurationException;
import br.com.auster.om.reference.facade.ReferenceFacades;

/**
 * This is an abstract cache for Billcheckout enabled VOs.
 * Acctually this class behaves as a hub for parameter tables cache.
 * Internally it uses a ConcurrentHashMap to keep track of all caches registered in the configuration
 * 
 * @author mtengelm
 * @version $Id$
 */
public abstract class ReferenceDataCache implements ReferenceFacades {
	
	
	
	// ---------------------------
	// Class constants
	// ---------------------------
	
	private static final Logger log = Logger.getLogger(ReferenceDataCache.class);

	protected static final int DEFAULT_CACHE_SIZE = 1000;
	protected static final ReadWriteLock lock = new ReentrantReadWriteLock();

    protected static final String LAZY_ATTR = "lazy-cache";
	protected static final String CACHE_SIZE_ATTR = "cache-size";
	protected static final String USE_ALTERNATE_KEY_ATTR = "use-alternate";
	protected static final String DB_ELEMENT = "database";
	protected static final String POOL_NAME_ATTR = "pool-name";
	
	
	
	// ---------------------------
	// Instance variables
	// ---------------------------
	
	protected Set<CacheableKey> notFoundSet;
	protected LRUMap cache;
	protected String poolName;
	protected boolean useAlternate;
	protected Object refreshToken;
	


	// ---------------------------
	// Constructors
	// ---------------------------
	
	public ReferenceDataCache() {
		log.debug("Creating a new Instance of ReferenceDataCache");
		this.notFoundSet = new HashSet<CacheableKey>();
	}

	
	
	// ---------------------------
	// Public methods
	// ---------------------------
	
	public void configure(String _configurationFile) throws ConfigurationException {
		this.configure(_configurationFile, false);
	}
	
	public void configure(String _configurationFile, boolean _encrypted) throws ConfigurationException {
		try {
			Element conf = DOMUtils.openDocument(_configurationFile, _encrypted);
			this.configure(conf);
		} catch (SAXException saxe) {
			throw new ConfigurationException(saxe);
		} catch (ParserConfigurationException pce) {
			throw new ConfigurationException(pce);
		} catch (IOException ioe) {
			throw new ConfigurationException(ioe);			
		} catch (GeneralSecurityException gse) {
			throw new ConfigurationException(gse);
		}
	}
	
	/**
	 * Initializes the cache instance
	 */
	public void configure(Element config) throws ConfigurationException {
		try {
			lock.writeLock().lockInterruptibly();
			log.debug("Configuring cache instance for " + this.getClass().getSimpleName() + " instances.");
			Element dbElement = DOMUtils.getElement(config, DB_ELEMENT, true);
			poolName = DOMUtils.getAttribute(dbElement, POOL_NAME_ATTR, true);
			if ((poolName == null) || (poolName.trim().length() == 0)) {
				throw new ConfigurationException("pool-name was not informed.");
			}
			boolean lazy = DOMUtils.getBooleanAttribute(config, LAZY_ATTR, false);
			this.useAlternate = DOMUtils.getBooleanAttribute(config, USE_ALTERNATE_KEY_ATTR);
			int cacheSize = DOMUtils.getIntAttribute(config, CACHE_SIZE_ATTR, false);
			if (cacheSize <= 0) { cacheSize = DEFAULT_CACHE_SIZE; }
			this.cache = new LRUMap(cacheSize, false);
			if (!lazy) {
				log.debug("populating cache with all instances in database.");
				this.populateCache();
			}
		} catch (InterruptedException ie) {
			throw new ConfigurationException(ie);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Returns the configured JDBC pool name
	 */
	public String getPoolName() {
		return this.poolName;
	}

	/**
	 * This method returns the hole cache map. Be carefull that right after this method returns, 
	 * 	the map can be changed by any thread and any modifications made any other thread can cause 
	 * 	an exception on requesting thread and vice-versa.
	 * 
	 * Also, note that the returned map is unmodifiable.
	 * 
	 * @param key
	 * @return
	 */
	public Map getCacheImage() {
		return Collections.unmodifiableMap((LRUMap)this.cache);
	}
	
	public void updateCacheImage(LRUMap _map) {
		try {
			lock.writeLock().lockInterruptibly();
			cache = _map;
		} catch (InterruptedException ie) {
			throw new IllegalStateException(ie);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public CacheableVO getFromCache(CacheableKey key) {
		try {
			lock.readLock().lockInterruptibly();
			log.debug("Searching for key " + key);
			CacheableVO vo = (CacheableVO) this.cache.get(key);
			if (vo == null) {
				log.debug("Key " + key + " not found in cache. Going to database");
				if (this.notFoundSet.contains(key)) {
					log.debug("Key " + key + " found in not-found set. Resuming search.");
					return null;
				}
				try {
					this.loadFromDatabase(key);
				} catch (DataNotFoundException e) {
					this.notFoundSet.add(key);
					return null;
				}
				vo = (CacheableVO) this.cache.get(key);
				if (vo == null) {
					log.debug("Key " + key + " not found in database. Added to not-found set");
					this.notFoundSet.add(key);
				} else {
					log.debug("Key " + key + " found in database.");
				}
			}
			return vo;
		} catch (InterruptedException ie) {
			throw new IllegalStateException(ie);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void putInCache(CacheableKey key, CacheableVO obj) {
//		try {
//			lock.writeLock().lockInterruptibly();
			cache.put(key, obj);
			log.debug("Cache has size of:" + this.cache.size() + " and a max size of:"+ this.cache.maxSize() + 
					   ".Obj:[" + obj + "]");		
//		} catch (InterruptedException ie) {
//			throw new IllegalStateException(ie);
//		} finally {
//			lock.writeLock().unlock();
//		}
	}
	
	public boolean isUseAlternateKey() {
		return useAlternate;
	}

	public void refreshCache(Object _refreshToken) {
		try {
			if ((this.refreshToken == null) || (_refreshToken == null) || (!this.refreshToken.equals(_refreshToken))) {
				log.debug("New refresh token found.");
				this.clearCache();
			}
		} catch (ClassCastException cce) {
			log.warn("Previous refresh token differs in type from current one, resulting in a ClassCastException. Will force clearing cached objects.");
			this.clearCache();
		}
		this.refreshToken = _refreshToken;
	}

	public void clearCache() {
		log.debug("Cache map and notFound set cleared.");
		this.cache.clear();
		this.notFoundSet.clear();
	}

	// ---------------------------
	// Non-Public methods
	// ---------------------------
	
	protected Connection getConnection() throws SQLException, NamingException {
		return SQLConnectionManager.getInstance(this.poolName).getConnection();
	}

	protected void populateCache() {
		Connection conn=null;
		PreparedStatement stmt=null;
		StatsMapping stats = ProcessingStats.starting(getClass(), "populateCache()");
		try {
			conn = this.getConnection();
			stmt = conn.prepareStatement(this.getNonLazySQL());
			loadIntoCache(stmt, false);
		} catch (Exception e) {
			log.error("Could not load non-lazy cache", e);
		} finally {
			stats.finished();
			try {
				if (stmt != null) {	stmt.close(); }
				if (conn != null) {	conn.close(); }
			} catch (SQLException e) {
				log.error("Exception populating cache", e);
			} 
		}		
	}

	protected void loadFromDatabase(CacheableKey _key) {
		Connection conn=null;
		PreparedStatement stmt=null;
		StatsMapping stats = ProcessingStats.starting(getClass(), "loadFromDatabase()");
		try {
			conn = this.getConnection();
			if (this.useAlternate) {
				stmt = conn.prepareStatement(this.getAlternateLazySQL());
				this.setAlternateLazySQLParameters(stmt, _key);
			} else {
				stmt = conn.prepareStatement(this.getLazySQL());
				this.setLazySQLParameters(stmt, _key);
			}
			loadIntoCache(stmt, true);
		} catch (Exception e) {
			log.error("Could not load non-lazy cache", e);
		} finally {
			stats.finished();
			try {
				if (stmt != null) {	stmt.close(); }
				if (conn != null) {	conn.close(); }
			} catch (SQLException e) {
				log.error("Exception populating cache", e);
			} 
		}		
	}
	
	protected void loadIntoCache(PreparedStatement _stmt, boolean _overflowAllowed) throws SQLException {
		ResultSet rs = null;
		try {
			rs = _stmt.executeQuery();
			while (rs.next()) {
				if (!readAndAddToCache(rs, _overflowAllowed)) {
					break;
				}
			}
		} finally {			
			try {
				if (rs != null) {	rs.close();	}
			} catch (SQLException e) {
				log.error("Exception populating cache", e);
			} 
		}		
	}

	protected final boolean readAndAddToCache(ResultSet _rset, boolean _overflowAllowed) throws SQLException {
		CacheableVO obj = createVO(_rset);
		if (obj == null) {
			throw new DataNotFoundException();
		}
		if ((this.cache.isFull()) && (!_overflowAllowed)) {
			log.warn("Cache for key is full. Leaving the hungry populate.");
			return false;
		}
		this.putInCache((this.isUseAlternateKey())? obj.getAlternateKey() : obj.getKey(), obj);
		return true;
	}
	

	
	// ---------------------------
	// Abstract methods
	// ---------------------------

	/**
	 * Returns the SQL statement used to read information when cache is not defined as not lazy.
	 */
	protected abstract String getNonLazySQL();

	/**
	 * Returns the SQL statement used to read information when cache is defined as lazy.
	 * 
	 * This method is for use when the natural key is enabled.
	 */
	protected abstract String getLazySQL();
	
	/**
	 * Sets the attributes which are stored in the key, into the lazy sql statement
	 * 
	 * This method is for use when the natural key is enabled.
	 */
	protected abstract void setLazySQLParameters(PreparedStatement _stmt, CacheableKey _key) throws SQLException ;

	/**
	 * Returns the SQL statement used to read information when cache is defined as lazy.
	 * 
	 * This method is for use when the alternate key is enabled.
	 */
	protected abstract String getAlternateLazySQL();
	
	/**
	 * Sets the attributes which are stored in the key, into the lazy sql statement.
	 * 
	 * This method is for use when the alternate key is enabled.
	 */
	protected abstract void setAlternateLazySQLParameters(PreparedStatement _stmt, CacheableKey _key) throws SQLException ;
	
	
	/**
	 * Instantiates the {@link CacheableVO} instance for this cache, using the specified result set.
	 */
	protected abstract CacheableVO createVO(ResultSet _rset) throws SQLException;	
}
