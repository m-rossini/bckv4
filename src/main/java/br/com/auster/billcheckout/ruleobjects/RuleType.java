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
 * @hibernate.class table="BCK_RULE_TYPE"
 * 
 * @author framos
 * @version $Id: RuleType.java 424 2006-08-28 15:00:22Z framos $
 */
public class RuleType extends CustomizableEntity {

	
	
	// ---------------------------
	// Instance variables
	// ---------------------------
	
	private String name;
	private String description;
	
	
	
	// ---------------------------
	// Constructors
	// ---------------------------

	public RuleType() {
		super();
	}
	
	public RuleType(long _uid) {
		super(_uid);
	}
	
	
	
	// ---------------------------
	// Public methods
	// ---------------------------
	

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getName() == null? 0 : 
            					  this.getName().hashCode());
		return result;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + " : Type=[" + this.getName() + "." + this.getDescription() + "]";
	}
}
