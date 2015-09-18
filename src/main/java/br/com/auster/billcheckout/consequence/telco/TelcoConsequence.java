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
 * Created on 16/08/2006
 */
package br.com.auster.billcheckout.consequence.telco;

import java.util.Collection;
import java.util.LinkedList;

import br.com.auster.billcheckout.consequence.Consequence;
import br.com.auster.om.invoice.InvoiceModelObject;

/**
 * @hibernate.class table="BCK_CONSEQUENCE"
 * 
 * @author framos
 * @version $Id$
 */
public class TelcoConsequence extends Consequence {

	
	
	// ---------------------------
	// Instance variables
	// ---------------------------
	
	private AccountDimension account;
	private CarrierDimension carrier;
	private CycleDimension cycle;
	private GeographicDimension geo;
	private TimeDimension time;
	private Collection<InvoiceModelObject> relatedObjects;

	
	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public TelcoConsequence() {
		super();
		this.relatedObjects = new LinkedList<InvoiceModelObject>();
	}
	
	public TelcoConsequence(long _uid) {
		super(_uid);
		this.relatedObjects = new LinkedList<InvoiceModelObject>();
	}


	
	// ---------------------------
	// Public methods
	// ---------------------------

	public void setAccount(AccountDimension _account) {
		this.account = _account;
	}
	public AccountDimension getAccount() {
		return this.account;
	}
	
	public void setCarrier(CarrierDimension _carrier) {
		this.carrier = _carrier;
	}
	public CarrierDimension getCarrier() {
		return this.carrier;
	}

	public void setCycle(CycleDimension _cycle) {
		this.cycle = _cycle;
	}
	public CycleDimension getCycle() {
		return this.cycle;
	}
	
	public void setGeographics(GeographicDimension _geo) {
		this.geo = _geo;
	}
	public GeographicDimension getGeographics() {
		return this.geo;
	}
	
	public void setTime(TimeDimension _time) {
		this.time = _time;
	}
	public TimeDimension getTime() {
		return this.time;
	}
	
	
	public boolean addRelatedObject(InvoiceModelObject _omObject) {
		return this.relatedObjects.add(_omObject);
	}

	public boolean removeRelatedObject(InvoiceModelObject _omObject) {
		return this.relatedObjects.remove(_omObject);
	}
	
	public Collection<InvoiceModelObject> getRelatedObjects() {
		return this.relatedObjects;
	}
	
	public void setRelatedObjects(Collection<InvoiceModelObject> _omObjectList) {
		this.relatedObjects = _omObjectList;
	}
	
	public String toString() {
		return super.toString() +
		".Account=[" + (this.getAccount() == null ? "-" : this.getAccount().getUid()) + "]" + 
		".Cycle=[" + (this.getCycle() == null ? "-" : this.getCycle().getUid()) + "]" + 
		".Carrier=[" + (this.getCarrier() == null ? "-" : this.getCarrier().getUid()) + "]" + 
		".Geo=[" + (this.getGeographics() == null ? "-" : this.getGeographics().getUid()) + "]" + 
		".Time=[" + (this.getTime() == null ? "-" : this.getTime().getUid()) + "]"
		+ System.getProperty("line.separator"); 
	}
}
