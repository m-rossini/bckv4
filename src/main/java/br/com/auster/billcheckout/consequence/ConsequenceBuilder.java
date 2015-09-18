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
 * Created on 23/08/2006
 */
package br.com.auster.billcheckout.consequence;

import org.w3c.dom.Element;

import br.com.auster.billcheckout.consequence.ConsequenceAttributeList.AttributeType;
import br.com.auster.billcheckout.consequence.telco.TelcoDimensionsFacade;
import br.com.auster.billcheckout.ruleobjects.Rule;
import br.com.auster.common.data.runtime.MultiTypeCache;
import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class helps creating new instances of <code>Consequence</code>. It also
 * 	interfaces with facades to load information from databases. 
 * <p>
 * All instances of <code>Consequence</code> created here are treated as unsaved 
 *   by the Hibernate mapping. To reference a already registered consequence the
 *   <code>uid</code> attribute must be set after executing the {@link #getConsequence()}
 *   method.
 *  
 * @author framos
 * @version $Id$
 *
 */
public abstract class ConsequenceBuilder {

	

	// ---------------------------
	// Class constants
	// ---------------------------
	
	public static final String CONFIGURATION_FACADE_ELEMENT = "facade";
	public static final String CONFIGURATION_FACADE_CLASS = "class";
	
	private static final I18n i18n = I18n.getInstance(ConsequenceBuilder.class);

	
	
	// ---------------------------
	// Instance variables
	// ---------------------------

	protected Consequence newConsequence;
	protected ConsequenceDimensionsFacade dimFacade;
	protected boolean lenient;
	protected MultiTypeCache cache;
	

	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public ConsequenceBuilder() {
		this.lenient = false;
		this.cache = new MultiTypeCache();
		// TODO 
		this.cache.initCache(Rule.class, 250);
	}
	
	public ConsequenceBuilder(ConsequenceDimensionsFacade _facade) {
		this();
		this.dimFacade = _facade;
	}
	
	
	
	// ---------------------------
	// Public methods
	// ---------------------------


	public void configure(Element _configuration) throws InstantiationException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException {
		Element config = DOMUtils.getElement(_configuration, CONFIGURATION_FACADE_ELEMENT, false);
		if (config != null) {
			Class klass = Class.forName(DOMUtils.getAttribute(config, CONFIGURATION_FACADE_CLASS, true));
			this.dimFacade = (TelcoDimensionsFacade)klass.newInstance();
			this.dimFacade.configure(config);
		}
	}
	
	public ConsequenceDimensionsFacade getFacade() {
		return this.dimFacade;
	}
	
	/**
	 * Resets the in-creation consequence
	 */
	public abstract void reset();

	/**
	 * Returns the consequence created if all the mandatory attributes
	 *   are set. If not, an <code>IllegalStateException</code> is raised.
	 */
	public Consequence getConsequence() {
		if (isConsequenceReady()) {
			Consequence tmp = this.newConsequence;
			this.reset();
			return tmp;
		}
		throw new IllegalStateException(i18n.getString("consequence.notReady"));
	}
	
	/**
	 * Identifies if the consequence being created is ok to be returned
	 *   or not.
	 */
	public boolean isConsequenceReady() {
		return ((this.newConsequence != null) &&
				(this.newConsequence.getRelatedRule() != null));
	}
	
	/***
	 * Returns the indicator of lenient consequence builder.
	 * The default is false.
	 * When it is false, the concrete class extending this one has the opportunity to
	 * make tight check on other attributes (Like dimensions for example - see
	 * TelcoConsequenceBuilder) in order to guarantee all is properly set.
	 * When it is true, the concrete class has the opportunity to allows a more
	 * relaxed and flexible attributes setting.
	 * 
	 * Support for this is not mandatory, althrought is MUST be documented in 
	 * implementations fo this abstract class.
	 * 
	 * @return true or false, depending on the lenient attribute for this builder.
	 */
	public boolean isLenient() {
		return this.lenient;
	}
	
	public void setLenient(boolean lenient) {
		this.lenient = lenient;
	}
	
	/**
	 * Sets the rule this consequence was triggered by.
	 */
	public void setRule(String _ruleCode, String _ruleName) {
		Rule r = (Rule) this.cache.get(Rule.class, _ruleCode);
		if (r == null) {
			r = new Rule(_ruleCode, _ruleName);
		}
		this.setRule(r);
	}
	
	public void setRule(Rule _rule) {
		if (this.newConsequence == null) { this.reset(); }
		// if current rule dimension UID <= 0 and we have a facade instance, than try to load it.
		Rule tmp = null;
		if ((this.dimFacade != null) && (_rule.getUid() <= 0)) {
			tmp = this.dimFacade.getRule(_rule.getCode());
		}
		tmp = Rule.mergeNonKeyAttributes(tmp, _rule);
		this.cache.put(Rule.class, tmp.getCode(), tmp);
		this.newConsequence.setRelatedRule(tmp);
	}

	/**
	 * This method will internally try to guess which type is the <code>_value</code> parameter. The rules are:
	 *   <ul>
	 *     <li> if <code>instanceof</code> {@code java.util.Date} will set as {@value ConsequenceAttributeList.AttributeType.DATETIME} </li>
	 *     <li> if <code>instanceof</code> {@code java.lang.Number} will set as {@value ConsequenceAttributeList.AttributeType.DOUBLE} </li>
	 *     <li> else will set as {@value ConsequenceAttributeList.AttributeType.STRING} </li>
	 *   </ul>
	 *   </p>
	 *   <strong>NOTE:</strong> {@code java.lang.Number} is the superclass for all number wrapper classes. So, if you are using 
	 *   	auto-boxing, will work as expected.
	 *   </p>
	 *   If you need more precise definition of the <code>_value</code> type, then use {@link #addAttribute(String, Object, AttributeType)}.  
	 *   
	 * @param _name
	 * @param _value
	 */
	public void addAttribute(String _name, Object _value) {
		this.newConsequence.addAttribute(_name, _value);
	}
	
	/**
	 * Same as {@link #addAttribute(String, Object)}, but lets you define which type is the <code>_value</code> attribute. The
	 * 	format will be kept <code>null</code>, so that {@link Consequence} defaults will apply.
	 * 
	 * @param _name
	 * @param _value
	 * @param _type
	 */
	public void addAttribute(String _name, Object _value, AttributeType _type) {
		this.newConsequence.addAttribute(_name, _value, _type, null);
	}

	/**
	 * Same as {@link #addAttribute(String, Object)}, but lets you define which type is the <code>_value</code> attribute, and
	 * 	also the format you expect the value to be saved as.
	 * </p>
	 * The <code>_format</code> attribute must be equivalent to the type of the <code>_value</code> parameter, otherwise a
	 * 	parsing exception will be thrown. The correspondence is:
	 *   <ul>
	 *     <li> for date, time or datetime, use the syntax defined in {@code SimpleDateFormat} class </li>
	 *     <li> for numbers, use the syntax defined in {@code DecimaFormat} class </li>
	 *   </ul>
	 * 
	 * @param _name
	 * @param _value
	 * @param _type
	 * @param _format
	 */
	public void addAttribute(String _name, Object _value, AttributeType _type, String _format) {
		this.newConsequence.addAttribute(_name, _value, _type, _format);
	}

	/**
	 * Enables adding primitive doubles to the list of attributes. The format used will be the default, as defined in 
	 * 	{@link Consequence.DEFAULT_NUMBER_FORMAT}.
	 * 
	 * @param _name
	 * @param _value
	 */
	public void addAttribute(String _name, double _value) {
		this.addAttribute(_name, _value, null);
	}

	/**
	 * Enables adding primitive doubles to the list of attributes with a specific format pattern. Remember that the pattern
	 *   must be compliant with the specifications in {@code DecimalFormat}.
	 * 
	 * @param _name
	 * @param _value
	 * @param _format
	 */
	public void addAttribute(String _name, double _value, String _format) {
		this.addAttribute(_name, _value, _format);
	}

	/**
	 * Enables adding primitive longs to the list of attributes. The format used will be the default, as defined in 
	 * 	{@link Consequence.DEFAULT_NUMBER_FORMAT}.
	 * 
	 * @param _name
	 * @param _value
	 */
	public void addAttribute(String _name, long _value) {
		this.addAttribute(_name, _value, null);
	}

	/**
	 * Enables adding primitive longs to the list of attributes with a specific format pattern. Remember that the pattern
	 *   must be compliant with the specifications in {@code DecimalFormat}.
	 *   
	 * @param _name
	 * @param _value
	 * @param _format
	 */
	public void addAttribute(String _name, long _value, String _format) {
		this.addAttribute(_name, _value, _format);
	}
	
}
