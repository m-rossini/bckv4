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

import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.om.reference.CustomizableEntity;

public class InvoiceSection extends CustomizableEntity {

	public static final int MAX_SHORT_NAME_SIZE = 10;

	public static final int MAX_SECTION_DESCRIPTION_SIZE = 64;

	private String sectionShortName;

	private String sectionDescription;

	private CarrierDimension carrierDimension;

	public InvoiceSection(){
		super(0);
	}

	public InvoiceSection(long uid){
		super(uid);
	}

	public CarrierDimension getCarrierDimension() {
		return carrierDimension;
	}

	public void setCarrierDimension(CarrierDimension carrierDimension) {		
		this.carrierDimension = carrierDimension;
	}

	public String getSectionDescription() {
		return sectionDescription;
	}

	public void setSectionDescription(String sectionDescription) {
		if(sectionDescription != null){
			this.sectionDescription = sectionDescription.substring(0, Math.min(sectionDescription.length(), MAX_SECTION_DESCRIPTION_SIZE));
		}
		else{
			this.sectionDescription = sectionDescription;
		}
	}

	public String getSectionShortName() {
		return sectionShortName;
	}

	public void setSectionShortName(String sectionShortName) {
		if(sectionShortName != null){
			this.sectionShortName = sectionShortName.substring(0, Math.min(sectionShortName.length(), MAX_SHORT_NAME_SIZE));
		}
		else{
			this.sectionShortName = sectionShortName;
		}
	}
	
	public void setCarrierDimensionUid(long carrierDMUid){
		this.setCarrierDimension(new CarrierDimension(carrierDMUid));
	}
	
	public long getCarrierDimensionUid(){
		return this.getCarrierDimension() != null ? this.getCarrierDimension().getUid() : 0;
	}
	
	/**
	 * @see java.lang.Object#equals()
	 */
	public boolean equals(Object obj) {
		InvoiceSection anotherObj = (InvoiceSection) obj;
		boolean eq = (this.carrierDimension == null ? (anotherObj.carrierDimension == null) : (this.carrierDimension.equals(anotherObj.carrierDimension)));		
		return eq;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getCarrierDimension() == null? 0 : this.getCarrierDimension().hashCode());		
		return result;
	}	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() + " : CarrierDimension=[" + this.getCarrierDimension() + "]"; 
		
	}
}
