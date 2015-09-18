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

public class InvoiceAllowedCaptions extends CustomizableEntity {

	public static final int MAX_ALLOWED_CAPTION_SIZE = 64;

	private String allowedCaption;

	private InvoiceSection invoiceSection;

	public InvoiceAllowedCaptions(){
		super(0);
	}

	public InvoiceAllowedCaptions(long uid){
		super(uid);
	}

	public String getAllowedCaption() {
		return allowedCaption;
	}

	public void setAllowedCaption(String allowedCaption) {
		if(allowedCaption != null){
			this.allowedCaption = allowedCaption.substring(0, Math.min(allowedCaption.length(), MAX_ALLOWED_CAPTION_SIZE));
		}
		else{
			this.allowedCaption = allowedCaption;
		}
	}

	public InvoiceSection getInvoiceSection() {
		return invoiceSection;
	}

	public void setInvoiceSection(InvoiceSection invoiceSection) {
		this.invoiceSection = invoiceSection;
	}
	
	public void setInvoiceSectionUid(long uid){
		this.setInvoiceSection(new InvoiceSection(uid));
	}
	
	public long getInvoiceSectionUid(){
		return this.getInvoiceSection() != null ? this.getInvoiceSection().getUid() : 0;
	}
	
	/**
	 * @see java.lang.Object#equals()
	 */
	public boolean equals(Object obj) {
		InvoiceAllowedCaptions anotherObj = (InvoiceAllowedCaptions) obj;
		boolean eq = (this.invoiceSection == null ? (anotherObj.invoiceSection == null) : (this.invoiceSection.equals(anotherObj.invoiceSection)));		
		return eq;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getInvoiceSection() == null? 0 : this.getInvoiceSection().hashCode());		
		return result;
	}	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + " : InvoiceSection=[" + this.getInvoiceSection() + "]"; 
		
	}
}
