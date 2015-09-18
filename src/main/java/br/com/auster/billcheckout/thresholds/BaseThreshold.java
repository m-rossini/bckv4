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
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.om.reference.CustomizableEntity;

/**
 * @author framos
 * @version $Id$
 */
public abstract class BaseThreshold extends CustomizableEntity implements CacheableVO {

	
	
	public static final double DISABLED_LIMIT = -99999999.99;
	
	
	
	// ---------------------------
	// Instance attributes
	// ---------------------------
		
	// the upper monetary amount limit   
	protected double upperAmount;	
	// the lower monetary amount limit   
	protected double lowerAmount;	
	// hint message to use/display when the limit crossed
	protected String message;
	
	protected CacheableKey key;
	protected CacheableKey alternateKey;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
		
	public BaseThreshold() {
		this(0);
	}
	
	public BaseThreshold(long _uid) {
		super(_uid);
		this.upperAmount = BaseThreshold.DISABLED_LIMIT;
		this.lowerAmount = BaseThreshold.DISABLED_LIMIT;
	}
	
	
	
	// ---------------------------
	// Getters / Setters
	// ---------------------------	
	
	public final double getLowerAmount() {
		return lowerAmount;
	}
	public final void setLowerAmount(double _amount) {
		this.lowerAmount = _amount;
	}

	public final boolean isLowerAmountSet() {
		return Math.abs(this.lowerAmount - DISABLED_LIMIT) > 0.01;
	}
	
	public final double getUpperAmount() {
		return upperAmount;
	}
	public final void setUpperAmount(double _amount) {
		this.upperAmount = _amount;
	}

	public final boolean isUpperAmountSet() {
		return Math.abs(this.upperAmount - DISABLED_LIMIT) > 0.01;
	}
	
	public final String getHintMessage() { 
		return message;
	}
	public void setHintMessage(String _message) { 
		this.message = _message;
	}
	
	
	
	// ---------------------------
	// CacheableVO KEY attribute
	// ---------------------------	
	
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
			return (int) this.uid;
		}
		
		public long getUid() { return this.uid; }
	}	

	
	
	// ---------------------------
	// Helper methods
	// ---------------------------
	
	public static final CacheableKey createKey(long _uid ) {
		return new InnerKey(_uid);
	}
	
}
