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
 * Created on 19/01/2007
 */
package br.com.auster.billcheckout.rules.duplicity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Holds metadata info that refers to an unique usage file.
 * 
 * <p>
 * Extensions of this class must override the
 * {@link Object#equals(Object)} and {@link Object#hashCode()} methods.
 * </p>
 * <p>
 * Finally, all metadata fields must be made <code>private</code> and
 * <code>final</code> with getter methods to access them, but no setters.
 * </p>
 * 
 * @author rbarone
 * @version $Id$
 */
public class UsageFileMetadata {
  
  
  private static final int DATE_MIN_VALUE = 19700000;
  
  
  private final String accountId;
  private final String subscriptionId;
  private final int date;

  private int hashcode;
  
  
  public UsageFileMetadata(String accountId, String subscriptionId, int date) {
    if (accountId == null || accountId.trim().length() == 0) {
      throw new IllegalArgumentException("DefaultUsageFileMetadata's Account ID cannot be null or empty.");
    }
    this.accountId = accountId.trim().replace(' ', '_');
    if (subscriptionId == null || subscriptionId.trim().length() == 0) {
      throw new IllegalArgumentException("DefaultUsageFileMetadata's subscription ID cannot be null or empty.");
    }
    this.subscriptionId = subscriptionId.trim().replace(' ', '_');
    if (date < DATE_MIN_VALUE) {
      throw new IllegalArgumentException("DefaultUsageFileMetadata's date must be greater than "
                                         + DATE_MIN_VALUE);
    }
    this.date = date;
  }
  
  
  public final String getAccountId() {
    return this.accountId;
  }
  
  public final String getSubscriptionId() {
    return this.subscriptionId;
  }

  /**
   * Returns the date of this usage file as an integer with 8 digits, where:
   * <ul>
   * <li><b>digits 1 to 4:</b> year (yyyy) - from 1970 to n</li>
   * <li><b>digits 5 to 6:</b> month (MM) - from 1 to 12</li>
   * <li><b>digits 7 to 8:</b> day of the month - from 1 to 31 (dd)</li>
   * </ul>
   * 
   * <p>
   * For example, <code>20061230</code> would mean:
   * </p>
   * 
   * <pre>
   *     Year  = 2006
   *     Month = 12 (December)
   *     Day   = 30
   * </pre>
   * 
   * @return the date of this usage file.
   */
  public final int getDate() {
    return this.date;
  }
  
  @Override
  public String toString() {
    ToStringBuilder eb = new ToStringBuilder(this);
    eb.append("accountId", this.getAccountId());
    eb.append("subscriptionId", this.getSubscriptionId());
    eb.append("service", this.getDate());
    return eb.toString();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null 
        || obj instanceof UsageFileMetadata == false 
        || this.hashCode() != obj.hashCode()) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    UsageFileMetadata that = (UsageFileMetadata) obj;
    EqualsBuilder eb = new EqualsBuilder();
    eb.append(this.getAccountId(), that.getAccountId());
    eb.append(this.getSubscriptionId(), that.getSubscriptionId());
    eb.append(this.getDate(), that.getDate());
    return eb.isEquals();
  }
  
  protected static HashCodeBuilder generateHashcodeBuilder(UsageFileMetadata obj) {
    HashCodeBuilder hc = new HashCodeBuilder();
    hc.append(obj.getAccountId());
    hc.append(obj.getSubscriptionId());
    hc.append(obj.getDate());
    return new HashCodeBuilder();
  }
  
  public static int generateHashcode(UsageFileMetadata obj) {
    return generateHashcodeBuilder(obj).toHashCode();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    if (this.hashcode == 0) {
      this.hashcode = generateHashcode(this);
    }
    return this.hashcode;
  }
  
  
  /**
   * Parses a date, represented by an integer, to an array where:
   * <ul>
   * <li>index 0 = year</li>
   * <li>index 1 = month</li>
   * <li>index 2 = day</li>
   * </ul>
   * 
   * @param date
   *          the integer that represents a <code>date</code> value, as
   *          defined by {@link #getDate()}.
   * @return an array containing the year, month and day represented by
   *         the specified date value.
   */
  public static final int[] parseDate(int date) {
    final int[] f = new int[3];
    f[0] = date / 10000;
    f[1] = (date / 100) - (f[0] * 100);
    f[2] = date % 100;
    return f;
  }
  
}
