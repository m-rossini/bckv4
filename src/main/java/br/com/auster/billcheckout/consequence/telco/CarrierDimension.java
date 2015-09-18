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

import br.com.auster.om.reference.CustomizableEntity;

/**
 * @author framos
 * @version $Id$
 */
public class CarrierDimension extends CustomizableEntity {

	
	
	// ---------------------------
	// Class constants
	// ---------------------------
	
	public static final int MAX_CARRIERNAME_SIZE = 48;

	
	
	// ---------------------------
	// Instance variables
	// ---------------------------

	private String carrier;
	private String code;
	private String state;
	
	private Key key;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public CarrierDimension() {
		this(0);
	}

    /**
     * This version will create a new instance that, due to the Hibernate mapping, will
     *   be treated as <strong>already registered</strong> in the database.
     * <p>
     * This means that is <code>saveOrUpdate()</code> is called, then an update (and not 
     *   an insert) will be executed. So, use this constructor wisely. 
     */
	public CarrierDimension(long _uid) {
		super(_uid);
	}

	public CarrierDimension(String _carrier, String _code, String _state) {
		this(0);
		this.setCarrierCompany(_carrier);
		this.setCarrierCode(_code);
		this.setCarrierState(_state);
	}

	
	
	// ---------------------------
	// Public methods
	// ---------------------------
	
	
	public String getCustomDescription(){
		return this.getCarrierCode() + " - " + this.getCarrierState(); 
	}
		
	
	
	
	public void setCarrierCompany(String _carrier) {
		if (_carrier != null) {
			this.carrier = _carrier.substring(0, Math.min(_carrier.length(), MAX_CARRIERNAME_SIZE));
		} else {
			this.carrier = _carrier;
		}
	}
	public String getCarrierCompany() {
		return this.carrier;
	}

	public void setCarrierCode(String _code) {
		this.code = _code;
	}
	public String getCarrierCode() {
		return this.code;
	}
	
	public void setCarrierState(String _state) {
		this.state = _state;
	}
	public String getCarrierState() {
		return this.state;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getCarrierCode() == null? 0 : 
			                        this.getCarrierCode().hashCode());
		result = result*37 + (this.getCarrierState() == null? 0 : 
                                    this.getCarrierState().hashCode());
		return result;
	}	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() +  
		" : OpCode=[" + this.getCarrierCode() + 
		"].OpState=[" + this.getCarrierState() +
		"].OpName=[" + this.getCarrierCompany() +"]";
	}

	public Key getKey() {
		if (this.key == null) {
			this.key = new Key(this.getCarrierCode(), this.getCarrierState());
		}
		return this.key;
	}
	
	public static class Key {
		
		private String carrierCode;
		private String carrierState;
		
		public Key(String _carrierCode, String _carrierState) {
			this.carrierCode = _carrierCode;
			this.carrierState = (_carrierState == null ? "" : _carrierState);
		}
		
		public boolean equals(Object obj) {
			Key k = (Key) obj;
			return ( k.carrierCode.equals(this.carrierCode) && 
					 k.carrierState.equals(this.carrierState) );
		}
		
		public int hashCode() {
			int code = this.carrierCode.hashCode();
			code += this.carrierState.hashCode();
			return code;
		}
	}
	
	
		
	// ---------------------------
	// static methods
	// ---------------------------

	public static CustomizableEntity mergeNonKeyAttributes(CarrierDimension _carrier1, CarrierDimension _carrier2) {
		
		CarrierDimension finalObj = (CarrierDimension) CustomizableEntity.mergeNonKeyAttributes(_carrier1, _carrier2);
		CarrierDimension otherObj = (finalObj == _carrier1 ? _carrier2 : _carrier1);
		if ((finalObj == null) || (otherObj == null)) {
			return finalObj;
		}
		finalObj.setCarrierCompany((finalObj.getCarrierCompany() == null ? otherObj.getCarrierCompany() : 
																		   finalObj.getCarrierCompany()));
		return finalObj;
	}	
}
