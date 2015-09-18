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
 * @author framos
 * @version $Id$
 */
public class UsageType extends BaseEnum {

	
	public static final UsageType VOICE = new UsageType(0, "VOICE");
	public static final UsageType DATA = new UsageType(1, "DATA");
	public static final UsageType EVENT = new UsageType(2, "EVENT");

	
	public UsageType() { this(VOICE.getSequenceId(), VOICE.getValue()); }
	private UsageType(int _id, String _value) { super(_id, _value); }
	
	public static final UsageType getUsageType(int _id) {
		switch (_id) {
			case 0 : return VOICE;
			case 1 : return DATA;
			case 2 : return EVENT;
		}
		throw new IllegalArgumentException("No usage type with id " + _id + " defined.");
	}	
}
