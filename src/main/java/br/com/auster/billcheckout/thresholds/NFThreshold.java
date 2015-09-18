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
public class NFThreshold extends BaseThreshold {

	
	
	// ---------------------------
	// Instance attributes
	// ---------------------------
		
	// if NF is for LOCAL
	protected boolean local;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
		
	public NFThreshold() {
		super();
		this.local = true; 
	}
	
	
	
	// ---------------------------
	// Getters / Setters
	// ---------------------------	
	
	public final boolean isLocalNF() {
		return this.local;
	}
	public final void setLocalNF(boolean _local) {
		this.local = _local;
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
			this.alternateKey = createAlternateKey(this.isLocalNF());
		}
		return this.alternateKey;
	}

	public static final class InnerAlternateKey implements CacheableKey {
		
		private boolean local;
		
		public InnerAlternateKey(boolean _local) {
			this.local = _local;
		}
		
		public boolean equals(Object _other) {
			if ( _other instanceof InnerAlternateKey ) {
				InnerAlternateKey other = (InnerAlternateKey) _other;
				return (this.local == other.local);
			}
			else 
				return false;
		}

		public int hashCode() {
			int hashcode = 37;			
			hashcode = (this.local ? (hashcode+1)*17 : (hashcode+2)*17);;
			return hashcode;
		}

		public boolean isLocalNF() { return this.local; }
	}
		
	// ---------------------------
	// Helper methods
	// ---------------------------
	
	public static final CacheableKey createAlternateKey(boolean _local) {
		return new InnerAlternateKey(_local);
	}

}