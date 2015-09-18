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
 * Created on 12/03/2007
 */
package br.com.auster.billcheckout.thresholds;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;

/**
 * @author framos
 * @version $Id$
 */
public class OCCThreshold extends BaseThreshold {

	
	
	public static final String ANY_CARRIER = "-1";
	
	// ---------------------------
	// Instance attributes
	// ---------------------------
		
	// the CARRIER this OCC is related to
	protected CarrierDimension carrier;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
		
	public OCCThreshold() {
		super();
		this.carrier = null;
	}
	
	
	
	// ---------------------------
	// Getters / Setters
	// ---------------------------	

	public final CarrierDimension getCarrier() {
		return this.carrier;
	}
	public final void setCarrier(CarrierDimension _carrier) {
		this.carrier = _carrier;
	}
	
	public final String getCarrierCode() {
		return (this.carrier == null ? null : this.carrier.getCarrierCode());
	}
	
	public final String getCarrierState() {
		return (this.carrier == null ? null : this.carrier.getCarrierState());
	}
	
	/**
	 * @see br.com.auster.om.reference.PKEnabledEntity#equals(Object)
	 */
	public boolean equals(Object _other) {
		if (_other != null) {
			if (_other.getClass().isAssignableFrom(this.getClass())) {
				return super.equals(_other);
			}
		}
		return false;
	}

	
	
	// ---------------------------
	// CacheableVO ALTERNATE KEY 
	//      attribute
	// ---------------------------		
	
	public CacheableKey getAlternateKey() {
		if (this.alternateKey == null) {
			if (this.getCarrier() == null) {
				this.alternateKey = createAlternateKey(null, null);
			} else {
				this.alternateKey = createAlternateKey(this.getCarrier().getCarrierCode(), this.getCarrier().getCarrierState());
			}
		}
		return this.alternateKey;
	}

	public static final class InnerAlternateKey implements CacheableKey {
		
		private String carrierCode;
		private String carrierState;
		
		public InnerAlternateKey(String _carrierCode, String _carrierState) {
			this.carrierCode = _carrierCode;
			this.carrierState = _carrierState;
		}
		
		public boolean equals(Object _other) {
			if ( _other instanceof InnerAlternateKey ) {
				InnerAlternateKey other = (InnerAlternateKey) _other;
				return (this.carrierCode.equals(other.carrierCode) && 
						this.carrierState.equals(other.carrierState));
			}
			else 
				return false;
		}

		public int hashCode() {
			int hashcode = 37;			
			hashcode += 17 * this.carrierCode.hashCode();			
			hashcode += 17 * this.carrierState.hashCode();			
			return hashcode;
		}

		public String getCarrierCode() { return this.carrierCode; }
		public String getCarrierState() { return this.carrierState; }
	}
		
	// ---------------------------
	// Helper methods
	// ---------------------------
	
	public static final CacheableKey createAlternateKey(String _carrierCode, String _carrierState) {
		if (_carrierCode == null) {
			return new InnerAlternateKey(ANY_CARRIER, ANY_CARRIER);
		}
		return new InnerAlternateKey(_carrierCode, _carrierState);
	}

}