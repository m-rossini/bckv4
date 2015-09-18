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
 *
 */
public class GeographicDimension extends CustomizableEntity {


	
	// ---------------------------
	// Instance variables
	// ---------------------------

	private String country;
	private String region;
	private String state;
	private String city;

	private Key key;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------

	public GeographicDimension() {
		this(0);
	}

    /**
     * This version will create a new instance that, due to the Hibernate mapping, will
     *   be treated as <strong>already registered</strong> in the database.
     * <p>
     * This means that is <code>saveOrUpdate()</code> is called, then an update (and not 
     *   an insert) will be executed. So, use this constructor wisely. 
     */
	public GeographicDimension(long _uid) {
		super(_uid);
	}

	public GeographicDimension(String _country, String _region, String _state, String _city) {
		this(0);
		this.setCountry(_country);
		this.setRegion(_region);
		this.setState(_state);
		this.setCity(_city);
	}	
	
	
	
	// ---------------------------
	// Public methods
	// ---------------------------

	public void setCountry(String _country) {
		this.country = _country;
	}
	public String getCountry() {
		return this.country;
	}

	public void setRegion(String _region) {
		this.region = _region;
	}
	public String getRegion() {
		return this.region;
	}

	public void setState(String _state) {
		this.state = _state;
	}
	public String getState() {
		return this.state;
	}
		
	public void setCity(String _city) {
		this.city = _city;
	}
	public String getCity() {
		return this.city;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getCountry() == null? 0 : 
			                        this.getCountry().hashCode());
		result = result*37 + (this.getState() == null? 0 : 
                                    this.getState().hashCode());
		result = result*37 + (this.getCity() == null? 0 : 
            						this.getCity().hashCode());
		return result;
	}	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() +  
		" : Country=[" + this.getCountry() + 
		"].Region=[" + this.getRegion() +
		"].State=[" + this.getState() + 
		"].City=[" + this.getCity() +		
		"]";
	}

	public Key getKey() {
		if (this.key == null) {
			this.key = new Key(this.getCountry(), this.getState(), this.getCity());
		}
		return this.key;
	}
	
	public static class Key {
		
		private String country;
		private String state;
		private String city;
		
		public Key(String _country, String _state, String _city) {
			this.country = _country;
			this.state = _state;
			this.city = _city;
		}
		
		public boolean equals(Object obj) {
			Key k = (Key) obj;
			boolean eq = (this.country == null ? (k.country == null) : (this.country.equals(k.country)));
			eq &= k.state.equals(this.state);
			eq &= (this.city == null ? (k.city == null) : (this.city.equals(k.city)));
			return eq;
		}
		
		public int hashCode() {
			int code = (this.country != null ? this.country.hashCode() : 0);
			code += this.state.hashCode();
			code += (this.city != null ? this.city.hashCode() : 0);
			return code;
		}
	}
	

	
	
	// ---------------------------
	// static methods
	// ---------------------------

	public static CustomizableEntity mergeNonKeyAttributes(GeographicDimension _geo1, GeographicDimension _geo2) {
		
		GeographicDimension finalObj = (GeographicDimension) CustomizableEntity.mergeNonKeyAttributes(_geo1, _geo2);
		GeographicDimension otherObj = (finalObj == _geo1 ? _geo2 : _geo1);
		if ((finalObj == null) || (otherObj == null)) {
			return finalObj;
		} else if (finalObj == _geo2) {
			otherObj = _geo1;
		}
		finalObj.setRegion((finalObj.getRegion() == null ? otherObj.getRegion() : 
			                                               finalObj.getRegion()));
		return finalObj;
	}	
}
