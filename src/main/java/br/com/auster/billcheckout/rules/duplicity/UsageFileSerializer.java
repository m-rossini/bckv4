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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Responsible for the serialization of {@link UsageFile} instances.
 * 
 * @author rbarone
 * @version $Id$
 */
final class UsageFileSerializer {

  private static final int MAX_BUFFER_SIZE = 16 * 1024;
  
  // this is not final because we may never initialize it
  // in order to save memory
  private ByteBuffer buffer;
  
  private final WeakReference<UsageFile> usageFile;
  
  UsageFileSerializer(UsageFile ufile) {
    this.usageFile = new WeakReference<UsageFile>(ufile);
  }
  
  /**
   * Serialize the specified usage file using a backup file.
   * 
   * @param forceNewBackup
   *          if <code>true</code>, a new backup file will be created even if it already exists.
   * @param commit
   *          if <code>true</code>, the backup file will be restored before this method exits.
   * @throws UsageFileSerializationException
   */
  public final void serialize(boolean forceNewBackup, boolean commit) 
  throws UsageFileSerializationException {
    UsageFile ufile = this.usageFile.get();
    try {
      if (prepareToSerialize(ufile, forceNewBackup) == false) {
        return;
      }
    } catch (IOException e) {
      throw new UsageFileSerializationException(e);
    }
    try {
      ufile.isModificationsCommitted = false;
      ufile.backupChannel.truncate(UsageFile.HEADER_SIZE + 
                                   (UsageFile.RECORD_SIZE * (long)ufile.getUsageRecords().length));
      serializeHeader(false, false);
      serializeRecords(false, false);
      if (commit) {
        ufile.commitBackup();
      }
    } catch (Throwable t) {
      ufile.deleteBackup();
      throw new UsageFileSerializationException("Error while serializing usage file: " + 
                                                ufile.usageFile.getAbsolutePath(), t);
    }
    
    this.buffer.clear();
  }
  
  /**
   * Serialize the header into a new or existing file. If any records exist,
   * they will reamain intact.
   * 
   * @param forceNewBackup
   *          if <code>true</code>, a new backup file will first be created.
   * @param commit
   *          if <code>true</code>, the backup file will be restored before this method exits.
   * @throws UsageFileSerializationException
   */
  public final void serializeHeader(boolean forceNewBackup, boolean commit) 
  throws UsageFileSerializationException {
    UsageFile ufile = this.usageFile.get();
    try {
      if (prepareToSerialize(ufile, forceNewBackup) == false) {
        return;
      }
    } catch (IOException e) {
      throw new UsageFileSerializationException(e);
    }
    ufile.isModificationsCommitted = false;
    UsageFileMetadata metadata = ufile.getMetadata();
    CharBuffer cb = this.buffer.asCharBuffer();
    putString(cb, metadata.getAccountId(), UsageFile.ACCOUNT_ID_CHAR_SIZE);
    putString(cb, metadata.getSubscriptionId(), UsageFile.SUBSCRIPTION_ID_CHAR_SIZE);
    this.buffer.position(UsageFile.DATE_POSITION);
    this.buffer.putInt(metadata.getDate());
    this.buffer.put( ufile.getUsageMap() );
    this.buffer.putInt( ufile.updateUsageCount() );
    this.buffer.flip();
    try {
      int length = 0, size = this.buffer.remaining();
      while((length += ufile.backupChannel.write(this.buffer, 0L)) != size);
    } catch (IOException e) {
      this.buffer.clear();
      ufile.deleteBackup();
      throw new UsageFileSerializationException(e);
    }
    if (commit) {
      try {
        ufile.commitBackup();
      } catch (IOException ioe) {
        throw new UsageFileSerializationException("Could not restore usage file!", ioe);
      }
    }
  }
  
  /**
   * Serialize all records to the underlying file. All previous
   * usage data (records) will be discarded. The header is not
   * changed.
   * 
   * @param forceNewBackup
   *          if <code>true</code>, a new backup file will first be created.
   * @param commit
   *          if <code>true</code>, the backup file will be restored before this method exits.
   * @throws UsageFileSerializationException
   */
  public final void serializeRecords(boolean forceNewBackup, boolean commit) 
  throws UsageFileSerializationException {
    UsageFile ufile = this.usageFile.get();
    UsageRecord[] usages = ufile.getUsageRecords();
    try {
      if (prepareToSerialize(ufile, forceNewBackup) == false) {
        return;
      }
      ufile.isModificationsCommitted = false;
      ufile.backupChannel.truncate(UsageFile.HEADER_SIZE + (UsageFile.RECORD_SIZE * 
                                                            (long)usages.length));
      ufile.backupChannel.position(UsageFile.FIRST_RECORD_POSITION);
    } catch (IOException e) {
      throw new UsageFileSerializationException(e);
    }
    int lastSerializedRecord = -1;
    while (lastSerializedRecord + 1 < usages.length) {
      this.buffer.clear();
      for (int i = 0; 
           i < this.buffer.limit() && 
           this.buffer.remaining() >= UsageFile.RECORD_SIZE && 
           lastSerializedRecord + 1 < usages.length; 
           i+=UsageFile.RECORD_SIZE) {
        UsageRecord u = usages[++lastSerializedRecord];
        int recordEndPos = this.buffer.position() + UsageFile.RECORD_SIZE;
        this.buffer.putInt( u.getHashCode() );
        this.buffer.putShort( u.getPartialSequence() );
        this.buffer.putInt( u.getStartSecondsWithinDay() );
        this.buffer.putLong( u.getUsageDuration() );
        CharBuffer cb = this.buffer.asCharBuffer();
        putString(cb, u.getFromNumber(), UsageFile.FROM_NUMBER_CHAR_SIZE);
        putString(cb, u.getToNumber(), UsageFile.TO_NUMBER_CHAR_SIZE);
        putString(cb, u.getService(), UsageFile.SERVICE_CHAR_SIZE);
        this.buffer.position(recordEndPos);
      }
      this.buffer.flip();
      try {
        int length = 0, size = this.buffer.remaining();
        while((length += ufile.backupChannel.write(this.buffer)) != size);
      } catch (IOException e) {
        this.buffer.clear();
        ufile.deleteBackup();
        throw new UsageFileSerializationException(e);
      }
    }
    this.buffer.clear();
    if (commit) {
      try {
        ufile.commitBackup();
      } catch (IOException ioe) {
        throw new UsageFileSerializationException("Could not restore usage file!", ioe);
      }
    }
  }
  
  
  private void putString(CharBuffer cb, String str, int maxLength) {
    if (str.length() > maxLength) {
      cb.put(str, 0, maxLength);
    } else {
      cb.put(str);
      for (int i = 0; i < maxLength - str.length(); i++) {
        cb.put('\0');
      }
    }
  }
  
  /**
   * Prepare this instance's buffer for serialization, creating or clearing it
   * as necessary.
   * 
   * @param usageCount
   *          the number of <code>UsageRecord</code> that will be serialized,
   *          used to optimize the buffer's size upon creation.
   */
  private void prepareBuffer(int usageCount) {
    if (this.buffer == null) {
      long bufferSize = (UsageFile.HEADER_SIZE + (UsageFile.RECORD_SIZE * (long)usageCount));
      this.buffer = ByteBuffer.allocateDirect( (int)Math.min(bufferSize, 
                                                             (long)MAX_BUFFER_SIZE) );
    } else {
      this.buffer.clear();
    }
  }
  
  /**
   * Prepare the backup file for serialization, returning a boolean to indicate
   * if there is at least one usage info to serialize.
   * 
   * @param ufile
   *          the usage file that will be serialized.
   * @return <code>true</code> if there is at least one <code>UsageRecord</code>
   *         to be serialized, <code>false</code> otherwise.
   */
  private boolean prepareToSerialize(UsageFile ufile, boolean forceNewBackup)
  throws IOException {
    if (ufile.getUsageCount() == 0 || ufile.getUsageRecords() == null || ufile.getUsageRecords().length == 0) {
      ufile.delete();
      return false;
    }
    if (forceNewBackup || !ufile.backupFile.exists()) {
      ufile.createBackup();
    } else {
      ufile.openBackup();
    }
    prepareBuffer(ufile.getUsageRecords().length);
    return true;
  }
  
}
