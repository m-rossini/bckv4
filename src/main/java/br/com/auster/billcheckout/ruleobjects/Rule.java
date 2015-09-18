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
package br.com.auster.billcheckout.ruleobjects;

import br.com.auster.om.reference.CustomizableEntity;

/**
 * @author framos
 * @version $Id: Rule.java 503 2006-11-14 20:42:28Z framos $
 */
public class Rule extends CustomizableEntity {

	
	
	private static final int	MAX_SHORTNAME_SIZE	= 32;
	// ---------------------------
	// Instance variables
	// ---------------------------

	private String code;
	private String shortName;
	private String description;
	private RuleType type;

	
	
	// ---------------------------
	// Contructors
	// ---------------------------
	
	public Rule() {
		this(0);
	}
	
    /**
     * This version will create a new instance that, due to the Hibernate mapping, will
     *   be treated as <strong>already registered</strong> in the database.
     * <p>
     * This means that is <code>saveOrUpdate()</code> is called, then an update (and not 
     *   an insert) will be executed. So, use this constructor wisely. 
     */
	public Rule(long _uid) {
		super(_uid);
	}
	
	public Rule(String _code, String _name) {
		this(0);
		this.setCode(_code);
		this.setShortName(_name);
	}	
	
	
	
	// ---------------------------
	// Public methods
	// ---------------------------
		
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		if (shortName != null) {
			this.shortName = shortName.substring(0, Math.min(shortName.length(), MAX_SHORTNAME_SIZE));
		} else {
			this.shortName = shortName;
		}		
	}
	
	public RuleType getType() {
		return type;
	}
	public void setType(RuleType type) {
		this.type = type;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getCode() == null? 0 : 
			                        this.getCode().hashCode());
		result = result*37 + (this.getShortName() == null? 0 : 
                                    this.getShortName().hashCode());
		return result;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + 
		" : Rule=[" + this.getCode() + "." + this.getShortName() + "]";
	}

	public Key getKey() {
		return new Key(this.getCode());
	}
	
	public static class Key {
		
		private String code;
		
		public Key(String _ruleCode) {
			this.code = _ruleCode;
		}
		
		public boolean equals(Object obj) {
			Key k = (Key) obj;
			return k.code.equals(this.code);
		}
		
		public int hashCode() {
			return this.code.hashCode();
		}
	}
	
	// ---------------------------
	// static methods
	// ---------------------------

	public static Rule mergeNonKeyAttributes(Rule _rule1, Rule _rule2) {
		
		Rule finalRule = (Rule) CustomizableEntity.mergeNonKeyAttributes(_rule1, _rule2);
		Rule otherRule = (finalRule == _rule1 ? _rule2 : _rule1);
		if ((finalRule == null) || (otherRule == null)) {
			return finalRule;
		}
		finalRule.setShortName((finalRule.getShortName() == null ? otherRule.getShortName() : 
																   finalRule.getShortName()));
		finalRule.setDescription((finalRule.getDescription() == null ? otherRule.getDescription() : 
																	   finalRule.getDescription()));
		finalRule.setType((finalRule.getType() == null ? otherRule.getType() : 
														 finalRule.getType()));
		return finalRule;
	}

}
