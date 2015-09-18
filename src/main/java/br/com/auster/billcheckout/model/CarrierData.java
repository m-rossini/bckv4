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
package br.com.auster.billcheckout.model;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.om.reference.CustomizableEntity;

/**
 * 
 * @author framos
 * @version $Id$
 *
 */
public class CarrierData extends CustomizableEntity implements CacheableVO {

	
	
	public static final int MAX_FULLNAME_SIZE = 128;
	public static final int MAX_TAXID_SIZE = 24;
	public static final int MAX_STATEENROLL_SIZE = 32;
	public static final int MAX_CITYENROLL_SIZE = 32;
	public static final int MAX_ADDRSTREET_SIZE = 128;
	public static final int MAX_ADDRNUMBER_SIZE = 32;
	public static final int MAX_ADDRCOMPLEMENT_SIZE = 32;
	public static final int MAX_ADDRZIP_SIZE = 16;
	public static final int MAX_ADDRCITY_SIZE = 64;
	public static final int MAX_ADDRWEB_SIZE = 64;
	public static final int MAX_ADDREMAIL_SIZE = 64;

	private String fullName;
	private String taxId;
	private String stateEnrollNumber;
	private String cityEnrollNumber;
	private String addressStreet;
	private String addressNumber;
	private String addressComplement;
	private String addressZip;
	private String addressCity;
	private String addressWeb;
	private String addressEmail;
	private CarrierDimension carrierDimension;
	
	private CacheableKey key;
	private CacheableKey alternateKey;

	
	
	public CarrierData() {
		super(0);
	}
	
	public CarrierData(long uid) {
		super(uid);
	}

	public String getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(String addressCity) {

		if (addressCity != null) {
			this.addressCity = addressCity.substring(0, Math.min(addressCity.length(), MAX_ADDRCITY_SIZE));
		} else {
			this.addressCity = addressCity;
		}
	}

	public String getAddressComplement() {
		return addressComplement;
	}

	public void setAddressComplement(String addressComplement) {

		if (addressComplement != null) {
			this.addressComplement = addressComplement.substring(0, Math.min(addressComplement.length(), MAX_ADDRCOMPLEMENT_SIZE));
		} else {
			this.addressComplement = addressComplement;
		}
	}

	public String getAddressEmail() {
		return addressEmail;
	}

	public void setAddressEmail(String addressEmail) {

		if (addressEmail != null) {
			this.addressEmail = addressEmail.substring(0, Math.min(addressEmail.length(), MAX_ADDREMAIL_SIZE));
		} else {
			this.addressEmail = addressEmail;
		}
	}

	public String getAddressNumber() {
		return addressNumber;
	}

	public void setAddressNumber(String addressNumber) {

		if (addressNumber != null) {
			this.addressNumber = addressNumber.substring(0, Math.min(addressNumber.length(), MAX_ADDRNUMBER_SIZE));
		} else {
			this.addressNumber = addressNumber;
		}
	}

	public String getAddressStreet() {
		return addressStreet;
	}

	public void setAddressStreet(String addressStreet) {

		if (addressStreet != null) {
			this.addressStreet = addressStreet.substring(0, Math.min(addressStreet.length(), MAX_ADDRSTREET_SIZE));
		} else {
			this.addressStreet = addressStreet;
		}
	}

	public String getAddressWeb() {
		return addressWeb;
	}

	public void setAddressWeb(String addressWeb) {

		if (addressWeb != null) {
			this.addressWeb = addressWeb.substring(0, Math.min(addressWeb.length(), MAX_ADDRWEB_SIZE));
		} else {
			this.addressWeb = addressWeb;
		}
	}

	public String getAddressZip() {
		return addressZip;
	}

	public void setAddressZip(String addressZip) {

		if (addressZip != null) {
			this.addressZip = addressZip.substring(0, Math.min(addressZip.length(), MAX_ADDRZIP_SIZE));
		} else {
			this.addressZip = addressZip;
		}
	}

	public String getCityEnrollNumber() {
		return cityEnrollNumber;
	}

	public void setCityEnrollNumber(String cityEnrollNumber) {

		if (cityEnrollNumber != null) {
			this.cityEnrollNumber = cityEnrollNumber.substring(0, Math.min(cityEnrollNumber.length(), MAX_CITYENROLL_SIZE));
		} else {
			this.cityEnrollNumber = cityEnrollNumber;
		}
	}

	public CarrierDimension getCarrierDimension() {
		return carrierDimension;
	}

	public void setCarrierDimension(CarrierDimension carrierDimension) {
		this.carrierDimension = carrierDimension;
	}

	public long getCarrierDimensionUid() {
		
		return this.getCarrierDimension() != null? this.getCarrierDimension().getUid(): 0;
	}

	public void setCarrierDimensionUid(long carrierDMUid) {
		
		this.setCarrierDimension(new CarrierDimension(carrierDMUid));
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {

		if (fullName != null) {
			this.fullName = fullName.substring(0, Math.min(fullName.length(), MAX_FULLNAME_SIZE));
		} else {
			this.fullName = fullName;
		}
	}

	public String getStateEnrollNumber() {
		return stateEnrollNumber;
	}

	public void setStateEnrollNumber(String stateEnrollNumber) {

		if (stateEnrollNumber != null) {
			this.stateEnrollNumber = stateEnrollNumber.substring(0, Math.min(stateEnrollNumber.length(), MAX_STATEENROLL_SIZE));
		} else {
			this.stateEnrollNumber = stateEnrollNumber;
		}
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {

		if (taxId != null) {
			this.taxId = taxId.substring(0, Math.min(taxId.length(), MAX_TAXID_SIZE));
		} else {
			this.taxId = taxId;
		}
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
	
	public CacheableKey getAlternateKey() {
		if (this.alternateKey == null) {
			if (this.carrierDimension == null) {
				throw new IllegalStateException("Cannot create carrier data without carrier dimension.");
			}
			this.alternateKey = createAlternateKey(this.carrierDimension.getCarrierCode(), this.carrierDimension.getCarrierState());
		}
		return this.alternateKey;
	}

	public static final class InnerAlternateKey implements CacheableKey {
		
		private String carrierCode;
		private String state;
		
		public InnerAlternateKey( String _carrierCode, String _state) {
			this.carrierCode = _carrierCode;
			this.state = _state;
		}
		
		public boolean equals(Object _other) {
			if ( _other instanceof InnerAlternateKey ) {
				InnerAlternateKey other = (InnerAlternateKey) _other;
				return  this.carrierCode.equals(other.carrierCode) && 
						this.state.equals(other.state);
			}
			else 
				return false;
		}

		public int hashCode() {
			int hashcode = this.carrierCode.hashCode();
			hashcode += this.state.hashCode();
			return hashcode;
		}

		public String getCarrierCode() { return this.carrierCode; }
		public String getState()    { return this.state; }
	}
	
	public CacheableKey getKey() {
		if (this.key == null) {
			this.key = createKey(this.getUid());
		}
		return this.key;
	}
	
	public static final class InnerKey implements CacheableKey {
		
		private long uid;
		
		public InnerKey(long _uid ) {
			this.uid = _uid;
		}
		
		public boolean equals(Object _other) {
			if ( _other instanceof InnerKey ) {
				InnerKey other = (InnerKey) _other;
				return (this.uid == other.uid);
			}
			else 
				return false;
		}

		public int hashCode() {
			int hashcode = 37 + ((int) (17*this.uid));
			return hashcode;
		}
		
		public long getUid() { return this.uid; }
	}	
	

	
	// ---------------------------
	// Helper methods
	// ---------------------------

	public static final CacheableKey createKey(long _uid ) {
		return new InnerKey(_uid);
	}

	public static final CacheableKey createAlternateKey( String _carrierCode, String _state ) {
		return new InnerAlternateKey( _carrierCode, _state );
	}
}
