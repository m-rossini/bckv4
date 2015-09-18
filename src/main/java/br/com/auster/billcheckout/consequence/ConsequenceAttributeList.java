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
 * Created on Apr 1, 2005
 */
package br.com.auster.billcheckout.consequence;

import br.com.auster.om.reference.PKEnabledEntity;

/**
 * @author framos
 * @version $Id$
 */
public class ConsequenceAttributeList extends PKEnabledEntity {

	
	
	// ---------------------------
	// Class constants
	// ---------------------------
	
	public static final int MAX_ATTRNAME_SIZE = 32;
	public static final int MAX_ATTRVALUE_SIZE = 128;
	
	public static final int ATTRLIST_SIZE = 20;
	
	
	// ---------------------------
	// Instance variables
	// ---------------------------

	public enum AttributeType { STRING, DATE, TIME, DATETIME, INTEGER, DOUBLE };
	protected String[] attributeName;
	protected String[] attributeValue;
	protected boolean[] usedSlots;

	
	// ---------------------------
	// Constructors
	// ---------------------------
	
    /**
     * This implementation will create a new, <strong>and unsaved</code> attribute for 
     * 	 billcheckout consequences.  
     */
	public ConsequenceAttributeList() {	
		this(0);
	}

	public ConsequenceAttributeList(long _uid) {	
		this.setUid(_uid);
		this.attributeName = new String[ATTRLIST_SIZE];
		this.attributeValue = new String[ATTRLIST_SIZE];
		this.usedSlots = new boolean[ATTRLIST_SIZE];
	}
	

	
	// ---------------------------
	// Public methods
	// ---------------------------
	
	public String addAttribute(String _name, String _value) {
		int freePos = 0;
		while (this.usedSlots[freePos]) {
			freePos++;
		}
		return this.addAttribute(freePos, _name, _value);
	}
	
	public String addAttribute(int _pos, String _name, String _value) {
		if (_pos >= ATTRLIST_SIZE) {
			throw new ArrayIndexOutOfBoundsException("Cannot add attribute to position " + _pos); 
		}
		String oldAttr = this.getAttributeValue(_pos);
		this.setAttributeName(_pos, _name);
		this.setAttributeValue(_pos, _value);
		this.usedSlots[_pos] = true;
		return oldAttr;
	}
	
	public String getAttribute(int _pos) {
		if (_pos >= ATTRLIST_SIZE) {
			throw new ArrayIndexOutOfBoundsException("Cannot get attribute from position " + _pos);
		}
		return this.getAttributeValue(_pos); 
	}
	
	public String getAttributeNameByPosition(int _pos) {
		if (_pos >= ATTRLIST_SIZE) {
			throw new ArrayIndexOutOfBoundsException("Cannot get attribute from position " + _pos);
		}
		return this.getAttributeName(_pos); 
	}	

	
	// ---------------------------
	// Hibernate methods
	// ---------------------------
	
	// Methods for manipulating attribute names
	
	public final String getAttributeName1() { return this.getAttributeName(0);	}
	public final void setAttributeName1(String _attr) { this.setAttributeName(0, _attr); }

	public final String getAttributeName2() { return this.getAttributeName(1); }
	public final void setAttributeName2(String _attr) { this.setAttributeName(1, _attr); }

	public final String getAttributeName3() { return this.getAttributeName(2); }
	public final void setAttributeName3(String _attr) {	this.setAttributeName(2, _attr); }

	public final String getAttributeName4() { return this.getAttributeName(3);	}
	public final void setAttributeName4(String _attr) {	this.setAttributeName(3, _attr); }

	public final String getAttributeName5() { return this.getAttributeName(4); }
	public final void setAttributeName5(String _attr) { this.setAttributeName(4, _attr); }

	public final String getAttributeName6() { return this.getAttributeName(5); }
	public final void setAttributeName6(String _attr) {	this.setAttributeName(5, _attr); }

	public final String getAttributeName7() { return this.getAttributeName(6); }
	public final void setAttributeName7(String _attr) {	this.setAttributeName(6, _attr); }

	public final String getAttributeName8() { return this.getAttributeName(7);	}
	public final void setAttributeName8(String _attr) { this.setAttributeName(7, _attr); }

	public final String getAttributeName9() { return this.getAttributeName(8); }
	public final void setAttributeName9(String _attr) { this.setAttributeName(8, _attr); }

	public final String getAttributeName10() { return this.getAttributeName(9); }
	public final void setAttributeName10(String _attr) {	this.setAttributeName(9, _attr); }

	public final String getAttributeName11() { return this.getAttributeName(10);	}
	public final void setAttributeName11(String _attr) {	this.setAttributeName(10, _attr); }

	public final String getAttributeName12() { return this.getAttributeName(11); }
	public final void setAttributeName12(String _attr) { this.setAttributeName(11, _attr); }

	public final String getAttributeName13() { return this.getAttributeName(12); }
	public final void setAttributeName13(String _attr) {	this.setAttributeName(12, _attr); }

	public final String getAttributeName14() { return this.getAttributeName(13); }
	public final void setAttributeName14(String _attr) {	this.setAttributeName(13, _attr); }

	public final String getAttributeName15() { return this.getAttributeName(14); }
	public final void setAttributeName15(String _attr) { this.setAttributeName(14, _attr); }

	public final String getAttributeName16() { return this.getAttributeName(15); }
	public final void setAttributeName16(String _attr) {	this.setAttributeName(15, _attr); }

	public final String getAttributeName17() { return this.getAttributeName(16);	}
	public final void setAttributeName17(String _attr) {	this.setAttributeName(16, _attr); }

	public final String getAttributeName18() { return this.getAttributeName(17); }
	public final void setAttributeName18(String _attr) { this.setAttributeName(17, _attr); }

	public final String getAttributeName19() { return this.getAttributeName(18); }
	public final void setAttributeName19(String _attr) {	this.setAttributeName(18, _attr); }

	public final String getAttributeName20() { return this.getAttributeName(19); }
	public final void setAttributeName20(String _attr) {	this.setAttributeName(19, _attr); }

	// Methods for manipulating attribute values
	
	public final String getAttributeValue1() { return this.getAttributeValue(0);	}
	public final void setAttributeValue1(String _attr) { this.setAttributeValue(0, _attr); }

	public final String getAttributeValue2() { return this.getAttributeValue(1); }
	public final void setAttributeValue2(String _attr) { this.setAttributeValue(1, _attr); }

	public final String getAttributeValue3() { return this.getAttributeValue(2); }
	public final void setAttributeValue3(String _attr) {	this.setAttributeValue(2, _attr); }

	public final String getAttributeValue4() { return this.getAttributeValue(3);	}
	public final void setAttributeValue4(String _attr) {	this.setAttributeValue(3, _attr); }

	public final String getAttributeValue5() { return this.getAttributeValue(4); }
	public final void setAttributeValue5(String _attr) { this.setAttributeValue(4, _attr); }

	public final String getAttributeValue6() { return this.getAttributeValue(5); }
	public final void setAttributeValue6(String _attr) {	this.setAttributeValue(5, _attr); }

	public final String getAttributeValue7() { return this.getAttributeValue(6); }
	public final void setAttributeValue7(String _attr) {	this.setAttributeValue(6, _attr); }

	public final String getAttributeValue8() { return this.getAttributeValue(7);	}
	public final void setAttributeValue8(String _attr) { this.setAttributeValue(7, _attr); }

	public final String getAttributeValue9() { return this.getAttributeValue(8); }
	public final void setAttributeValue9(String _attr) { this.setAttributeValue(8, _attr); }

	public final String getAttributeValue10() { return this.getAttributeValue(9); }
	public final void setAttributeValue10(String _attr) {	this.setAttributeValue(9, _attr); }

	public final String getAttributeValue11() { return this.getAttributeValue(10);	}
	public final void setAttributeValue11(String _attr) {	this.setAttributeValue(10, _attr); }

	public final String getAttributeValue12() { return this.getAttributeValue(11); }
	public final void setAttributeValue12(String _attr) { this.setAttributeValue(11, _attr); }

	public final String getAttributeValue13() { return this.getAttributeValue(12); }
	public final void setAttributeValue13(String _attr) {	this.setAttributeValue(12, _attr); }

	public final String getAttributeValue14() { return this.getAttributeValue(13); }
	public final void setAttributeValue14(String _attr) {	this.setAttributeValue(13, _attr); }

	public final String getAttributeValue15() { return this.getAttributeValue(14); }
	public final void setAttributeValue15(String _attr) { this.setAttributeValue(14, _attr); }

	public final String getAttributeValue16() { return this.getAttributeValue(15); }
	public final void setAttributeValue16(String _attr) {	this.setAttributeValue(15, _attr); }

	public final String getAttributeValue17() { return this.getAttributeValue(16);	}
	public final void setAttributeValue17(String _attr) {	this.setAttributeValue(16, _attr); }

	public final String getAttributeValue18() { return this.getAttributeValue(17); }
	public final void setAttributeValue18(String _attr) { this.setAttributeValue(17, _attr); }

	public final String getAttributeValue19() { return this.getAttributeValue(18); }
	public final void setAttributeValue19(String _attr) {	this.setAttributeValue(18, _attr); }

	public final String getAttributeValue20() { return this.getAttributeValue(19); }
	public final void setAttributeValue20(String _attr) {	this.setAttributeValue(19, _attr); }
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer bf = new StringBuffer();
		for (int i=0; i < ATTRLIST_SIZE; i++) {
			bf.append("ConsequenceAttributeList { ");
			if (this.usedSlots[i]) {
				bf.append("[");
				bf.append(i);
				bf.append("] ");
				bf.append(this.getAttributeName(i));
				bf.append("=");
				bf.append(this.getAttributeValue(i));
				bf.append(",");
			}
			bf.append(" }");
		}
		return bf.toString();
	}

	
	
	// ---------------------------
	// Private methods
	// ---------------------------
	
	private final void setAttributeName(int _pos, String _name) {
		this.attributeName[_pos] = (_name.length() > MAX_ATTRNAME_SIZE ? 
			                        _name.substring(0, MAX_ATTRNAME_SIZE) :
			                        _name);
	}

	private final String getAttributeName(int _pos) {
		return this.attributeName[_pos];
	}

	private final void setAttributeValue(int _pos, String _value) {
		this.attributeValue[_pos] = (_value.length() > MAX_ATTRVALUE_SIZE ? 
				                     _value.substring(0, MAX_ATTRVALUE_SIZE) :
					                 _value);
	}
	
	private final String getAttributeValue(int _pos) {
		return this.attributeValue[_pos];
	}
}
