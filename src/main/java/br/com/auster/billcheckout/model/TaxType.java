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

public class TaxType extends CustomizableEntity {

	public static final int MAX_CODE_SIZE = 10;
	
	public static final int MAX_NAME_SIZE = 64;

	private String taxCode;

	private String taxName;

	public TaxType() {
		super(0);
	}
	
	public TaxType(long uid) {
		super(uid);
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String code) {
		
		if (code != null) {
			this.taxCode = code.substring(0, Math.min(code.length(), MAX_CODE_SIZE));
		} else {
			this.taxCode = code;
		}
	}

	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(String name) {

		if (name != null) {
			this.taxName = name.substring(0, Math.min(name.length(), MAX_NAME_SIZE));
		} else {
			this.taxName = name;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() +  
		" : TaxCode=[" + this.getTaxCode() + 
		"].TaxName=[" + this.getTaxName() + 
		"]";
	}

	public Key getKey() {
		return new Key(this.getTaxCode());
	}
	
	public static class Key {
		
		private String code;
		
		public Key(String code) {
			this.code = code;
		}
		
		public boolean equals(Object obj) {
			Key k = (Key) obj;
			boolean eq = (this.code == null ? (k.code == null) : (this.code.equals(k.code)));

			return eq;
		}
		
		public int hashCode() {
			int code = (this.code != null ? this.code.hashCode() : 0);

			return code;
		}
	}

	public static CustomizableEntity mergeNonKeyAttributes(TaxType taxType1, TaxType taxType2) {
		
		TaxType finalObj = (TaxType) CustomizableEntity.mergeNonKeyAttributes(taxType1, taxType2);
		TaxType otherObj = (finalObj == taxType1 ? taxType2 : taxType1);
		if ((finalObj == null) || (otherObj == null)) {
			return finalObj;
		} else if (finalObj == taxType2) {
			otherObj = taxType1;
		}
		finalObj.setTaxName((finalObj.getTaxName() == null ? otherObj.getTaxName() : 
			                                               finalObj.getTaxName()));
		return finalObj;
	}	

}
