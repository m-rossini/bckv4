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
 * Denotes the different periods of time, during the day/week, a usage detail can be checked for.
 *  - WEEK for any week days (monday - friday)
 *  - WEEKEND for weekends (saturday and sunday) 
 *  - DAY for business hours, or <i>peek</i> periods
 *  - NIGHT for non-business hours, or <i>off-peek</i> periods
 *  - ANY allows this threshold to be checked regardless of the time of the day
 *  
 * @author framos
 * @version $Id$
 */
public class TimePeriod extends BaseEnum {

	public static final TimePeriod WEEK_DAY = new TimePeriod(0, "WEEK_DAY");
	public static final TimePeriod WEEK_NIGHT = new TimePeriod(1, "WEEK_NIGHT");
	public static final TimePeriod WEEK_ANY = new TimePeriod(2, "WEEK_ANY");
	public static final TimePeriod WEEKEND_DAY = new TimePeriod(3, "WEEKEND_DAY");
	public static final TimePeriod WEEKEND_NIGHT = new TimePeriod(4, "WEEKEND_NIGHT");
	public static final TimePeriod WEEKEND_ANY = new TimePeriod(5, "WEEKEND_ANY");
	public static final TimePeriod ANY_DAY = new TimePeriod(6, "ANY_DAY");
	public static final TimePeriod ANY_NIGHT = new TimePeriod(7, "ANY_NIGHT");
	public static final TimePeriod ANY = new TimePeriod(8, "ANY");


	public TimePeriod() { this(ANY.getSequenceId(), ANY.getValue()); }
	private TimePeriod(int _id, String _value) { super(_id, _value); }
	
	public static final TimePeriod getTimePeriod(int _id) {
		switch (_id) {
			case 0 : return WEEK_DAY;
			case 1 : return WEEK_NIGHT;
			case 2 : return WEEK_ANY;
			case 3 : return WEEKEND_DAY;
			case 4 : return WEEKEND_NIGHT;
			case 5 : return WEEKEND_ANY;
			case 6 : return ANY_DAY;
			case 7 : return ANY_NIGHT;
			case 8 : return ANY;
		}
		throw new IllegalArgumentException("No time period with id " + _id + " defined.");
	}	
	
}
