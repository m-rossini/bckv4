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
package br.com.auster.billcheckout.consequence.telco.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import br.com.auster.billcheckout.consequence.DimensionQueryException;
import br.com.auster.billcheckout.consequence.hibernate.HibernateConsequenceDimensionsFacadeImpl;
import br.com.auster.billcheckout.consequence.telco.AccountDimension;
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.billcheckout.consequence.telco.CycleDimension;
import br.com.auster.billcheckout.consequence.telco.GeographicDimension;
import br.com.auster.billcheckout.consequence.telco.TelcoDimensionsFacade;
import br.com.auster.billcheckout.consequence.telco.TimeDimension;
import br.com.auster.common.util.I18n;

/**
 * TODO create cache configuration for this facade
 * 
 * @author framos
 * @version $Id$
 */
public class HibernateTelcoDimensionsFacadeImpl 
                                     extends HibernateConsequenceDimensionsFacadeImpl  
                                     implements TelcoDimensionsFacade {


	
	// ---------------------------
	// Class constants
	// ---------------------------
	
	private static final Logger log = Logger.getLogger(HibernateTelcoDimensionsFacadeImpl.class);
	private static final I18n i18n = I18n.getInstance(HibernateTelcoDimensionsFacadeImpl.class);
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------	

	public HibernateTelcoDimensionsFacadeImpl() {
		this(null);
	}

	public HibernateTelcoDimensionsFacadeImpl(Session _session) {
		super(_session);
	}
	
	
	public AccountDimension getAccount(long _uid) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingAccountByUID", String.valueOf(_uid)));
			Criteria query = session.createCriteria(AccountDimension.class);
			setExpressionField(query, "uid", new Long(_uid));
			return (AccountDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public AccountDimension getAccount(String _type, String _number) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingAccountByNumber", _number));
			Criteria query = session.createCriteria(AccountDimension.class);
			setExpressionField(query, "accountType", _type);
			setExpressionField(query, "accountNumber", _number);
			return (AccountDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public Collection<AccountDimension> getAccounts() {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			Criteria query = session.createCriteria(AccountDimension.class);
			return query.list();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}
	
	public AccountDimension saveAccount(AccountDimension _dimension) {
		if (_dimension == null) { return null; }
		// first searches if this object already exists
		AccountDimension dm = getAccount(_dimension.getAccountType(), _dimension.getAccountNumber());
		if (dm != null) { return dm; }
		try {
			return (AccountDimension) saveDimension(_dimension);
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
		} catch (SQLException sqle) {
			throw new DimensionQueryException(sqle);
		}
	}

	public CarrierDimension getCarrier(long _uid) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingCarrierByUID", String.valueOf(_uid)));
			Criteria query = session.createCriteria(CarrierDimension.class);
			setExpressionField(query, "uid", new Long(_uid));
			return (CarrierDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public CarrierDimension getCarrier(String _code, String _state) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingCarrierByCode", _code, _state));
			Criteria query = session.createCriteria(CarrierDimension.class);
			setExpressionField(query, "carrierCode", _code);
			setExpressionField(query, "carrierState", _state);
			return (CarrierDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public CarrierDimension saveCarrier(CarrierDimension _dimension) {
		if (_dimension == null) { return null; }
		// first searches if this object already exists
		CarrierDimension dm = getCarrier(_dimension.getCarrierCode(), _dimension.getCarrierState());
		if (dm != null) { return dm; }
		try {
			return (CarrierDimension) saveDimension(_dimension);
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
		} catch (SQLException sqle) {
			throw new DimensionQueryException(sqle);
		}
	}
	
	public Collection<CarrierDimension> getCarrier() {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			Criteria query = session.createCriteria(CarrierDimension.class);
			return query.list();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public CycleDimension getCycle(long _uid) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingCycleByUID", String.valueOf(_uid)));
			Criteria query = session.createCriteria(CycleDimension.class);
			setExpressionField(query, "uid", new Long(_uid));
			return (CycleDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public CycleDimension getCycle(String _code, Date _cutDate, Date _dueDate) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingCycleByCode", _code, _cutDate, _dueDate));
			Criteria query = session.createCriteria(CycleDimension.class);
			setExpressionField(query, "cycleCode", _code);
			setExpressionField(query, "cutDate", _cutDate);
			setExpressionField(query, "dueDate", _dueDate);
			return (CycleDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public CycleDimension saveCycle(CycleDimension _dimension) {
		if (_dimension == null) { return null; }
		// first searches if this object already exists
		CycleDimension dm = getCycle(_dimension.getCycleCode(), _dimension.getCutDate(), _dimension.getDueDate());
		if (dm != null) { return dm; }
		try {
			return (CycleDimension) saveDimension(_dimension);
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
		} catch (SQLException sqle) {
			throw new DimensionQueryException(sqle);
		}
	}
		
	public Collection<CycleDimension> getCycles() {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			Criteria query = session.createCriteria(CycleDimension.class);
			return query.list();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public GeographicDimension getGeo(long _uid) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingGeoByUID", String.valueOf(_uid)));
			Criteria query = session.createCriteria(GeographicDimension.class);
			setExpressionField(query, "uid", new Long(_uid));
			return (GeographicDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public GeographicDimension getGeo(String _country, String _state, String _city) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingGeoByCode", _country, _state, _city));
			Criteria query = session.createCriteria(GeographicDimension.class);
			setExpressionField(query, "country", _country);
			setExpressionField(query, "state", _state);
			setExpressionField(query, "city", _city);
			return (GeographicDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public GeographicDimension saveGeo(GeographicDimension _dimension) {
		if (_dimension == null) { return null; }
		// first searches if this object already exists
		GeographicDimension dm = getGeo(_dimension.getCountry(), _dimension.getState(), _dimension.getCity());
		if (dm != null) { return dm; }
		try {
			return (GeographicDimension) saveDimension(_dimension);
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
		} catch (SQLException sqle) {
			throw new DimensionQueryException(sqle);
		}
	}

	public Collection<GeographicDimension> getGeographics() {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			Criteria query = session.createCriteria(GeographicDimension.class);
			return query.list();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public TimeDimension getTime(long _uid) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingTimeByUID", String.valueOf(_uid)));
			Criteria query = session.createCriteria(TimeDimension.class);
			setExpressionField(query, "uid", new Long(_uid));
			return (TimeDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public TimeDimension getTime(String _year, String _month, String _day) {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			log.debug(i18n.getString("consequenceFilter.gettingTimeByTime", _year, _month, _day));
			Criteria query = session.createCriteria(TimeDimension.class);
			setExpressionField(query, "year", _year);
			setExpressionField(query, "month", _month);
			setExpressionField(query, "day", _day);
			return (TimeDimension) query.uniqueResult();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}

	public TimeDimension saveTime(TimeDimension _dimension) {
		if (_dimension == null) { return null; }
		// first searches if this object already exists
		TimeDimension dm = getTime(_dimension.getYear(), _dimension.getMonth(), _dimension.getDay());
		if (dm != null) { return dm; }
		try {
			return (TimeDimension) saveDimension(_dimension);
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
		} catch (SQLException sqle) {
			throw new DimensionQueryException(sqle);
		}
	}
	
	public Collection<TimeDimension> getTimes() {
//		Session session = null;
		try {
			if (session == null) { session = this.factory.openSession(); }
			Criteria query = session.createCriteria(TimeDimension.class);
			return query.list();
		} catch (HibernateException he) {
			throw new DimensionQueryException(he);
//		} finally {
//			if (session != null) { session.close(); }
		}
	}
	
}
