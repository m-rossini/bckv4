/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 21/10/2006
 */
package br.com.auster.billcheckout.model;

import br.com.auster.om.reference.CustomizableEntity;

public class FiscalCode extends CustomizableEntity {

	public static final int MAX_FISCALCODE_SIZE = 10;
	
	public static final int MAX_DESCRIPTION_SIZE = 128;

	private String fiscalCode;

	private String codeDescription;

	public FiscalCode() {
		super(0);
	}
	
	public FiscalCode(long uid) {
		super(uid);
	}

	public String getFiscalCode() {
		return fiscalCode;
	}

	public void setFiscalCode(String fiscalCode) {

		if (fiscalCode != null) {
			this.fiscalCode = fiscalCode.substring(0, Math.min(fiscalCode.length(), MAX_FISCALCODE_SIZE));
		} else {
			this.fiscalCode = fiscalCode;
		}
	}

	public String getCodeDescription() {
		return codeDescription;
	}

	public void setCodeDescription(String description) {

		if (description != null) {
			this.codeDescription = description.substring(0, Math.min(description.length(), MAX_DESCRIPTION_SIZE));
		} else {
			this.codeDescription = description;
		}
	}

	public boolean equals(Object obj) {
		FiscalCode anotherObj = (FiscalCode) obj;
		boolean eq = (this.fiscalCode == null ? (anotherObj.fiscalCode == null) : (this.fiscalCode.equals(anotherObj.fiscalCode)));

		return eq;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getFiscalCode() == null? 0 : 
			                        this.getFiscalCode().hashCode());

		return result;
	}	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() +  
		" : FiscalCode=[" + this.getFiscalCode() + 
		"].CodeDescription=[" + this.getCodeDescription() + 
		"]";
	}

	public Key getKey() {
		return new Key(this.getFiscalCode());
	}
	
	public static class Key {
		
		private String fiscalCode;
		
		public Key(String fiscalCode) {
			this.fiscalCode = fiscalCode;
		}
		
		public boolean equals(Object obj) {
			Key k = (Key) obj;
			boolean eq = (this.fiscalCode == null ? (k.fiscalCode == null) : (this.fiscalCode.equals(k.fiscalCode)));

			return eq;
		}
		
		public int hashCode() {
			int fiscalCode = (this.fiscalCode != null ? this.fiscalCode.hashCode() : 0);

			return fiscalCode;
		}
	}

	public static CustomizableEntity mergeNonKeyAttributes(FiscalCode fiscalCode1, FiscalCode fiscalCode2) {
		
		FiscalCode finalObj = (FiscalCode) CustomizableEntity.mergeNonKeyAttributes(fiscalCode1, fiscalCode2);
		FiscalCode otherObj = (finalObj == fiscalCode1 ? fiscalCode2 : fiscalCode1);
		if ((finalObj == null) || (otherObj == null)) {
			return finalObj;
		} else if (finalObj == fiscalCode2) {
			otherObj = fiscalCode1;
		}
		finalObj.setCodeDescription((finalObj.getCodeDescription() == null ? otherObj.getCodeDescription() : 
			                                               finalObj.getCodeDescription()));
		return finalObj;
	}	

}
