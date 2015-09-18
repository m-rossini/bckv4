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
 * Created on 08/01/2007
 */
package br.com.auster.billcheckout.rules.duplicity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents an usage record with the following fields:
 * 
 * <ul>
 * <li>"partialSequence": free field (not used, but is verified for duplicity detection)</li>
 * <li>"startSecondsWithinDay": number of seconds that represents the start time of the usage
 * in that date, from 0 to 3599</li>
 * <li>"duration": duration of the usage (unit not enforced/checked)</li>
 * <li>"fromNumber": Originating number (callee) - can be {@code null}/empty</li>
 * <li>"toNumber": Destination number (called) - can be {@code null}/empty</li>
 * <li>"service": Service code - can be {@code null}/empty</li>
 * <li>"hashCode": Integer value built using all the above fields.</li>
 * </ul>
 *
 * @author rbarone
 * @version $Id$
 */
public class UsageRecord {
  
  private int hashcode;
  private short partialSequence;
  private int startSecondsWithinDay = Integer.MIN_VALUE;
  private long duration;
  private String fromNumber;
  private String toNumber;
  private String service;
  
  public UsageRecord() {
  }
  
  
  protected void setHashCode(int hashcode) {
    this.hashcode = hashcode;
  }
  
  protected void setPartialSequence(short _sequence) {
    if (_sequence < 0) {
      throw new IllegalArgumentException("Invalid partial sequence: must be a positive number." + 
                                         "Received: " + _sequence);
    }
    this.partialSequence = _sequence;
  }
  
  /**
   * Sets the number of seconds ellapsed within the defined
   * {@link #setStartDateAndHour(int) startDateAndHour}.
   * 
   * @see #getStartSecondsWithinDay()
   * @param _startSecondsWithinDay
   *          the number os seconds ellapsed within this
   *          <code>startDateAndDay</code>, from 0 to 86399.
   */
  public void setStartSecondsWithinDay(int _startSecondsWithinDay) {
    this.startSecondsWithinDay = _startSecondsWithinDay;
  }
  
  public void setUsageDuration(long _duration) {
    this.duration = _duration;
  }
  
  /**
   * Sets the callee number for this usage. Leading/Trailing spaces may be
   * discarded.
   * 
   * <p>
   * <code>null</code> values won't be accepted - provide an empty string
   * instead.
   * </p>
   *  
   * @param _fromNumber
   *          the callee number for this usage - can't be null.
   */
  public void setFromNumber(String _fromNumber) {
    if (_fromNumber == null) {
      this.fromNumber = "";
    } else {
      this.fromNumber = _fromNumber.trim();
    }
  }
  
  /**
   * Sets the called number for this usage. Leading/Trailing spaces may be
   * discarded.
   * 
   * <p>
   * <code>null</code> values won't be accepted - provide an empty string
   * instead.
   * </p>
   * 
   * @param _fromNumber
   *          the called number for this usage - can't be null.
   */
  public void setToNumber(String _toNumber) {
    if (_toNumber == null) {
      this.toNumber = "";
    } else {
      this.toNumber = _toNumber.trim();
    }
  }
  
  /**
   * Sets the service for this usage. Leading/Trailing spaces may be discarded.
   * 
   * <p>
   * <code>null</code> values won't be accepted - provide an empty string
   * instead.
   * </p>
   * 
   * @param _fromNumber
   *          the service for this usage - can't be null.
   */
  public void setService(String _service) {
    if (_service == null) {
      this.service = "";
    } else {
      this.service = _service.trim();
    }
  }
  
  
  public int getHashCode() {
    if (this.hashcode == 0) {
      setHashCode( generateHashcode(this) );
    }
    return this.hashcode;
  }
  
  /**
   * Free field, but can be used to identify a partial usage, as explained bellow.
   * 
   * <p>
   * 0 means this is an entire usage. Any other values means this is a partial
   * usage and the number represents the part sequence, from 1 to
   * {@code Short.MAX_VALUE - 1}. The last part is always
   * <code>Short.MAX_VALUE</code>.
   * </p>
   * 
   * <p>
   * For example, if the usage has been broken into 3 separate parts, first part
   * will be 1, next will be 2 and the last will be <code>Short.MAX_VALUE</code>.
   * <p>
   * 
   * @return
   */
  public short getPartialSequence() {
    return this.partialSequence;
  }
  
  /**
   * This will give the number of seconds ellapsed within the day that the
   * usage started.
   * 
   * <p>
   * For example, if a call started on 2006-12-30 at 23:10:45, the return value
   * will be:
   * <p>
   * <code>(23 * 3600) + (10 * 60) + 45 = <b>83505 seconds</b></code>
   * 
   * @return the date/time representing the instant when this usage started,
   *         from 0 to 86399.
   */
  public int getStartSecondsWithinDay() {
    return this.startSecondsWithinDay;
  }

  public long getUsageDuration() {
    return duration;
  }

  public String getFromNumber() {
    return fromNumber;
  }

  public String getToNumber() {
    return toNumber;
  }
  
  public String getService() {
    return service;
  }
  
  protected static HashCodeBuilder generateHashcodeBuilder(UsageRecord usage) {
    HashCodeBuilder hc = new HashCodeBuilder();
    hc.append(usage.getPartialSequence());
    hc.append(usage.getStartSecondsWithinDay());
    hc.append(usage.getUsageDuration());
    hc.append(usage.getFromNumber());
    hc.append(usage.getToNumber());
    hc.append(usage.getService());
    return new HashCodeBuilder();
  }
  
  protected static int generateHashcode(UsageRecord usage) {
    return generateHashcodeBuilder(usage).toHashCode();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return getHashCode();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || obj instanceof UsageRecord == false || this.hashCode() != obj.hashCode()) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    UsageRecord that = (UsageRecord) obj;
    EqualsBuilder eb = new EqualsBuilder();
    eb.append(this.getPartialSequence(), that.getPartialSequence());
    eb.append(this.getStartSecondsWithinDay(), that.getStartSecondsWithinDay());
    eb.append(this.getUsageDuration(), that.getUsageDuration());
    eb.append(this.getFromNumber(), that.getFromNumber());
    eb.append(this.getToNumber(), that.getToNumber());
    eb.append(this.getService(), that.getService());
    return eb.isEquals();
  }
  
  @Override
  public String toString() {
    ToStringBuilder eb = new ToStringBuilder(this);
    eb.append("partialSequence", this.getPartialSequence());
    eb.append("startSecondsWithinDay", this.getStartSecondsWithinDay());
    eb.append("duration", this.getUsageDuration());
    eb.append("fromNumber", this.getFromNumber());
    eb.append("toNumber", this.getToNumber());
    eb.append("service", this.getService());
    return eb.toString();
  }
}
