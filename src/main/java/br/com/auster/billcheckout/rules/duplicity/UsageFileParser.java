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

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import br.com.auster.common.io.NIOUtils;
import br.com.auster.common.log.LogFactory;

/**
 * Responsible for the parsing of an {@link UsageFile}'s persisted file.
 * 
 * @author rbarone
 * @version $Id$
 */
public class UsageFileParser {
  
  private static final Logger log = LogFactory.getLogger(UsageFileParser.class);
  
  private static final int MAX_BUFFER_SIZE = 16 * 1024;
  
  // this is not final because we may never initialize it
  // in order to save memory
  private ByteBuffer buffer;
  
  private long bufferedFromPosition = -1L;
  
  private final WeakReference<UsageFile> usageFile;
  
  private long currentFileLength = -1L;
  private long currentFileLastModified = -1L;
  
  UsageFileParser(UsageFile ufile) {
    this.usageFile = new WeakReference<UsageFile>(ufile);
  }
  
  public boolean isFileModified(UsageFile ufile) {
    return  ufile.getFile().lastModified() != this.currentFileLastModified ||
            ufile.getFile().length() != this.currentFileLength;
  }
  
  /**
   * Parses the whole file represented by this <code>UsageFile</code>
   * instance. This behaves exactly the same as the following code snippet:
   * 
   * <pre>
   *    parseHeader();
   *    parseRecords();
   * </pre>
   * 
   * <p>
   * WARNING: all current fields will be lost and replaced by the parsed ones.
   * </p>
   * 
   * @throws UsageFileParseException
   *           If any error occurs during the parsing.
   */
  protected void parse() throws UsageFileParseException {
    clearBuffer();
    parseHeader(false);
    parseRecords(true);
  }
  
  /**
   * Parses the header of file represented by this <code>UsageFile</code>
   * instance. After executing this method, the following fields will be
   * populated:
   * 
   * <ul>
   * <li>{@link #getUsageMap() usageMap}</li>
   * <li>{@link #getUsageCount() usageCount}</li>
   * </ul>
   * 
   * Additionally, the file length will be checked based on the parsed <code>usageCount</code>
   * using the following rule:
   * 
   * <pre>
   *    fileLength == {@link #HEADER_SIZE} + (usageCount * {@link #RECORD_SIZE})
   * </pre>
   * 
   * <p>
   * WARNING: all current header fields will be lost and replaced by the parsed ones.
   * </p>
   * @param releaseLock 
   * 
   * @throws UsageFileParseException
   *           If any error occurs during the parsing.
   */
  public void parseHeader(boolean releaseLock) throws UsageFileParseException {
    UsageFile ufile = this.usageFile.get();
    if (!ufile.getFile().exists()) {
      throw new UsageFileParseException("File " + ufile.getFile().getPath() +
                                        " doesn't exist.");
    } else if (isFileModified(ufile)) {
      if (this.currentFileLength != -1L) {
        log.info("UsageFile " + ufile.getFile() + " has been modified: re-parsing header and clearing parsed usages.");
        ufile.setUsageRecords(null);
        this.bufferedFromPosition = -1L;
        this.buffer.clear();
      }
    } else if (ufile.getMetadata() != null) {
      // already parsed header
      return;
    }
    
    this.currentFileLastModified = -1L;
    this.currentFileLength = -1L;
    if (this.bufferedFromPosition != 0) {
      try {
        ufile.openFile();
        ufile.aquireReadFileLock();
        read(0, true);
      } catch (IOException e) {
        throw new UsageFileParseException(e);
      } finally {
        if (releaseLock) {
          try {
            ufile.releaseFileLock();
          } catch (IOException e) {
            log.error("Could not release lock on file " + ufile.getFile(), e);
          }
        }
      }
    }

    try {
      String accountId = getStringField(this.buffer, UsageFile.ACCOUNT_ID_POSITION,
                                        UsageFile.ACCOUNT_ID_FIELD_SIZE);
      String subscriptionId = getStringField(this.buffer, UsageFile.SUBSCRIPTION_ID_POSITION,
                                             UsageFile.SUBSCRIPTION_ID_FIELD_SIZE);
      int date = this.buffer.getInt(UsageFile.DATE_POSITION);
      ufile.setMetadata(new UsageFileMetadata(accountId, subscriptionId, date));

      this.buffer.position(UsageFile.USAGE_MAP_POSITION);
      this.buffer.get(ufile.getUsageMap());

      ufile.setUsageCount(this.buffer.getInt(UsageFile.USAGE_COUNT_POSITION));
      final long computedLength = UsageFile.HEADER_SIZE
                                  + (ufile.getUsageCount() * (long) UsageFile.RECORD_SIZE);
      if (ufile.getFile().length() != computedLength) {
        throw new UsageFileParseException("Invalid or corrupted usage file: check if usage"
                                          + " count field is synchronized with the number of"
                                          + " usage records present in the file. Computed size is "
                                          + computedLength + "; actual size is "
                                          + ufile.getFile().length());
      } else {
        this.currentFileLastModified = ufile.getFile().lastModified();
        this.currentFileLength = ufile.getFile().length();
      }
    } catch (Exception e) {
      throw new UsageFileParseException("Invalid or corrupted usage file " + 
                                        ufile.getFile().getName(), e);
    }
  }
  
  /**
   * Parses all records present in the usage file.
   * <p>
   * WARNING: all current usages will be lost and replaced by the parsed ones.
   * </p>
   * 
   * @param releaseLock
   *          if <code>true</code>, the read lock will be released before
   *          exit.
   * @throws UsageFileParseException
   */
  public void parseRecords(boolean releaseLock) throws UsageFileParseException {
    UsageFile ufile = this.usageFile.get();
    if (!ufile.getFile().exists()) {
      throw new UsageFileParseException("File " + ufile.getFile().getPath() +
                                        " doesn't exist.");
    } else if (isFileModified(ufile)) {
      parseHeader(false);
    }
    try {
      final long computedLength = UsageFile.HEADER_SIZE + (ufile.getUsageCount() * (long) UsageFile.RECORD_SIZE);
      if (this.bufferedFromPosition > UsageFile.FIRST_RECORD_POSITION ||
          this.buffer.limit() + this.bufferedFromPosition < computedLength) {
        ufile.openFile();
        ufile.aquireReadFileLock();
      }
      for (int i = 0; i < ufile.getUsageCount(); i++) {
        parseRecord(i, false);
      }
    } catch (Exception e) {
      throw new UsageFileParseException("Invalid or corrupted usage file " + 
                                        ufile.getFile().getName(), e);
    } finally {
      if (releaseLock) {
        try {
          ufile.releaseFileLock();
        } catch (IOException e) {
          log.error("Could not release lock on file " + ufile.getFile(), e);
        }
      }
    }
  }
  
  /**
   * Parses one specific record present in the usage file.
   * <p>
   * WARNING: if an UsageRecord already exists at this position, the parsing
   * will be aborted and the existing usage will be returned.
   * </p>
   * 
   * @param recordNumber
   *          index (0..n) of the record to be parsed.
   * @param releaseLock
   *          if <code>true</code>, the read lock will be released before
   *          exit.
   * @return the UsageRecord found or <code>null</code> if no usage
   * @throws UsageFileParseException
   * @throws IndexOutOfBoundsException
   *           if the recordNumber is invalid.
   */
  public UsageRecord parseRecord(int recordNumber, boolean releaseLock) throws UsageFileParseException {
    UsageFile ufile = this.usageFile.get();
    if (!ufile.getFile().exists()) {
      throw new UsageFileParseException("File " + ufile.usageFile.getPath() +
                                        " doesn't exist.");
    } else if (isFileModified(ufile)) {
      parseHeader(false);
    } else if (ufile.getUsageRecords() != null && ufile.getUsageRecords()[recordNumber] != null) {
      // already parsed this record
      return ufile.getUsageRecords()[recordNumber];
    }
    if (recordNumber > ufile.getUsageCount()) {
      throw new IndexOutOfBoundsException("The record number " + recordNumber + 
                                          " is invalid since this UsageFile has " + 
                                          ufile.getUsageCount() + " records.");
    }
    try {
      if (ufile.getUsageRecords() == null) {
        ufile.setUsageRecords( new UsageRecord[ufile.getUsageCount()] );
      }
      jumpToRecordInBuffer(recordNumber, ufile, releaseLock);
      int limit = this.buffer.limit();
      this.buffer.limit(this.buffer.position() + UsageFile.RECORD_SIZE);
      ufile.getUsageRecords()[recordNumber] = parseUsageRecord(this.buffer.slice());
      this.buffer.limit(limit);
      return ufile.getUsageRecords()[recordNumber];
    } catch (Exception e) {
      throw new UsageFileParseException("Invalid or corrupted usage file " + 
                                        ufile.getFile().getName(), e);
    }
  }
  
  /**
   * Prepares the buffer to read/have the record specified:
   * 
   * <ul>
   * <li>if the record already exists in the buffer, it's position will be set
   * to the start of the record</li>
   * <li>if the record doesn't exist in the buffer, the buffer will be cleared
   * and filled with new data having the desired record. It's position is
   * undefined, but the buffer's position will be set to the start of the record</li>
   * </ul>
   * 
   * @param recordNumber
   *          the number of record - the first one is <code>0</code>.
   */
  protected void jumpToRecordInBuffer(int recordNumber, UsageFile ufile, boolean releaseLock) 
  throws UsageFileParseException {
    long filePosition = UsageFile.FIRST_RECORD_POSITION + (UsageFile.RECORD_SIZE * 
                                                           (long)recordNumber);
    if (filePosition < this.bufferedFromPosition || 
        filePosition + UsageFile.RECORD_SIZE >= this.bufferedFromPosition + this.buffer.limit()) {
      // buffer doesn't contain the record
      // TODO: filling entire buffer starting from desired record: not sure
      //       this is good for performance
      try {
        ufile.openFile();
        ufile.aquireReadFileLock();
        read(filePosition, true);
      } catch (IOException e) {
        throw new UsageFileParseException(e);
      } finally {
        if (releaseLock) {
          try {
            ufile.releaseFileLock();
          } catch (IOException e) {
            log.error("Could not release lock on file " + ufile.getFile(), e);
          }
        }
      }
    }
    this.buffer.position( (int) (filePosition - this.bufferedFromPosition) );
  }
  
  protected UsageRecord parseUsageRecord(ByteBuffer record) throws UsageFileParseException {
    UsageRecord u = new UsageRecord();
    u.setHashCode( parseUsageHashCode(record) );
    u.setPartialSequence( parseUsagePartialSequence(record) );
    u.setStartSecondsWithinDay( parseUsageStartSecondsInDay(record) );
    u.setUsageDuration( parseUsageDuration(record) );
    u.setFromNumber( parseUsageFromNumber(record).trim() );
    u.setToNumber( parseUsageToNumber(record).trim() );
    u.setService( parseUsageService(record).trim() );
    return u;
  }
  
  
  protected int parseUsageHashCode(ByteBuffer record) throws UsageFileParseException {
    return record.getInt(UsageFile.HASHCODE_POSITION);
  }
  
  protected short parseUsagePartialSequence(ByteBuffer record) throws UsageFileParseException {
    return record.getShort(UsageFile.PARTIAL_SEQUENCE_POSITION);
  }
  
  protected int parseUsageStartSecondsInDay(ByteBuffer record) throws UsageFileParseException {
    return record.getInt(UsageFile.START_TIME_POSITION);
  }
  
  protected long parseUsageDuration(ByteBuffer record) throws UsageFileParseException {
    return record.getLong(UsageFile.DURATION_POSITION);
  }
  
  protected String parseUsageFromNumber(ByteBuffer record) throws UsageFileParseException {
    return getStringField(record, UsageFile.FROM_NUMBER_POSITION, UsageFile.FROM_NUMBER_FIELD_SIZE);
  }
  
  protected String parseUsageToNumber(ByteBuffer record) throws UsageFileParseException {
    return getStringField(record, UsageFile.TO_NUMBER_POSITION, UsageFile.TO_NUMBER_FIELD_SIZE);
  }
  
  protected String parseUsageService(ByteBuffer record) throws UsageFileParseException {
    return getStringField(record, UsageFile.SERVICE_POSITION, UsageFile.SERVICE_FIELD_SIZE);
  }
  
  protected String getStringField(ByteBuffer record, int position, short length) 
  throws UsageFileParseException {
    record.mark();
    record.position(position);
    int limit = record.limit();
    record.limit(position + length);
    String res = record.asCharBuffer().toString().trim();
    record.reset();
    record.limit(limit);
    return res;
  }
  
  
  /**
   * Prepare this instance's buffer for serialization, creating or clearing it
   * as necessary.
   * 
   * @param usageCount
   *          the number of <code>UsageRecord</code> that will be serialized,
   *          used to optimize the buffer's size upon creation.
   */
  private void clearBuffer() {
    if (this.buffer == null) {
      File file = this.usageFile.get().usageFile;
      this.buffer = ByteBuffer.allocateDirect( (int)Math.min(file.length(), 
                                                             (long)MAX_BUFFER_SIZE) );
    } else {
      this.buffer.clear();
    }
    this.bufferedFromPosition = -1L;
  }
  
  /**
   * Reads from the file channel into the instance's {@link #buffer} starting at
   * the specified position.
   * 
   * @param filePosition
   *          the start position to read from the file channel.
   * @param clearBuffer
   *          if <code>true</code> the buffer will be cleared first.
   * @return the size (in bytes) actually read from the file channel.
   * @throws IOException
   *           If an I/O error occurs.
   */
  final long read(long filePosition, boolean clearBuffer) throws IOException {
    if (clearBuffer) {
      clearBuffer();
    }
    UsageFile ufile = this.usageFile.get();
    ufile.openFile();
    ufile.fileChannel.position(filePosition);
    int bufferPos = this.buffer.position();
    try {
      this.bufferedFromPosition = filePosition;
      return NIOUtils.read(ufile.fileChannel, this.buffer);
    } catch (IOException e) {
      if (clearBuffer) {
        clearBuffer();
      } else {
        this.buffer.position(bufferPos);
      }
      throw e;
    }
  }

}
