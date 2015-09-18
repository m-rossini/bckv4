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
 * Created on Jan 31, 2005
 */
package br.com.auster.billcheckout.consequence;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import br.com.auster.billcheckout.ruleobjects.Rule;
import br.com.auster.om.reference.CustomizableEntity;

/**
 * @author framos
 * @version $Id$
 */
public abstract class Consequence extends CustomizableEntity {

	
	
	// ---------------------------
	// Class constants
	// ---------------------------
	
	private static final ThreadLocal decimalFormatter = new ThreadLocal() {
		protected Object initialValue() {
			return new DecimalFormat();
		};
	};
	
	public static final String DEFAULT_NUMBER_FORMAT = "#0.00";
	
	public static final int MAX_DESCRIPTION_SIZE = 128;
	public static final int MAX_FILENAME_SIZE = 256;
	public static final int MAX_TRANSACTIONID_SIZE = 64;
	
	
	
	// ---------------------------
	// Instance variables
	// ---------------------------

    private Rule rule;
    private String description;
    private String transactionId;
    private String filename;
    private String custom4;
    private String custom5;
    ConsequenceAttributeList attributes;
    private ConsequenceLevel level;
    
    public enum ConsequenceLevel { ERROR, WARNING, INFO, REPORT, DEBUG };
    
    
   
	// ---------------------------
	// Constructors
	// ---------------------------
    
    /**
     * This implementation will create a new, <strong>and unsaved</code> instance of 
     * 	 a billcheckout consequence.  
     */
    public Consequence() { 
    	this(0);
    }

    /**
     * This version will create a new instance of a billcheckout consequence. But, differently
     *   from {@link #Consequence()}, and due to the Hibernate mapping, this new instance
     *   will be treated as <strong>already registered</strong> in the database.
     * <p>
     * This means that is <code>saveOrUpdate()</code> is called, then an update (and not an insert)
     *   will be executed. So, use this constructor wisely. 
     *  
     * @param _uid
     */
    public Consequence(long _uid) {
    	super(_uid);
    	this.level = ConsequenceLevel.INFO;
    	this.attributes = new ConsequenceAttributeList();
    }



	// ---------------------------
	// Public methods
	// ---------------------------

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String _desc) {
		if (_desc != null) {
			this.description = _desc.substring(0, Math.min(_desc.length(), MAX_DESCRIPTION_SIZE));
		} else {
			this.description = _desc;
		}
	}
    
	public String getCustom4() {
		return this.custom4;
	}
	public void setCustom4(String _custom) {
		this.custom4 = _custom;
	}
		
	public String getCustom5() {
		return this.custom5;
	}
	public void setCustom5(String _custom) {
		this.custom5 = _custom;
	}	    

	public String getTransactionId() {
		return this.transactionId;
	}
	public void setTransactionId(String _transactionId) {
		if (_transactionId != null) {
			this.transactionId = _transactionId.substring(0, Math.min(_transactionId.length(), MAX_TRANSACTIONID_SIZE));
		} else {
			this.transactionId = _transactionId;
		}
	}	   
	
	public Rule getRelatedRule() {
		return this.rule;
	}
	public void setRelatedRule(Rule _rule) {
		this.rule = _rule;
	}

	public ConsequenceLevel getLevel() {
		return this.level;
	}
	public void setLevel(ConsequenceLevel _level) {
		this.level = _level;
	}

	public String getFilename() {
		return this.filename;
	}
	public void setFilename(String _filename) {
		if (_filename == null) {
			this.filename = _filename;
		} else {
			this.filename = _filename.substring(0, Math.min(_filename.length(), MAX_FILENAME_SIZE));
		}
	}
	
	/**
	 * One-side of a many-to-one relationship with the attribute table
	 */
	public ConsequenceAttributeList getAttributes() {
		return this.attributes;
	}

	public void setAttributes(ConsequenceAttributeList _attributes) {
		this.attributes = _attributes;
	}
	
	public void addAttribute(String _name, Object _value) {
		if (_value == null) {
			this.addNullAttribute(_name);
		} else if (_value instanceof Date) {
			this.addAttribute(_name, _value, ConsequenceAttributeList.AttributeType.DATETIME, null);
		} else if (_value instanceof Number) {
			this.addAttribute(_name, _value, ConsequenceAttributeList.AttributeType.DOUBLE, null);
		} else {
			this.addAttribute(_name, _value, ConsequenceAttributeList.AttributeType.STRING, null);
		}
	}

	public void addAttribute(String _name, Object _value, ConsequenceAttributeList.AttributeType _type) {
		this.addAttribute(_name, _value, _type, null);
	}

	public void addAttribute(String _name, Object _value, ConsequenceAttributeList.AttributeType _type, String _format) {
		if (_value == null) {
			this.addNullAttribute(_name);
		} else if (_type == ConsequenceAttributeList.AttributeType.INTEGER) {
			this.addLongAttribute(_name, ((Number)_value).longValue(), _format);
		} else if (_type == ConsequenceAttributeList.AttributeType.DOUBLE) {
			this.addDoubleAttribute(_name, ((Number)_value).doubleValue(), _format);
		} else if (_type == ConsequenceAttributeList.AttributeType.DATE) {
			this.addDateAttribute(_name, (Date)_value, _format);
		} else if (_type == ConsequenceAttributeList.AttributeType.TIME) {
			this.addTimeAttribute(_name, (Date)_value, _format);			
		} else if (_type == ConsequenceAttributeList.AttributeType.DATETIME) {
			this.addDateTimeAttribute(_name, (Date)_value, _format);
		} else {
			this.addStringAttribute(_name, _value.toString());
		}
	}

	public void addStringAttribute(String _name, String _value) {
		if (_value == null) {
			this.addNullAttribute(_name);
		} else {
			this.attributes.addAttribute(_name, _value);
		}
	}

	public void addDateAttribute(String _name, Date _value, String _format) {
		if (_value == null) {
			this.addNullAttribute(_name);
		} else if (_format == null) {
			this.attributes.addAttribute(_name, DateFormatUtils.ISO_DATE_FORMAT.format(_value));
		} else  {
			this.attributes.addAttribute(_name, DateFormatUtils.format(_value, _format));
		}
	}

	public void addDateAttribute(String _name, Date _value) {
		this.addDateAttribute(_name, _value, null);		
	}
	
	public void addTimeAttribute(String _name, Date _value, String _format) {
		if (_value == null) {
			this.addNullAttribute(_name);
		} else if (_format == null) {
			this.attributes.addAttribute(_name, DateFormatUtils.ISO_TIME_FORMAT.format(_value));
		} else  {
			this.attributes.addAttribute(_name, DateFormatUtils.format(_value, _format));
		}
	}

	public void addTimeAttribute(String _name, Date _value) {
		this.addTimeAttribute(_name, _value, null);		
	}
	
	public void addDateTimeAttribute(String _name, Date _value, String _format) {
		if (_value == null) {
			this.addNullAttribute(_name);
		} else if (_format == null) {
			this.attributes.addAttribute(_name, DateFormatUtils.ISO_DATETIME_FORMAT.format(_value));
		} else  {
			this.attributes.addAttribute(_name, DateFormatUtils.format(_value, _format));
		}
	}

	public void addDateTimeAttribute(String _name, Date _value) {
		this.addDateTimeAttribute(_name, _value, null);		
	}
	
	public void addDoubleAttribute(String _name, double _value, String _format) {
		DecimalFormat formatter = (DecimalFormat) decimalFormatter.get();
		if (_format == null) {
			formatter.applyPattern(DEFAULT_NUMBER_FORMAT);
		} else {
			formatter.applyPattern(_format);
		}
		this.attributes.addAttribute(_name, formatter.format(_value));
	}

	public void addDoubleAttribute(String _name, double _value) {
		this.addDoubleAttribute(_name, _value, null);
	}
	
	public void addLongAttribute(String _name, long _value, String _format) {
		DecimalFormat formatter = (DecimalFormat) decimalFormatter.get();
		if (_format == null) {
			formatter.applyPattern(DEFAULT_NUMBER_FORMAT);
		} else {
			formatter.applyPattern(_format);
		}
		this.attributes.addAttribute(_name, formatter.format(_value));
	}
	
	public void addLongAttribute(String _name, long _value) {
		this.addLongAttribute(_name, _value,null);
	}
	
	public void addNullAttribute(String _name) {
		this.attributes.addAttribute(_name, "");
	}
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() +  
		" : Description=[" + this.getDescription() + "]"+
		".Rule=[" + (this.getRelatedRule() == null ? "-" : this.getRelatedRule().getUid()) + "]" + 
		".Attrs=" + this.getAttributes()
		;
	}
		
}
