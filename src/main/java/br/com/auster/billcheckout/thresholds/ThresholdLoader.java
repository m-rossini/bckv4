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
 * Created on 16/04/2007
 */
package br.com.auster.billcheckout.thresholds;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.common.io.IOUtils;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.om.reference.facade.ConfigurationException;
import br.com.auster.om.reference.facade.ReferenceFacades;

/**
 * @author framos
 * @version $Id$
 *
 */
public class ThresholdLoader implements ReferenceFacades {

	
	
	// ---------------------------
	// Class constants
	// ---------------------------
	
	private static final Logger log = Logger.getLogger(ThresholdLoader.class);
	private static final Lock configurationLock = new ReentrantLock();

	protected static final String HIBERNATE_FILE_ATTR = "factory-config";
	
	
	
	// ---------------------------
	// Instance variables
	// ---------------------------
	
	protected Map<Class, Set<CacheableVO>> cache;
	protected SessionFactory factory;
	protected Object refreshToken;

	

	// ---------------------------
	// Public methods
	// ---------------------------
	
	public void configure(Element _config) throws ConfigurationException {
		try {
			configurationLock.lockInterruptibly();
			// getting hibernate configuration file path
			String configFile = DOMUtils.getAttribute(_config, HIBERNATE_FILE_ATTR, true);
			// getting factory from configuration file
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(false);
			Document doc = dbf.newDocumentBuilder().parse(IOUtils.openFileForRead(configFile));
			Configuration cfg = new Configuration();
			cfg.configure(doc);
			this.factory = cfg.buildSessionFactory();
			// init. cache and not found sets
			this.cache = new HashMap<Class, Set<CacheableVO>>();
			// init. refresh token
			this.refreshToken = null;
		} catch (InterruptedException ie) {
			throw new ConfigurationException(ie);
		} catch (ParserConfigurationException pce) {
			throw new ConfigurationException(pce);
		} catch (SAXException saxe) {
			throw new ConfigurationException(saxe);
		} catch (IOException ioe) {
			throw new ConfigurationException(ioe);
		} finally {
			configurationLock.unlock();
		}
	}

	public void configure(String _config) throws ConfigurationException {
		this.configure(_config, false);
	}

	public void configure(String _config, boolean _encrypted) throws ConfigurationException {
		try {
			Element root = DOMUtils.openDocument(_config, _encrypted);
			this.configure(root);
		} catch (GeneralSecurityException gse) {
			throw new ConfigurationException(gse);
		} catch (ParserConfigurationException pce) {
			throw new ConfigurationException(pce);
		} catch (SAXException saxe) {
			throw new ConfigurationException(saxe);
		} catch (IOException ioe) {
			throw new ConfigurationException(ioe);
		}
	}

	public Collection<CacheableVO> getFromCache(Class _klass) {
		try {
			log.debug("Searching for cached thresholds for class " + _klass);
			Set classCache = this.cache.get(_klass);
			if (classCache == null) {
				classCache = new HashSet();
				this.cache.put(_klass, classCache);
				this.loadFromDatabase(_klass);
			}
			classCache = this.cache.get(_klass);
			return classCache;
		} catch (ClassNotFoundException cnfe) {
			throw new IllegalStateException(cnfe);
		} catch (InvocationTargetException ite) {
			throw new IllegalStateException(ite);
		} catch (IllegalAccessException iae) {
			throw new IllegalStateException(iae);
		} catch (NoSuchMethodException nsme) {
			throw new IllegalStateException(nsme);
		}
			
	}
	
	public void putInCache(CacheableKey key, CacheableVO obj) {
		throw new NoSuchMethodError("Operation not supported by this cache implementation.");
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
	}
	
	

	// ---------------------------
	// Public methods
	// ---------------------------
		
	protected void loadFromDatabase(Class _klass) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Session session = null;
		try {
			session = this.factory.openSession();
			Criteria criteria = session.createCriteria(_klass);
			List<CacheableVO> results = criteria.list();
			for (CacheableVO vo : results) {
				this.cache.get(_klass).add(vo);
			}
		} finally {
			if (session != null) { session.close(); }
		}
	}
}
