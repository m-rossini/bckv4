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
package br.com.auster.billcheckout.consequence.telco;

import java.util.Date;

import br.com.auster.billcheckout.consequence.ConsequenceBuilder;

/**
 * @author framos
 * @version $Id$
 * 
 */
public class TelcoConsequenceBuilder extends ConsequenceBuilder {

	
	
	// ---------------------------
	// Constructors
	// ---------------------------

	public TelcoConsequenceBuilder() {
		super();
	}

	public TelcoConsequenceBuilder(TelcoDimensionsFacade _facade) {
		super(_facade);
	}


	
	// ---------------------------
	// Public methods
	// ---------------------------
	
	public void reset() {
		this.newConsequence = new TelcoConsequence();
	}

	/**
	 * Sets the account information for this <code>TelcoConsequence</code>
	 */
	public void setAccount(AccountDimension _account) {
		if (this.newConsequence == null) { this.reset(); }
		// if current account dimension UID <= 0 and we have a facade instance, than try to load it.
		AccountDimension tmp = null;
		if ((this.dimFacade != null) && (_account.getUid() <= 0)) {
			tmp = ((TelcoDimensionsFacade)dimFacade).getAccount(_account.getAccountType(),
							                                    _account.getAccountNumber());
		}
		tmp = (AccountDimension) AccountDimension.mergeNonKeyAttributes(tmp, _account);
		((TelcoConsequence) this.newConsequence).setAccount(tmp);
	}

	public void setAccount(String _type, String _holding, String _number) {
		this.setAccount(new AccountDimension(_type, _holding, _number));
	}

	/**
	 * Sets the cycle information for this <code>TelcoConsequence</code>
	 */
	public void setCycle(CycleDimension _cycle) {
		if (this.newConsequence == null) { this.reset(); }
		// if current cycle dimension UID <= 0 and we have a facade instance, than try to load it.
		CycleDimension tmp = null;
		if ((this.dimFacade != null) && (_cycle.getUid() <= 0)) {
			tmp = ((TelcoDimensionsFacade)dimFacade).getCycle(_cycle.getCycleCode(),
							                                  _cycle.getCutDate(),
//							                                  _cycle.getIssueDate(),
							                                  _cycle.getDueDate());
		}
		tmp = (CycleDimension) CycleDimension.mergeNonKeyAttributes(tmp, _cycle);
		((TelcoConsequence) this.newConsequence).setCycle(tmp);
	}

	public void setCycle(String _code, Date _cut, Date _issue, Date _due) {
		this.setCycle(new CycleDimension(_code, _cut, _issue, _due));
	}

	/**
	 * Sets the time information for this <code>TelcoConsequence</code>
	 */
	public void setTime(TimeDimension _time) {
		if (this.newConsequence == null) { this.reset(); }
		// if current time dimension UID <= 0 and we have a facade instance, than try to load it.
		TimeDimension tmp = null;
		if ((this.dimFacade != null) && (_time.getUid() <= 0)) {
			tmp = ((TelcoDimensionsFacade)dimFacade).getTime(_time.getYear(),
															 _time.getMonth(),
							                                 _time.getDay());
		}
		tmp = (TimeDimension) TimeDimension.mergeNonKeyAttributes(tmp, _time);
		((TelcoConsequence) this.newConsequence).setTime(tmp);
	}

	public void setTime(String _year, String _month, String _day) {
		this.setTime(new TimeDimension(_year, _month, _day));
	}

	public void setTime(Date _date) {
		this.setTime(new TimeDimension(_date));
	}

	/**
	 * Sets the geographics information for this <code>TelcoConsequence</code>
	 */
	public void setGeographics(GeographicDimension _geo) {
		if (this.newConsequence == null) { this.reset(); }
		// if current geo dimension UID <= 0 and we have a facade instance, than try to load it.
		GeographicDimension tmp = null;
		if ((this.dimFacade != null) && (_geo.getUid() <= 0)) {
			tmp = ((TelcoDimensionsFacade)dimFacade).getGeo(_geo.getCountry(),
//													        _geo.getRegion(),
													        _geo.getState(),
					                                        _geo.getCity());
		}
		tmp = (GeographicDimension) GeographicDimension.mergeNonKeyAttributes(tmp, _geo);
		((TelcoConsequence) this.newConsequence).setGeographics(tmp);
	}

	public void setGeographics(String _country, String _region, String _state, String _city) {
		this.setGeographics(new GeographicDimension(_country, _region, _state, _city));
	}

	/**
	 * Sets the carrier information for this <code>TelcoConsequence</code>
	 */
	public void setCarrier(CarrierDimension _carrier) {
		if (this.newConsequence == null) { this.reset(); }
		// if current carrier dimension UID <= 0 and we have a facade instance, than try to load it.
		CarrierDimension tmp = null;
		if ((this.dimFacade != null) && (_carrier.getUid() <= 0)) {
			tmp = ((TelcoDimensionsFacade)dimFacade).getCarrier(//_carrier.getCarrierCompany(),
															    _carrier.getCarrierCode(),
															    _carrier.getCarrierState());
		}
		tmp = (CarrierDimension) CarrierDimension.mergeNonKeyAttributes(tmp, _carrier);
		((TelcoConsequence) this.newConsequence).setCarrier(tmp);
	}

	/***
	 * @deprecated This method causes a Database error. It does not populate properly (Sometimes, dependning on the caller)
	 * the attribute operator name, which is NOT NULLABLE on the databse.
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * </p>
	 * 
	 * @param _carrier
	 * @param _code
	 * @param _state
	 */
	public void setCarrier(String _carrier, String _code, String _state) {
		this.setCarrier(new CarrierDimension(_carrier, _code, _state));
	}

	/**
	 * Sets all dimension informations for this <code>TelcoConsequence</code>
	 */
	public void setDimensions(AccountDimension _account, CarrierDimension _carrier,
			TimeDimension _time, GeographicDimension _geo, CycleDimension _cycle) {

		this.setAccount(_account);
		this.setCarrier(_carrier);
		this.setCycle(_cycle);
		this.setGeographics(_geo);
		this.setTime(_time);
	}

	public boolean isConsequenceReady() {
		TelcoConsequence tc = (TelcoConsequence) this.newConsequence;
		if (this.isLenient()) {
			return super.isConsequenceReady();
		}
		return super.isConsequenceReady() && (tc.getAccount() != null)
				&& (tc.getCycle() != null) && (tc.getCarrier() != null)
				&& (tc.getGeographics() != null) && (tc.getTime() != null);
	}

}
