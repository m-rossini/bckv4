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
public class UsageThreshold extends BaseThreshold {

	
	
	// ---------------------------
	// Instance attributes
	// ---------------------------
		
	// if usage was originated when in HOME or ROAM
	protected boolean home;
	// if usage was a collect call 
	protected boolean collectCall;
	// when voice usage, if call is VC1, VC2, ... 
	protected VoiceCallType calltype;
	// which part of the week/day the usage was originated 
	protected TimePeriod daytime;
	// if destination was in the same NET, was MOBILE phone, NON-MOBILE, ...
	protected DestinationType destination;
	// if usage is VOICE, DATA or EVENT
	protected UsageType usageType;
	// if this threshold should be evaluated as RELATIVE or ABSOLUTE
	protected boolean relative;
	// the lower duration time/period/amount limit 
	protected long lowerDuration;
	// the upper duration time/period/amount limit 
	protected long upperDuration;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
		
	public UsageThreshold() {
		super();
		this.calltype = VoiceCallType.ANY;
		this.daytime = TimePeriod.ANY;
		this.destination = DestinationType.ANY;
		this.usageType = UsageType.VOICE;
		this.home = true;
		this.collectCall = false;
		this.relative = false;
		this.lowerDuration = 0;
		this.upperDuration = 0;
	}
	
	
	
	// ---------------------------
	// Getters / Setters
	// ---------------------------	
	
	public final VoiceCallType getCallType() {
		return calltype;
	}
	public final void setCallType(VoiceCallType _calltype) {
		if (_calltype != null) {
			calltype = _calltype;
		}
	}
	
	public final boolean isCollectCall() {
		return collectCall;
	}
	public final void setCollectCall(boolean collectCall) {
		this.collectCall = collectCall;
	}
	
	public final DestinationType getDestination() {
		return destination;
	}
	public final void setDestination(DestinationType _destination) {
		if (_destination != null) {
			this.destination = _destination;
		}
	}

	public final long getLowerDuration() {
		return this.lowerDuration;
	}
	public final void setLowerDuration(long duration) {
		this.lowerDuration = duration;
	}
	
	public final long getUpperDuration() {
		return this.upperDuration;
	}
	public final void setUpperDuration(long duration) {
		this.upperDuration = duration;
	}
	
	public final boolean isHome() {
		return home;
	}
	public final void setHome(boolean home) {
		this.home = home;
	}
	
	public final boolean isRelative() {
		return relative;
	}
	public final void setRelative(boolean relative) {
		this.relative = relative;
	}
	
	public final boolean isVoiceUsage() {
		return UsageType.VOICE.equals(usageType);
	}
	public final boolean isEventUsage() {
		return UsageType.EVENT.equals(usageType);
	}
	public final boolean isDataUsage() {
		return UsageType.DATA.equals(usageType);
	}

	public final UsageType getUsageType() {
		return this.usageType;
	}	
	public final void setUsageType(UsageType _usageType) {
		if (_usageType != null) {
			this.usageType = _usageType;
		}
	}
	
	public final TimePeriod getTimePeriod() {
		return daytime;
	}
	public final void setTimePeriod(TimePeriod _daytime) {
		if (_daytime != null) {
			this.daytime = _daytime;
		}
	}
	
	/**
	 * This helper method returns the relative lower limit for this threshold. It returns the 
	 * 	result of <code>lowerAmount / lowerDuration</code> if lowerDuration is not zero. When this 
	 *  happends, then zero is returned. 
	 * 	
	 * @return the result of <code>lowerAmount / lowerDuration</code>, or zero.
	 */
	public final double getRelativeLowerLimit() {
		if (this.lowerDuration == 0) {
			return 0;
		}
		return (this.lowerAmount / this.lowerDuration);
	}

	/**
	 * This helper method returns the relative upper limit for this threshold. It returns the 
	 * 	result of <code>upperAmount / upperDuration</code> if upperDuration is not zero. When this 
	 *  happends, then zero is returned. 
	 * 	
	 * @return the result of <code>upperAmount / upperDuration</code>, or zero.
	 */
	public final double getRelativeUpperLimit() {
		if (this.upperDuration == 0) {
			return 0;
		}
		return (this.upperAmount / this.upperDuration);
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
			this.alternateKey = createAlternateKey(this.isHome(), this.isCollectCall(), 
					                  this.isRelative(), this.getCallType(), this.getTimePeriod(), 
					                  this.getDestination(), this.getUsageType());
		}
		return this.alternateKey;
	}

	public static final class InnerAlternateKey implements CacheableKey {
		
		private boolean home;
		private boolean collectCall;
		private boolean relative;
		private int calltype;
		private int timeperiod;
		private int destination;
		private int usagetype;
		
		public InnerAlternateKey(boolean _home, boolean _collect, boolean _relative, int _calltype, 
				                 int _timeperiod, int _desttype, int _usagetype) {
			this.home = _home;
			this.collectCall = _collect;
			this.relative = _relative;
			this.calltype = _calltype;
			this.timeperiod = _timeperiod;
			this.destination = _desttype;
			this.usagetype = _usagetype;
		}
		
		public boolean equals(Object _other) {
			if ( _other instanceof InnerAlternateKey ) {
				InnerAlternateKey other = (InnerAlternateKey) _other;
				
				return ((this.home == other.home) && (this.collectCall == other.collectCall) &&
						(this.usagetype == other.usagetype) && (this.relative == other.relative) &&
						(this.calltype == other.calltype) && (this.timeperiod == other.timeperiod) && 
						(this.destination == other.destination));
				
			}
			else 
				return false;
		}

		public int hashCode() {
			int hashcode = 37;			
			hashcode = (this.home ? (hashcode+1)*17 : (hashcode+2)*17);
			hashcode = (this.collectCall ? (hashcode+1)*17 : (hashcode+2)*17);
			hashcode = (this.relative ? (hashcode+1)*17 : (hashcode+2)*17);
			hashcode = ((this.calltype+1) * hashcode);
			hashcode = ((this.timeperiod+1) * hashcode);
			hashcode = ((this.destination+1) * hashcode);
			hashcode = ((this.usagetype+1) * hashcode);
			return hashcode;
		}

		public boolean isHome() { return this.home; }
		public boolean isCollectCall() { return this.collectCall; }
		public boolean isRelative() { return this.relative; }
		public VoiceCallType getCallType() { return VoiceCallType.getVoiceCallType(this.calltype); }
		public TimePeriod getTimePeriod() { return TimePeriod.getTimePeriod(this.timeperiod); }
		public DestinationType getDestination() { return DestinationType.getDestinationType(this.destination); }
		public UsageType getUsageType() { return UsageType.getUsageType(this.usagetype); }		
	}
		
	// ---------------------------
	// Helper methods
	// ---------------------------
	
	public static final CacheableKey createAlternateKey(boolean _home, boolean _collectcall, boolean _relative, 
			                                            VoiceCallType _calltype, TimePeriod _timeperiod,
			                                            DestinationType _desttype, UsageType _usagetype ) {
		
		return new InnerAlternateKey(_home, _collectcall, _relative, _calltype.getSequenceId(), 
				                     _timeperiod.getSequenceId(), _desttype.getSequenceId(),
				                     _usagetype.getSequenceId());
	}

}