/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
 * Created on 12/03/2007
 */
package br.com.auster.billcheckout.thresholds;



/**
 * The possible taxes a NF, or any other element from an Invoice, can be charged. 
 *  - ICMS_TAX ICMS tax
 *  - ISS_TAX ISS tax
 *  - PIS_TAX PIS tax
 *  - COFINS_TAX COFINS tax
 *  - FUST_TAX FUST tax
 *  - FUNTTEL_TAX FUNTTEL tax
 *  - OTHER_LOCAL_TAXES any other taxes charged by the city / county
 *  - OTHER_STATE_TAXES any other taxes charged by the state
 *  - OTHER_FEDERAL_TAXES any other taxes charged nationwide
 *  - ANY allows this threshold to be checked regardless of the tax
 * 
 * @author framos
 * @version $Id$
 */
public class TaxType extends BaseEnum {

	
	public static final TaxType ICMS_TAX = new TaxType(0, "ICMS_TAX");
	public static final TaxType ISS_TAX = new TaxType(1, "ISS_TAX");
	public static final TaxType PIS_TAX = new TaxType(2, "PIS_TAX");
	public static final TaxType COFINS_TAX = new TaxType(3, "COFINS_TAX");
	public static final TaxType FUST_TAX = new TaxType(4, "FUST_TAX");
	public static final TaxType FUNTTEL_TAX = new TaxType(5, "FUNTTEL_TAX");
	public static final TaxType OTHER_LOCAL_TAXES = new TaxType(6, "OTHER_LOCAL_TAXES");
	public static final TaxType OTHER_STATE_TAXES = new TaxType(7, "OTHER_STATE_TAXES");
	public static final TaxType OTHER_FEDERAL_TAXES = new TaxType(8, "OTHER_FEDERAL_TAXES");
	public static final TaxType ANY = new TaxType(9, "ANY");

	
	public TaxType() { this(ANY.getSequenceId(), ANY.getValue()); }
	private TaxType(int _id, String _value) { super(_id, _value); }
	
	public static final TaxType getTaxType(int _id) {
		switch (_id) {
			case 0 : return ICMS_TAX;
			case 1 : return ISS_TAX;
			case 2 : return PIS_TAX;
			case 3 : return COFINS_TAX;
			case 4 : return FUST_TAX;
			case 5 : return FUNTTEL_TAX;
			case 6 : return OTHER_LOCAL_TAXES;
			case 7 : return OTHER_STATE_TAXES;
			case 8 : return OTHER_FEDERAL_TAXES;
			case 9 : return ANY;
		}
		throw new IllegalArgumentException("No tax with id " + _id + " defined.");
	}	
	
}
