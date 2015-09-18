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

import java.util.Calendar;
import java.util.Date;

import br.com.auster.om.reference.CustomizableEntity;

/**
 * @author framos
 * @version $Id$
 */
public class CycleDimension extends CustomizableEntity {


	
	// ---------------------------
	// Instance variables
	// ---------------------------

	private String code;
	private Date cutDate;
	private Date issueDate;
	private Date dueDate;

	private int year;
	private int month;
	
	private Key key;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public CycleDimension() {
		this(0);
	}

    /**
     * This version will create a new instance that, due to the Hibernate mapping, will
     *   be treated as <strong>already registered</strong> in the database.
     * <p>
     * This means that is <code>saveOrUpdate()</code> is called, then an update (and not 
     *   an insert) will be executed. So, use this constructor wisely. 
     */
	public CycleDimension(long _uid) {
		super(_uid);
		this.initCycleDimension();
	}

	public CycleDimension(String _code, Date _cut, Date _issue, Date _due) {
		this(0);
		this.setCycleCode(_code);
		this.setCutDate(_cut);
		this.setIssueDate(_issue);
		this.setDueDate(_due);
	}
	
	
	
	// ---------------------------
	// Public methods
	// ---------------------------
	
	public void setCycleCode(String _code) {
		this.code = _code;
	}
	public String getCycleCode() {
		return this.code;
	}

	public void setCutDate(Date _date) {
		this.cutDate = _date;
		if (this.cutDate == null) { return; }
		Calendar c = Calendar.getInstance();
		c.setTime(_date);
		// setting year and month of this cycle
		this.year = c.get(Calendar.YEAR);
		this.month = c.get(Calendar.MONTH)+1;
	}
	public Date getCutDate() {
		return this.cutDate;
	}

	public void setIssueDate(Date _date) {
		this.issueDate = _date;
	}
	public Date getIssueDate() {
		return this.issueDate;
	}


	public void setDueDate(Date _date) {
		this.dueDate = _date;
	}
	public Date getDueDate() {
		return this.dueDate;		
	}

	/*
	 * These next two methods are helper to get the year and month of the current cycle 
	 */
	public int getCycleYear() {
		return this.year;
	}
	
	public int getCycleMonth() {
		return this.month;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getCycleCode() == null? 0 : 
			                        this.getCycleCode().hashCode());
		result = result*37 + (this.getCutDate() == null? 0 : 
                                    this.getCutDate().hashCode());
		return result;
	}	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() +  
		" : Code=[" + this.getCycleCode() + 
		"].Year=[" + this.getCycleYear() +
		"].Month=[" + this.getCycleMonth() + 
		"].CutDate=[" + this.getCutDate() +		
		"]";
	}	
	
	public Key getKey() {
		if (key == null) {
			this.key = new Key(this.getCycleCode(), this.getCutDate(), this.getDueDate());
		}
		return this.key;
	}
	
	public static class Key {
		
		private String cycleCode;
		private String cutDate;
		private String dueDate;
		
		public Key(String _cycleCode, Date _cutDate, Date _dueDate) {
			this.cycleCode = (_cycleCode == null ? "" : _cycleCode);
			this.cutDate = (_cutDate == null ? "" : (new java.sql.Date(_cutDate.getTime())).toString());
			this.dueDate = (_dueDate == null ? "" : (new java.sql.Date(_dueDate.getTime())).toString());
		}
		
		public boolean equals(Object obj) {
			Key k = (Key) obj;
			boolean result = true;
			if (this.cycleCode == null) {
				result &= (k.cycleCode == null);
			} else {
				result &= this.cycleCode.equals(k.cycleCode);
			}
			if (this.cutDate == null) {
				result &= (k.cutDate == null);
			} else {
				result &= this.cutDate.equals(k.cutDate);
			}
			if (this.dueDate == null) {
				result &= (k.dueDate == null);
			} else {
				result &= this.dueDate.equals(k.dueDate);
			}
			return result;
		}
		
		public int hashCode() {
			int code = this.cycleCode.hashCode();
			code += this.cutDate.hashCode();
			code += this.dueDate.hashCode();
			return code;
		}
		
		public String toString() {
			return "KEY=" + this.cycleCode + "/" + this.cutDate + "/" + this.dueDate;
		}
	}
	
	
	
	// ---------------------------
	// Private methods
	// ---------------------------
	
	private void initCycleDimension() {
		this.year = -1;
		this.month = -1;
	}
	
	
	
	// ---------------------------
	// static methods
	// ---------------------------

	public static CustomizableEntity mergeNonKeyAttributes(CycleDimension _cycle1, CycleDimension _cycle2) {
		
		CycleDimension finalObj = (CycleDimension) CustomizableEntity.mergeNonKeyAttributes(_cycle1, _cycle2);
		CycleDimension otherObj = (finalObj == _cycle1 ? _cycle2 : _cycle1);
		if ((finalObj == null) || (otherObj == null)) {
			return finalObj;
		} else if (finalObj == _cycle2) {
			otherObj = _cycle1;
		}
		finalObj.setIssueDate((finalObj.getIssueDate() == null ? otherObj.getIssueDate() : 
			                                                     finalObj.getIssueDate()));
		return finalObj;
	}	
}
