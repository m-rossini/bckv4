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
import br.com.auster.billcheckout.consequence.telco.GeographicDimension;

/**
 * @author framos
 * @version $Id$
 */
public class InvoiceThreshold extends BaseThreshold {

	
	
	// ---------------------------
	// Instance attributes
	// ---------------------------
		
	// if customer is FLAG, LARGE, GOVERN or ONG 
	protected CustomerType custType;
	// the UF of the customer
	protected GeographicDimension uf;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
		
	public InvoiceThreshold() {
		super();
		this.custType = null; 
		this.uf = null;
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
	// Getters / Setters
	// ---------------------------	
	
	public final CustomerType getCustomerType() {
		return custType;
	}
	public final void setCustomerType(CustomerType _custType) {
		if (_custType != null) {
			this.custType = _custType;
		}
	}
	
	public final GeographicDimension getUF() {
		return this.uf;
	}
	public final void setUF(GeographicDimension _uf) {
		this.uf = _uf;
	}

	
	
	// ---------------------------
	// CacheableVO ALTERNATE KEY 
	//      attribute
	// ---------------------------		
	
	public CacheableKey getAlternateKey() {
		if (this.alternateKey == null) {
			this.alternateKey = createAlternateKey(this.getCustomerType(), this.getUF());
		}
		return this.alternateKey;
	}

	public static final class InnerAlternateKey implements CacheableKey {
		
		private CustomerType custType;
		private String uf;
		
		public InnerAlternateKey(CustomerType _custType, String _uf) {
			this.custType = _custType;
			this.uf = _uf;
		}
		
		public boolean equals(Object _other) {
			if ( _other instanceof InnerAlternateKey ) {
				InnerAlternateKey other = (InnerAlternateKey) _other;
				boolean result = (this.custType.equals(other.custType));
				if (this.uf == null) {
					return result && (other.uf == null); 
				} else {
					return result && (other.uf != null) && this.uf.equals(other.uf);
				}
			}
			else {
				return false;
			}
		}

		public int hashCode() {
			int hashcode = 37;			
			hashcode += this.custType.hashCode();
			hashcode += (int) (this.uf == null ? 0 : this.uf.hashCode());			
			return hashcode;
		}

		public CustomerType getCustomerType() { return this.custType; }
		public String getUF() { return this.uf; }
	}
		
	// ---------------------------
	// Helper methods
	// ---------------------------
	
	public static final CacheableKey createAlternateKey(CustomerType _custType, GeographicDimension _uf) {
		if (_uf == null) {
			return new InnerAlternateKey(_custType, null);
		}
		return new InnerAlternateKey(_custType, _uf.getState());
	}

}