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
 * The possible destionations a usage detail can contact. 
 *  - NET if the destination is in the same network
 *  - MOBILE if the destination is a mobile phone, in another network
 *  - NON_MOBILE any other destionations, outside the home network, except for mobiles
 *  - ANY allows this threshold to be checked regardless of the destination type
 * 
 * @author framos
 * @version $Id$
 */
public class DestinationType extends BaseEnum {

	
	public static final DestinationType NET = new DestinationType(0, "NET");
	public static final DestinationType MOBILE = new DestinationType(1, "MOBILE");
	public static final DestinationType NON_MOBILE = new DestinationType(2, "NON_MOBILE");
	public static final DestinationType ANY = new DestinationType(3, "ANY");

	
	public DestinationType() { this(ANY.getSequenceId(), ANY.getValue()); }
	private DestinationType(int _id, String _value) { super(_id, _value); }
	
	public static final DestinationType getDestinationType(int _id) {
		switch (_id) {
			case 0 : return NET;
			case 1 : return MOBILE;
			case 2 : return NON_MOBILE;
			case 3 : return ANY;
		}
		throw new IllegalArgumentException("No destination type with id " + _id + " defined.");
	}	
	
}
