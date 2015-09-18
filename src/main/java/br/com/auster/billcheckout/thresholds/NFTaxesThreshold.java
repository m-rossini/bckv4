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

/**
 * @author framos
 * @version $Id$
 */
public class NFTaxesThreshold extends BaseThreshold {

	
	
	// ---------------------------
	// Instance attributes
	// ---------------------------
		
	// which type of tax this threshold is related to
	protected TaxType tax;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
		
	public NFTaxesThreshold() {
		super();
		this.tax = TaxType.ANY; 
	}
	
	
	
	// ---------------------------
	// Getters / Setters
	// ---------------------------	
	
	public final TaxType getTaxType() {
		return this.tax;
	}
	public final void setTaxType(TaxType _tax) {
		this.tax = _tax;
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
			this.alternateKey = createAlternateKey(this.getTaxType().getSequenceId());
		}
		return this.alternateKey;
	}

	public static final class InnerAlternateKey implements CacheableKey {
		
		private int  taxtype;
		
		public InnerAlternateKey(int _taxtype) {
			this.taxtype = _taxtype;
		}
		
		public boolean equals(Object _other) {
			if ( _other instanceof InnerAlternateKey ) {
				InnerAlternateKey other = (InnerAlternateKey) _other;
				return (this.taxtype == other.taxtype);
			}
			else 
				return false;
		}

		public int hashCode() {
			int hashcode = 37;			
			hashcode += 17*this.taxtype;
			return hashcode;
		}

		public TaxType getTaxType() { return TaxType.getTaxType(this.taxtype); }
	}
		
	// ---------------------------
	// Helper methods
	// ---------------------------
	
	public static final CacheableKey createAlternateKey(int _taxtype) {
		return new InnerAlternateKey(_taxtype);
	}

}