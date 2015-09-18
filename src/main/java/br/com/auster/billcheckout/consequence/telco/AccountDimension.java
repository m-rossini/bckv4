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

import br.com.auster.om.reference.CustomizableEntity;

/**
 * @author framos
 * @version $Id$
 */
public class AccountDimension extends CustomizableEntity {

	
	
	// ---------------------------
	// Instance variables
	// ---------------------------
	
	private String type;
	private String holding;
	private String number;
	
	private Key key;

	
	
	// ---------------------------
	// Constructors
	// ---------------------------
	
	public AccountDimension() {
		this(0);
	}
	
    /**
     * This version will create a new instance that, due to the Hibernate mapping, will
     *   be treated as <strong>already registered</strong> in the database.
     * <p>
     * This means that is <code>saveOrUpdate()</code> is called, then an update (and not 
     *   an insert) will be executed. So, use this constructor wisely. 
     */
	public AccountDimension(long _uid) {
		super(_uid);
	}
	
	public AccountDimension(String _type, String _holding, String _number) {
		this(0);
		this.setAccountType(_type);
		this.setAccountNumber(_number);
		this.setHoldingNumber(_holding);
	}
	
	
	
	// ---------------------------
	// Public methods
	// ---------------------------

	public void setAccountType(String _type) {
		this.type = _type;
	}
	public String getAccountType() {
		return this.type;
	}

	public void setHoldingNumber(String _number) {
		this.holding = _number;
	}
	public String getHoldingNumber() {
		return this.holding;
	}
	
	public void setAccountNumber(String _number) {
		this.number = _number;
	}
	public String getAccountNumber() {
		return this.number;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getAccountNumber() == null? 0 : 
			                       this.getAccountNumber().hashCode());
		return result;
	}	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() +  
		" : AccNo=[" + this.getAccountNumber() + 
		"].AccType=[" + this.getAccountType() +
		"].AccHolding=[" + this.getHoldingNumber() +"]";
	}

	public Key getKey() {
		if (this.key == null) {
			this.key = new Key(this.getAccountNumber());
		}
		return this.key;
	}
	
	public static class Key {
		
		private String accountNumber;
		
		public Key(String _accountNumber) {
			this.accountNumber = _accountNumber;
		}
		
		public boolean equals(Object obj) {
			Key k = (Key) obj;
			return k.accountNumber.equals(this.accountNumber);
		}
		
		public int hashCode() {
			return this.accountNumber.hashCode();
		}
	}
	

	
	
	// ---------------------------
	// static methods
	// ---------------------------

	public static CustomizableEntity mergeNonKeyAttributes(AccountDimension _acc1, AccountDimension _acc2) {
		
		AccountDimension finalObj = (AccountDimension) CustomizableEntity.mergeNonKeyAttributes(_acc1, _acc2);
		AccountDimension otherObj = (finalObj == _acc1 ? _acc2 : _acc1);
		if ((finalObj == null) || (otherObj == null)) {
			return finalObj;
		}
		finalObj.setHoldingNumber((finalObj.getHoldingNumber() == null ? otherObj.getHoldingNumber() : 
			                                                             finalObj.getHoldingNumber()));
		return finalObj;
	}
	
}
