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
public class TimeDimension extends CustomizableEntity {


	
	// ---------------------------
	// Instance variables
	// ---------------------------

	private String year;
	private String month;
	private String day;
	
	private Key key;	


	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public TimeDimension() {
		this(0);
	}

    /**
     * This version will create a new instance that, due to the Hibernate mapping, will
     *   be treated as <strong>already registered</strong> in the database.
     * <p>
     * This means that is <code>saveOrUpdate()</code> is called, then an update (and not 
     *   an insert) will be executed. So, use this constructor wisely. 
     */
	public TimeDimension(long _uid) {
		super(_uid);
	}

	public TimeDimension(String _year, String _month, String _day) {
		this(0);
		this.setYear(_year);
		this.setMonth(_month);
		this.setDay(_day);
	}

	public TimeDimension(Date _date) {
		this(0);
		this.setDate(_date);
	}
	
	
	
	// ---------------------------
	// Public methods
	// ---------------------------
	
	public void setYear(String _year) {
		this.year = _year;
	}
	public String getYear() {
		return this.year;
	}

	public void setMonth(String _month) {
		this.month = _month;
	}
	public String getMonth() {
		return this.month;
	}

	public void setDay(String _day) {
		this.day = _day;
	}
	public String getDay() {
		return this.day;
	}

	/*
	 * These next two methods are helper to get the current time object in 
	 * 	the form of a java.util.Date, and to set all attributes at once using
	 *  a instance of java.util.Date.
	 */
	public Date getDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(this.getYear()));
		c.set(Calendar.MONTH, Integer.parseInt(this.getMonth()));
		c.roll(Calendar.MONTH, -1);
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(this.getDay()));
		return c.getTime();
	}
	
	public void setDate(Date _date) {
		if (_date == null) { return; }
		Calendar c = Calendar.getInstance();
		c.setTime(_date);
		this.setYear(String.valueOf(c.get(Calendar.YEAR)));
		this.setMonth(String.valueOf(c.get(Calendar.MONTH)+1));
		this.setDay(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getDate() == null? 0 : 
			                        this.getDate().hashCode());
		return result;
	}	
	
	/**
	 * @see java.lang.Object#toString()
	 */	
	public String toString() {
		return super.toString() + " : Date=[" + this.getDate() + "]";
	}	
	
	public Key getKey() {
		if (this.key == null) {
			this.key = new Key(this.getYear(), this.getMonth(), this.getDay());
		}
		return this.key;
	}
	
	public static class Key {
		
		private String year;
		private String month;
		private String day;
		
		public Key(String _year, String _month, String _day) {
			this.year = _year;
			this.month = _month;
			this.day = _day;
		}
		
		public boolean equals(Object obj) {
			Key k = (Key) obj;
			boolean result = true;
			if (this.year == null) {
				result &= (k.year == null);
			} else {
				result &= this.year.equals(k.year);
			}
			if (this.month == null) {
				result &= (k.month == null);
			} else {
				result &= this.month.equals(k.month);
			}
			if (this.day == null) {
				result &= (k.day == null);
			} else {
				result &= this.day.equals(k.day);
			}
			return result;
		}
		
		public int hashCode() {			
			int code = 37;
			code += ( this.year == null ? 0 : this.year.hashCode() );
			code += ( this.month == null ? 0 : this.month.hashCode() );
			code += ( this.day == null ? 0 : this.day.hashCode() );
			return code;
		}
	}	
}
