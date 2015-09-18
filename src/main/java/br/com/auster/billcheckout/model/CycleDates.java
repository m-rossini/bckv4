/*
* Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
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
* Created on 29/08/2006
*/
package br.com.auster.billcheckout.model;

import java.util.Date;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.billcheckout.thresholds.CustomerType;
import br.com.auster.om.reference.CustomizableEntity;


/**
 * @author mtengelm
 * @version $Id: CycleDates.java 125 2006-10-11 18:38:54Z mtengelm $
 */
public class CycleDates extends CustomizableEntity implements CacheableVO {

	
	
	// ---------------------------
	// Instance attributes
	// ---------------------------
	
	protected String code;
	protected CustomerType customerType;
	protected Date startDate;
	protected Date endDate;  
	protected Date issueDate;
	protected Date refDate;  
	protected Date dueDate;
	
	protected CacheableKey key;
	protected CacheableKey alternateKey;


	
	// ---------------------------
	// Constructors
	// ---------------------------

	public CycleDates() {
		this(0);
	}

	public CycleDates(long _uid) {
		super(0);
	}
	

	
	// ---------------------------
	// Getters / Setters
	// ---------------------------
	
	public final String getCycleCode() { return code;	}
	public final void setCycleCode(String code) { this.code = code; }

	public final Date getEndDate() { return this.endDate;	}
	public final void setEndDate(Date endDate) { 
		if (endDate != null) {
			endDate = new Date(endDate.getTime());
		}
		this.endDate = endDate;
	}

	public final Date getIssueDate() { return this.issueDate;	}
	public final void setIssueDate(Date issueDate) { 
		if (issueDate != null) {
			issueDate = new Date(issueDate.getTime());
		}
		this.issueDate = issueDate; 
	}

	public final Date getReferenceDate() { return this.refDate; }
	public final void setReferenceDate(Date refDate) { 
		if (refDate != null) {
			refDate = new Date(refDate.getTime());
		}
		this.refDate = refDate; 
	}

	public final Date getStartDate() { return this.startDate;	}
	public final void setStartDate(Date startDate) { 
		if (startDate != null) {
			startDate = new Date(startDate.getTime());
		}
		this.startDate = startDate; 
	}

	public final Date getDueDate() { return this.dueDate; }
	public final void setDueDate(Date dueDate) { 
		if (dueDate != null) {
			dueDate = new Date(dueDate.getTime());
		}
		this.dueDate = dueDate; 
	}
	
	public final CustomerType getCustomerType() { return this.customerType; }
	public final void setCustomerType(CustomerType  _type) { this.customerType = _type; }
	
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

	public CacheableKey getAlternateKey() {
		if (this.alternateKey == null) {
			this.alternateKey = createAlternateKey(this.getCycleCode(), this.getEndDate().getTime(), this.customerType, this.getDueDate().getTime());
		}
		return this.alternateKey;
	}

	public static final class InnerAlternateKey implements CacheableKey {
		
		private String code;
		private long enddate;
		private CustomerType  custtype;
		private long duedate;
		
		public InnerAlternateKey(String _code, long _enddate, CustomerType  _type, long _duedate) {
			this.enddate = _enddate;
			this.code = _code;
			this.custtype = _type;
			this.duedate = _duedate;
		}
		
		public boolean equals(Object _other) {
			if ( _other instanceof InnerAlternateKey ) {
				InnerAlternateKey other = (InnerAlternateKey) _other;
				boolean state = this.code.equals(other.code) && (this.enddate == other.enddate) && (this.duedate == other.duedate);
				if (state) {
					if (this.custtype == null) {
						state &= (other.custtype == null);
					} else {
						state &=  this.custtype.equals(other.custtype); 
					}
				}
				return state;
			}
			return false;
		}

		public int hashCode() {
			int hashcode = 37;			
			hashcode += this.code.hashCode();
			hashcode += 17*this.enddate;			
			hashcode += (this.custtype == null ? 0 : 17*this.custtype.hashCode());
			hashcode += 17*this.duedate;
			return hashcode;
		}

		public String getCycleCode() { return this.code; }
		public Date getEndDate() { return new Date(this.enddate); }
		public CustomerType getCustomerType() { return this.custtype; }
		public Date getDueDate() { return new Date(this.duedate); }
	}
			
	

	// ---------------------------
	// Helper methods
	// ---------------------------
	
	public static final CacheableKey createKey(long _uid ) {
		return new InnerKey(_uid);
	}

	public static final CacheableKey createAlternateKey(String _code, long _enddate, CustomerType _custtype, long _duedate) {
		return new InnerAlternateKey(_code, _enddate, _custtype, _duedate);
	}

}
