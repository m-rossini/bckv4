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
package br.com.auster.billcheckout.filter.telco;

import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import br.com.auster.billcheckout.consequence.Consequence;
import br.com.auster.billcheckout.consequence.telco.AccountDimension;
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.billcheckout.consequence.telco.CycleDimension;
import br.com.auster.billcheckout.consequence.telco.GeographicDimension;
import br.com.auster.billcheckout.consequence.telco.TelcoConsequence;
import br.com.auster.billcheckout.consequence.telco.TimeDimension;
import br.com.auster.billcheckout.consequence.telco.hibernate.HibernateTelcoDimensionsFacadeImpl;
import br.com.auster.billcheckout.filter.BillcheckoutPersistenceFilter;
import br.com.auster.common.util.I18n;

/**
 * @author framos
 * @version $Id$
 */
public class TelcoBillcheckoutPersistenceFilter extends BillcheckoutPersistenceFilter {

	
	
	// ---------------------------
	// Class variables
	// ---------------------------
	
	private static final Logger log = Logger.getLogger(TelcoBillcheckoutPersistenceFilter.class);
	private static final I18n i18n = I18n.getInstance(TelcoBillcheckoutPersistenceFilter.class);

	private static final Lock accountLock = new ReentrantLock();
	private static final Lock carrierLock = new ReentrantLock();
	private static final Lock cycleLock = new ReentrantLock();
	private static final Lock geoLock = new ReentrantLock();
	private static final Lock timeLock = new ReentrantLock();
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public TelcoBillcheckoutPersistenceFilter(String _name) {
		super(_name);
		this.cache.put(AccountDimension.class, new LRUMap(300));
		this.cache.put(CycleDimension.class, new LRUMap(300));
		this.cache.put(GeographicDimension.class, new LRUMap(300));
		this.cache.put(TimeDimension.class, new LRUMap(300));
		this.cache.put(CarrierDimension.class, new LRUMap(300));
	}
	

	
	// ---------------------------
	// Protected methods
	// ---------------------------
	
	protected void checkDimensions(Session _session, Consequence _consequence) throws HibernateException, SQLException {
		// checking rule on parent class
		super.checkDimensions(_session, _consequence);
		// check if this is a TELCO consequence
		if (!(_consequence instanceof TelcoConsequence)) {
			log.warn(i18n.getString("consequence.notCompatible"));
			return;
		}
		HibernateTelcoDimensionsFacadeImpl facade = new HibernateTelcoDimensionsFacadeImpl(_session);
		TelcoConsequence consequence = (TelcoConsequence)_consequence;
		checkAccount(_session, facade, consequence);
		checkCycle(_session, facade, consequence);
		checkCarrier(_session, facade, consequence);
		checkTime(_session, facade, consequence);
		checkGeo(_session, facade, consequence);
	}
	
	protected final void checkAccount(Session _session, HibernateTelcoDimensionsFacadeImpl _facade, TelcoConsequence _consequence) throws HibernateException, SQLException {
		// checking account
		AccountDimension account = _consequence.getAccount();
		if (account != null) {
			log.debug(i18n.getString("lookingfor.account", account.getAccountNumber()));
			if (! this.cache.get(account.getClass()).containsKey(account.getKey())) {
				log.debug(i18n.getString("notincache.account", account.getAccountNumber()));
				AccountDimension loadedObj = _facade.getAccount(account.getAccountType(), account.getAccountNumber());
				if (loadedObj == null) {
					try {
						accountLock.lockInterruptibly();
						log.debug(i18n.getString("lock.acquired", Thread.currentThread().getName()));
						loadedObj = _facade.getAccount(account.getAccountType(), account.getAccountNumber());
						if (loadedObj != null) {
							log.debug(i18n.getString("found.account", loadedObj.getAccountNumber()));
						} else {
							runSave(_session, account, false, true);
							//_session.save(account);
//							_session.flush();
//							_session.connection().commit();
							loadedObj = account;
						}
					} catch (InterruptedException ie) {
						log.warn("Lock interrupted", ie);
						throw new HibernateException(ie);
					} finally {
						log.debug(i18n.getString("lock.released", Thread.currentThread().getName()));
						accountLock.unlock();
					}
				}
				_consequence.setAccount(loadedObj);
				this.cache.get(account.getClass()).put(_consequence.getAccount().getKey(), _consequence.getAccount());
			} else {
				_consequence.setAccount((AccountDimension)this.cache.get(account.getClass()).get(account.getKey()));
			}
		}
	}

	protected final void checkCarrier(Session _session, HibernateTelcoDimensionsFacadeImpl _facade, TelcoConsequence _consequence) throws HibernateException, SQLException {
		// checking carrier
		CarrierDimension carrier = _consequence.getCarrier();
		if (carrier != null) {
			log.debug(i18n.getString("lookingfor.carrier", carrier.getCarrierCode(), carrier.getCarrierState()));
			if (! this.cache.get(carrier.getClass()).containsKey(carrier.getKey())) {
				log.debug(i18n.getString("notincache.carrier", carrier.getCarrierCode(), carrier.getCarrierState()));
				CarrierDimension loadedObj = _facade.getCarrier(carrier.getCarrierCode(), carrier.getCarrierState());
				if (loadedObj == null) {
					try {
						carrierLock.lockInterruptibly();
						log.debug(i18n.getString("lock.acquired", Thread.currentThread().getName()));
						loadedObj = _facade.getCarrier(carrier.getCarrierCode(), carrier.getCarrierState());
						if (loadedObj != null) {
							log.debug(i18n.getString("found.carrier", loadedObj.getCarrierCode(), loadedObj.getCarrierState()));
						} else {
							runSave(_session, carrier, false, true);
//							_session.save(carrier);
//							_session.flush();
//							_session.connection().commit();
							loadedObj = carrier;
						}
					} catch (InterruptedException ie) {
						log.warn("Lock interrupted", ie);
						throw new HibernateException(ie);
					} finally {
						log.debug(i18n.getString("lock.released", Thread.currentThread().getName()));
						carrierLock.unlock();
					}
				}
				_consequence.setCarrier(loadedObj);
				this.cache.get(carrier.getClass()).put(_consequence.getCarrier().getKey(), _consequence.getCarrier());
			} else {
				_consequence.setCarrier((CarrierDimension)this.cache.get(carrier.getClass()).get(carrier.getKey()));
			}
		}
	}
	
	protected final void checkCycle(Session _session, HibernateTelcoDimensionsFacadeImpl _facade, TelcoConsequence _consequence) throws HibernateException, SQLException {
		// checking cycle
		CycleDimension cycle = _consequence.getCycle();
		if (cycle != null) {
			log.debug(i18n.getString("lookingfor.cycle", cycle.getCycleCode(), cycle.getCutDate(), cycle.getDueDate()));
			if (! this.cache.get(cycle.getClass()).containsKey(cycle.getKey())) {
				log.debug(i18n.getString("notincache.cycle", cycle.getCycleCode(), cycle.getCutDate(), cycle.getDueDate()));
				CycleDimension loadedObj = _facade.getCycle(cycle.getCycleCode(), cycle.getCutDate(), cycle.getDueDate());
				if (loadedObj == null) {
					try {
						cycleLock.lockInterruptibly();
						log.debug(i18n.getString("lock.acquired", Thread.currentThread().getName()));
						loadedObj = _facade.getCycle(cycle.getCycleCode(), cycle.getCutDate(), cycle.getDueDate());
						if (loadedObj != null) {
							log.debug(i18n.getString("found.cycle", loadedObj.getCycleCode(), loadedObj.getCutDate(), loadedObj.getDueDate()));
						} else {
							runSave(_session, cycle, false, true);
//							_session.save(cycle);
//							_session.flush();
//							_session.connection().commit();
							loadedObj = cycle;
						}
					} catch (InterruptedException ie) {
						log.warn("Lock interrupted", ie);
						throw new HibernateException(ie);
					} finally {
						log.debug(i18n.getString("lock.released", Thread.currentThread().getName()));
						cycleLock.unlock();
					}
				}
				_consequence.setCycle(loadedObj);
				this.cache.get(cycle.getClass()).put(_consequence.getCycle().getKey(), _consequence.getCycle());
			} else {
				_consequence.setCycle((CycleDimension)this.cache.get(cycle.getClass()).get(cycle.getKey()));
			}
		}
	}
	
	protected final void checkGeo(Session _session, HibernateTelcoDimensionsFacadeImpl _facade, TelcoConsequence _consequence) throws HibernateException, SQLException {
	
		// checking geographics
		GeographicDimension geo = _consequence.getGeographics();
		if (geo != null) {
			log.debug(i18n.getString("lookingfor.geo", geo.getCountry(), geo.getState(), geo.getCity()));
			if (! this.cache.get(geo.getClass()).containsKey(geo.getKey())) {
				log.debug(i18n.getString("notincache.geo", geo.getCountry(), geo.getState(), geo.getCity()));
				GeographicDimension loadedObj = _facade.getGeo(geo.getCountry(), geo.getState(), geo.getCity());
				if (loadedObj == null) {
					try {
						geoLock.lockInterruptibly();
						log.debug(i18n.getString("lock.acquired", Thread.currentThread().getName()));
						loadedObj = _facade.getGeo(geo.getCountry(), geo.getState(), geo.getCity());
						if (loadedObj != null) {
							log.debug(i18n.getString("found.geo", loadedObj.getCountry(), loadedObj.getState(), loadedObj.getCity()));
						} else {
							runSave(_session, geo, false, true);
//							_session.save(geo);
//							_session.flush();
//							_session.connection().commit();
							loadedObj = geo;
						}
					} catch (InterruptedException ie) {
						log.warn("Lock interrupted", ie);
						throw new HibernateException(ie);
					} finally {
						log.debug(i18n.getString("lock.released", Thread.currentThread().getName()));
						geoLock.unlock();
					}
				}
				_consequence.setGeographics(loadedObj);
				this.cache.get(geo.getClass()).put(_consequence.getGeographics().getKey(), _consequence.getGeographics());
			} else {
				_consequence.setGeographics((GeographicDimension)this.cache.get(geo.getClass()).get(geo.getKey()));
			}
		}
	}
	
	protected final void checkTime(Session _session, HibernateTelcoDimensionsFacadeImpl _facade, TelcoConsequence _consequence) throws HibernateException, SQLException {
		// checking geographics
		TimeDimension time = _consequence.getTime();
		if (time != null) {
			log.debug(i18n.getString("lookingfor.time", time.getYear(), time.getMonth(), time.getDay()));
			if (! this.cache.get(time.getClass()).containsKey(time.getKey())) {
				log.debug(i18n.getString("notincache.time", time.getYear(), time.getMonth(), time.getDay()));
				TimeDimension loadedObj = _facade.getTime(time.getYear(), time.getMonth(), time.getDay());
				if (loadedObj == null) {
					try {
						timeLock.lockInterruptibly();
						log.debug(i18n.getString("lock.acquired", Thread.currentThread().getName()));
						loadedObj = _facade.getTime(time.getYear(), time.getMonth(), time.getDay());
						if (loadedObj != null) {
							log.debug(i18n.getString("found.time", loadedObj.getYear(), loadedObj.getMonth(), loadedObj.getDay()));
						} else {
							runSave(_session, time, false, true);
//							_session.save(time);
//							_session.flush();
//							_session.connection().commit();
							loadedObj = time;
						}
					} catch (InterruptedException ie) {
						log.warn("Lock interrupted", ie);
						throw new HibernateException(ie);
					} finally {
						log.debug(i18n.getString("lock.released", Thread.currentThread().getName()));
						timeLock.unlock();
					}
				}
				_consequence.setTime(loadedObj);
				this.cache.get(time.getClass()).put(_consequence.getTime().getKey(), _consequence.getTime());
			} else {
				_consequence.setTime((TimeDimension)this.cache.get(time.getClass()).get(time.getKey()));
			}
		}
	}
	
}
