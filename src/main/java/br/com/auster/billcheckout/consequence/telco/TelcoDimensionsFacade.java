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

import java.util.Collection;
import java.util.Date;

import br.com.auster.billcheckout.consequence.ConsequenceDimensionsFacade;

/**
 * @author framos
 * @version $Id$
 *
 */
public interface TelcoDimensionsFacade extends ConsequenceDimensionsFacade {

	public AccountDimension getAccount(long _uid);
	public AccountDimension getAccount(String _type, String _number);
	public AccountDimension saveAccount(AccountDimension _dimension);
	public Collection<AccountDimension> getAccounts();
	
	public CycleDimension getCycle(long _uid);
	public CycleDimension getCycle(String _code, Date _cutDate, Date _dueDate);
	public CycleDimension saveCycle(CycleDimension _dimension);
	public Collection<CycleDimension> getCycles();
	
	public CarrierDimension getCarrier(long _uid);
	public CarrierDimension getCarrier(String _code, String _state);
	public CarrierDimension saveCarrier(CarrierDimension _dimension);
	public Collection<CarrierDimension> getCarrier();

	public TimeDimension getTime(long _uid);
	public TimeDimension getTime(String _year, String _month, String _day);
	public TimeDimension saveTime(TimeDimension _dimension);
	public Collection<TimeDimension> getTimes();

	public GeographicDimension getGeo(long _uid);
	public GeographicDimension getGeo(String _country, String _state, String _city);
	public GeographicDimension saveGeo(GeographicDimension _dimension);
	public Collection<GeographicDimension> getGeographics();
}
