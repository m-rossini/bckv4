/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 21/10/2006
 */
package br.com.auster.billcheckout.filter;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import br.com.auster.billcheckout.consequence.Consequence;
import br.com.auster.billcheckout.consequence.hibernate.HibernateConsequenceDimensionsFacadeImpl;
import br.com.auster.billcheckout.ruleobjects.Rule;
import br.com.auster.common.util.I18n;
import br.com.auster.om.filter.HibernatePersistenceFilter;

/**
 * @author framos
 * @version $Id$
 */
public class BillcheckoutPersistenceFilter extends HibernatePersistenceFilter {

	
	
	// ---------------------------
	// Class variables
	// ---------------------------
	
	private static final Logger log = Logger.getLogger(BillcheckoutPersistenceFilter.class);
	private static final I18n i18n = I18n.getInstance(BillcheckoutPersistenceFilter.class);
	
	private static final Lock ruleLock = new ReentrantLock();

	protected Map<Class, Map> cache;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public BillcheckoutPersistenceFilter(String _name) {
		super(_name);
		this.cache = new LinkedHashMap<Class, Map>();
		this.cache.put(Rule.class, new LRUMap(300));
	}

	
	
	// ---------------------------
	// Protected methods
	// ---------------------------
	
	protected void saveObject(Session _session, Object _object) throws HibernateException, SQLException {
		// logging object classname and content
		int counter=0;
		if (_object instanceof Collection) {
			for (Iterator it = ((List)_object).iterator(); it.hasNext(); ) {
				Object persistedObject = it.next();
				if (persistedObject instanceof Consequence) {
					log.debug(i18n.getString("checking.dimensions"));
					checkDimensions(_session, (Consequence)persistedObject);
				}
				runSave(_session, persistedObject, false, false);
//				_session.save(persistedObject);
				counter++;
				// commit interval check
				if (counter == this.commitCount) {
					runCommit(_session);
//					_session.flush();
//					_session.connection().commit();
					counter=0;
				}
			}
		} else {
			if (_object instanceof Consequence) {
				log.debug(i18n.getString("checking.dimensions"));
				checkDimensions(_session, (Consequence)_object);
			}
			runSave(_session, _object, false, false);
//			_session.save(_object);
		}
	}
	
	protected void checkDimensions(Session _session, Consequence _consequence) throws HibernateException, SQLException {
		HibernateConsequenceDimensionsFacadeImpl facade = new HibernateConsequenceDimensionsFacadeImpl(_session);
		// checking rule
		Rule rule = _consequence.getRelatedRule();
		if (rule != null) {
			if (! this.cache.get(rule.getClass()).containsKey(rule.getKey())) {
				Rule loadedRule = facade.getRule(rule.getCode());
				if (loadedRule == null) {
					try {
						ruleLock.lockInterruptibly();
						log.debug(i18n.getString("lock.acquired", Thread.currentThread().getName()));
						loadedRule = facade.getRule(rule.getCode());
						if (loadedRule != null) {
							log.debug(i18n.getString("found.rule", loadedRule.getCode()));
						} else {
							runSave(_session, rule, false, true);
//							_session.save(rule);
//							runCommit(_session);
//							_session.flush();
//							_session.connection().commit();
							loadedRule = rule;
						}
					} catch (InterruptedException ie) {
						log.warn("Lock interrupted", ie);
						throw new HibernateException(ie);
					} finally {
						log.debug(i18n.getString("lock.released", Thread.currentThread().getName()));
						ruleLock.unlock();
					}
				}
				_consequence.setRelatedRule(loadedRule);
				this.cache.get(rule.getClass()).put(_consequence.getRelatedRule().getKey(), _consequence.getRelatedRule());
			} else {
				_consequence.setRelatedRule((Rule)this.cache.get(rule.getClass()).get(rule.getKey()));
			}
		}
	}
}
