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
 * Created on 23/08/2006
 */
package br.com.auster.billcheckout.consequence.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import br.com.auster.billcheckout.consequence.ConsequenceDimensionsFacade;
import br.com.auster.billcheckout.consequence.DimensionQueryException;
import br.com.auster.billcheckout.ruleobjects.Rule;
import br.com.auster.common.util.I18n;
import br.com.auster.om.reference.CustomizableEntity;
import br.com.auster.om.reference.facade.impl.HibernateFacadeBase;

/**
 * This facade interacts with the database to load all information related to 
 * 	rules.
 * 
 * TODO load in configure-time and store all rule information in cache to make
 * 	queries faster. We do not expect to have a extremally large number of rules
 *  anyway. 
 * 
 * @author framos
 * @version $Id$
 */
public class HibernateConsequenceDimensionsFacadeImpl extends HibernateFacadeBase  
                                     implements ConsequenceDimensionsFacade {
	
	
	
	// ---------------------------
	// Class constants
	// ---------------------------
	
	private static final Logger log = Logger.getLogger(HibernateConsequenceDimensionsFacadeImpl.class);
	private static final I18n i18n = I18n.getInstance(HibernateConsequenceDimensionsFacadeImpl.class);
	
	public static final int RULE_CACHE_SIZE = 2;
	public static final int CACHE_DEFAULT_SIZE = 100;
//	protected Map<Class, Map> cache;
	
	
	
	// ---------------------------
	// Instance variables
	// ---------------------------

	protected Session session;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public HibernateConsequenceDimensionsFacadeImpl() {
//		this.cache = new HashMap<Class, Map>();
//		this.cache.put(Rule.class, new LRUMap(CACHE_DEFAULT_SIZE));
	}
	
	public HibernateConsequenceDimensionsFacadeImpl(Session _session) {
		this.session = _session;
	}
	
	
	
	// ---------------------------
	// Public methods
	// ---------------------------
	
	/**
	 * Will implement the funcionality described in the super class using 
	 * 	a Hibernate session factory.
	 *  
	 * @see br.com.auster.billcheckout.consequence.ConsequenceDimensionsFacade#getRule(long)
	 */
	public Rule getRule(long _uid) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingRuleByUID", String.valueOf(_uid)));
			Criteria query = session.createCriteria(Rule.class);
			setExpressionField(query, "uid", new Long(_uid));
			return (Rule) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
		}
	}

	/**
	 * Will implement the funcionality described in the super class using 
	 * 	a Hibernate session factory.
	 * 
	 * @see br.com.auster.billcheckout.consequence.ConsequenceDimensionsFacade#getRule(java.lang.String)
	 */
	public Rule getRule(String _ruleCode) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingRuleByRuleCode", _ruleCode));
			Criteria query = session.createCriteria(Rule.class);
			setExpressionField(query, "code", _ruleCode);
			return (Rule) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
		}
	}

	/**
	 * Will implement the funcionality described in the super class using 
	 * 	a Hibernate session factory.
	 * 
	 * @see br.com.auster.billcheckout.consequence.ConsequenceDimensionsFacade#getRules()
	 */
	public Collection<Rule> getRules() {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			Criteria query = session.createCriteria(Rule.class);
			return query.list();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
		}
	}

	

	// ---------------------------
	// Protected methods
	// ---------------------------

	protected Object saveDimension(Object _object) throws HibernateException, SQLException {
//		Session session = null;
//		try {
			if (session == null) { session = this.factory.openSession(); }
			session.save(_object);
			session.flush();
			session.connection().commit();
//		} finally {
//			if (session != null) { session.close(); }
//		}
		return _object;
		
	}
	
	protected void setExpressionField(Criteria _query, String _field, Object _value) {
		if (_value == null) {
			_query.add(Expression.isNull(_field));
		} else {
			_query.add(Expression.eq(_field, _value));
		}
	}	
	
//	protected final CustomizableEntity getFromCache(String _subCache, String _key) {
//		if (_subCache == null) { return null; }
//		Map classCache = (Map) this.cache.get(Rule.class);
//		if (classCache == null) {
//			classCache = new HashMap(RULE_CACHE_SIZE);
//			Map uidSubCache = new LRUMap(CACHE_DEFAULT_SIZE);
//			this.cache.put(Rule.class, classCache);
//		}
//		Map subCache = (Map) classCache.get(_subCache);
//		if (subCache == null) {
//			subCache = new LRUMap(CACHE_DEFAULT_SIZE);
//			classCache.put(_subCache, subCache);
//		}
//		return (CustomizableEntity) subCache.get(_key);
//	}
//	
//	protected final void putIntoRuleCache(Object _entity) {
//		if (_entity != null) { 
//			Map classCache = (Map) this.cache.get(Rule.class);
//			if (classCache == null) {
//				classCache = new HashMap(RULE_CACHE_SIZE);
//				this.cache.put(Rule.class, classCache);
//			}
//			Map subCache 
//			classCache.put(_key, _entity);
//		}
//	}	
}
