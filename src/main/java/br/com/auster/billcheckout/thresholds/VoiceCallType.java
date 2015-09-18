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
 * Only considered when usage detail is voice. This enumation defines the possible types a voice call
 *   can be.
 *  - VC1 - local voice calls 
 *  - VC2 - non-local, region-wide voice calls
 *  - VC3 - non-local, non-region, nation-wide voice calls
 *  - INTL - international calls originated nation-wide
 *  - ROI - international calls originated outside the national territory
 *  - AD1 - additional fee payed when originating VC2 calls in ROAM
 *  - AD2 - additional fee payed when originating VC3 calls in ROAM
 *  - ADi - additional fee payed when originating international calls in ROAM
 *  - DSL2 - additional fee payed when receiving VC2 calls in ROAM
 *  - DSL3 - additional fee payed when receiving VC3 calls in ROAM
 *  - DLSi - additional fee payed when receiving international calls in ROAM
 *  - ANY allows this threshold to be checked regardless of the type of voice call
 *  
 * @author framos
 * @version $Id$
 */
public class VoiceCallType extends BaseEnum {

	
	public static final VoiceCallType VC1 = new VoiceCallType(0, "VC1");
	public static final VoiceCallType VC2 = new VoiceCallType(1, "VC2");
	public static final VoiceCallType VC3 = new VoiceCallType(2, "VC3");
	public static final VoiceCallType INTL = new VoiceCallType(3, "INTL");
	public static final VoiceCallType ROI = new VoiceCallType(4, "ROI");
	public static final VoiceCallType AD1 = new VoiceCallType(5, "AD1");
	public static final VoiceCallType AD2 = new VoiceCallType(6, "AD2");
	public static final VoiceCallType ADi = new VoiceCallType(7, "ADi");
	public static final VoiceCallType DSL2 = new VoiceCallType(8, "DSL2");
	public static final VoiceCallType DSL3 = new VoiceCallType(9, "DSL3");
	public static final VoiceCallType DSLi = new VoiceCallType(10, "DSLi");
	public static final VoiceCallType ANY = new VoiceCallType(11, "ANY");
	
	
	
	public VoiceCallType() { this(ANY.getSequenceId(), ANY.getValue()); }
	private VoiceCallType(int _id, String _value) {  super(_id, _value); }	
	
	public static final VoiceCallType getVoiceCallType(int _id) {
		switch (_id) {
			case 0 : return VC1;
			case 1 : return VC2;
			case 2 : return VC3;
			case 3 : return INTL;
			case 4 : return ROI;
			case 5 : return AD1;
			case 6 : return AD2;
			case 7 : return ADi;
			case 8 : return DSL2;
			case 9 : return DSL3;
			case 10: return DSLi;
			case 11: return ANY;
		}
		throw new IllegalArgumentException("No voice call type with id " + _id + " defined.");
	}
}
