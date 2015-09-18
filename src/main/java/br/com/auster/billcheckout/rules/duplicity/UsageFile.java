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
 * Created on 11/01/2007
 */
package br.com.auster.billcheckout.rules.duplicity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;

/**
 * TODO: class comments
 *
 * @author rbarone
 * @version $Id$
 */
public class UsageFile {
  
  //###############
  // Sizes
  //###############
  
  /**
   * {@value} : size, in chars, of the metadata's Account ID. May be overriden.
   */
  protected static short ACCOUNT_ID_CHAR_SIZE = 20;
  /**
   * {@value} : size, in bytes, of the metadata's Account ID.
   */
  protected static final short ACCOUNT_ID_FIELD_SIZE = (short) (ACCOUNT_ID_CHAR_SIZE 
                                                                * Character.SIZE / Byte.SIZE);
  /**
   * {@value} : size, in chars, of the metadata's Subscription ID. May be
   * overriden.
   */
  protected static short SUBSCRIPTION_ID_CHAR_SIZE = 40;
  /**
   * {@value} : size, in bytes, of the metadata's Subscription ID.
   */
  protected static final short SUBSCRIPTION_ID_FIELD_SIZE = (short) (SUBSCRIPTION_ID_CHAR_SIZE
                                                                     * Character.SIZE / Byte.SIZE);
  /**
   * {@value} : size, in bytes, of the metadata's date.
   */
  protected static final short DATE_FIELD_SIZE = Integer.SIZE / Byte.SIZE;
  /**
   * {@value} : size, in bytes, of the usage file's metadata info.
   */
  protected static final short METADATA_SIZE = (short) ( ACCOUNT_ID_FIELD_SIZE +
                                                         SUBSCRIPTION_ID_FIELD_SIZE +
                                                         DATE_FIELD_SIZE );
  
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short USAGE_MAP_FIELD_SIZE = (short) Math.ceil(24 * 60 / Byte.SIZE);
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short USAGE_COUNT_FIELD_SIZE = Integer.SIZE / Byte.SIZE;
  /**
   * {@value} : size, in bytes, of the entire header.
   */
  protected static final short HEADER_SIZE = (short) ( METADATA_SIZE + 
                                                       USAGE_MAP_FIELD_SIZE + 
                                                       USAGE_COUNT_FIELD_SIZE );
  
  
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short HASCHCODE_FIELD_SIZE =  Integer.SIZE / Byte.SIZE;
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short PARTIAL_SEQUENCE_FIELD_SIZE = Short.SIZE / Byte.SIZE;
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short START_TIME_FIELD_SIZE = Integer.SIZE / Byte.SIZE;
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short DURATION_FIELD_SIZE = Long.SIZE / Byte.SIZE;
  /**
   * {@value} : size, in chars, of this field. May be overriden.
   */
  protected static short FROM_NUMBER_CHAR_SIZE = 40;
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short FROM_NUMBER_FIELD_SIZE = (short) (FROM_NUMBER_CHAR_SIZE
                                                                 * Character.SIZE / Byte.SIZE);
  /**
   * {@value} : size, in chars, of this field. May be overriden.
   */
  protected static short TO_NUMBER_CHAR_SIZE = 40;
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short TO_NUMBER_FIELD_SIZE = (short) (TO_NUMBER_CHAR_SIZE 
                                                               * Character.SIZE / Byte.SIZE);
  /**
   * {@value} : size, in chars, of this field. May be overriden.
   */
  protected static short SERVICE_CHAR_SIZE = 128;
  /**
   * {@value} : size, in bytes, of this field.
   */
  protected static final short SERVICE_FIELD_SIZE = (short) (SERVICE_CHAR_SIZE 
                                                             * Character.SIZE / Byte.SIZE);
  
  /**
   * {@value} : size, in bytes, of an entire record.
   */
  protected static final short RECORD_SIZE = (short) ( HASCHCODE_FIELD_SIZE +
                                                       PARTIAL_SEQUENCE_FIELD_SIZE +
                                                       START_TIME_FIELD_SIZE +
                                                       DURATION_FIELD_SIZE +
                                                       FROM_NUMBER_FIELD_SIZE +
                                                       TO_NUMBER_FIELD_SIZE +
                                                       SERVICE_FIELD_SIZE );
  
  
  //###############
  // Positions
  //###############
  
  protected static final int HEADER_POSITION = 0;
  
  protected static final int METADATA_POSITION = HEADER_POSITION;
  protected static final int ACCOUNT_ID_POSITION = METADATA_POSITION;
  protected static final int SUBSCRIPTION_ID_POSITION = ACCOUNT_ID_POSITION + ACCOUNT_ID_FIELD_SIZE;
  protected static final int DATE_POSITION = SUBSCRIPTION_ID_POSITION + SUBSCRIPTION_ID_FIELD_SIZE;
  
  protected static final int USAGE_MAP_POSITION = METADATA_POSITION + METADATA_SIZE;
  protected static final int USAGE_COUNT_POSITION = USAGE_MAP_POSITION + USAGE_MAP_FIELD_SIZE;
  
  protected static final int FIRST_RECORD_POSITION = HEADER_POSITION + HEADER_SIZE;
  
  protected static final int HASHCODE_POSITION = 0;
  protected static final int PARTIAL_SEQUENCE_POSITION = HASHCODE_POSITION + HASCHCODE_FIELD_SIZE;
  protected static final int START_TIME_POSITION = PARTIAL_SEQUENCE_POSITION + PARTIAL_SEQUENCE_FIELD_SIZE;
  protected static final int DURATION_POSITION = START_TIME_POSITION + START_TIME_FIELD_SIZE;
  protected static final int FROM_NUMBER_POSITION = DURATION_POSITION + DURATION_FIELD_SIZE;
  protected static final int TO_NUMBER_POSITION = FROM_NUMBER_POSITION + FROM_NUMBER_FIELD_SIZE;
  protected static final int SERVICE_POSITION = TO_NUMBER_POSITION + TO_NUMBER_FIELD_SIZE;
  
  
  //######################
  // Other static fields
  //######################
  
  protected static final String BACKUP_SUFFIX = ".bck$";
  
  private static final Logger log = LogFactory.getLogger(UsageFile.class);
  
  
  //######################
  // Instance fields
  //######################
  
  protected final File usageFile;
  protected FileChannel fileChannel;
  protected FileLock fileLock;
  
  protected final File backupFile;
  protected FileChannel backupChannel;
  protected FileLock backupLock;
  
  private UsageFileMetadata metadata;
  
  private final byte[] usageMap;
  
  private int usageCount;
  
  private UsageRecord[] usages;
  
  protected UsageFileSerializer serializer;
  
  protected UsageFileParser parser;
  
  protected boolean isModificationsCommitted = true;
  
  
  /**
   * Creates a new <codE>UsageFile</code> that is not yet backed by a
   * persisted file. All related information may be passed at this point, but
   * the file <b>won't</b> be automatically serialized.
   * 
   * @param file
   *          the file that contains the data for this UsageFile.
   * @param metadata
   *          the metadata information shared by all usages in this
   *          UsageFile.
   * @param usageMap
   *          the map of bits representing the start second (within the hour of
   *          the <code>dateAndHour</code> metadata) of each usage in this file.
   *          It's length must be exactly equal to {@link #USAGE_MAP_FIELD_SIZE}.
   * @param usages
   *          the usages stored in this <code>UsageFile</code>.
   * @throws DuplicatedUsageFileException
   *           if an UsageFile for the given filename already exists.
   * @throws IOException
   *           If an I/O error occurs
   */
  public UsageFile(File file, UsageFileMetadata metadata, 
                   byte[] usageMap, UsageRecord[] usages) 
  throws DuplicatedUsageFileException, IOException {
    if (file.exists()) {
      throw new DuplicatedUsageFileException("File " + file + " already exists.");
    }
    this.usageFile = file;
    this.backupFile = new File(file.getPath() + BACKUP_SUFFIX);
    this.metadata = metadata;
    
    if (usageMap == null) {
      usageMap = new byte[USAGE_MAP_FIELD_SIZE];
    } else if (usageMap.length != USAGE_MAP_FIELD_SIZE) {
      throw new IllegalArgumentException("Usage map array length must be exactly " + 
                                         USAGE_MAP_FIELD_SIZE);
    }
    this.usageMap = usageMap;
    
    if (usages == null) {
      usages = new UsageRecord[0];
    }
    this.usages = usages;
    this.usageCount = usages.length;
    
    openFile();
  }
  
  /**
   * <i>**Internal use only**</i>
   * <p>
   * Creates a new <codE>UsageFile</code> instance with no information.
   * </p>
   * 
   * @param file
   *          the file that contains the data for this UsageFile.
   * @throws DuplicatedUsageFileException
   *           if an UsageFile for the given filename already exists.
   * @throws IOException
   *           If an I/O error occurs
   */
  protected UsageFile(File file) 
  throws DuplicatedUsageFileException, IOException {
    this.usageFile = file;
    this.backupFile = new File(file.getPath() + BACKUP_SUFFIX);
    this.usageMap = new byte[USAGE_MAP_FIELD_SIZE];
    openFile();
  }
  
  protected File getFile() {
    return this.usageFile;
  }
  
  protected File getBackupFile() {
    return this.backupFile;
  }
  
  public final UsageFileMetadata getMetadata() {
    return this.metadata;
  }
  
  final void setMetadata(UsageFileMetadata _metadata) {
    this.metadata = _metadata;
  }

  public final int getUsageCount() {
    return this.usageCount;
  }
  
  final void setUsageCount(int _count) {
    this.usageCount = _count;
  }
  
  final int updateUsageCount() {
    UsageRecord[] _usages = getUsageRecords();
    if (_usages == null) {
      this.usageCount = 0;
    } else if (this.usageCount != _usages.length) {
      this.usageCount = _usages.length;
    }
    return this.usageCount;
  }

  public final byte[] getUsageMap() {
    return this.usageMap;
  }

  public final UsageRecord[] getUsageRecords() {
    return this.usages;
  }
  
  public final void setUsageRecords(UsageRecord[] _usages) {
    this.usages = _usages;
    this.usageCount = this.usages == null ? 0 : this.usages.length;
  }
  
  public final void addUsageRecord(UsageRecord _usage) {
    UsageRecord[] _usages = new UsageRecord[this.usages.length + 1];
    System.arraycopy(this.usages, 0, _usages, 0, this.usages.length);
    _usages[this.usages.length] = _usage;
    this.usages = _usages;
    this.usageCount = _usages.length;
  }
  
  /**
   * Parses an usage file, returning a new <code>UsageFile</code> instance
   * representing it.
   * 
   * @param file
   *          the persisted usage file.
   * @param parseRecords
   *          if <code>true</code>, all records will be parsed. Otherwise,
   *          only the header will be parsed.
   * @return a new <code>UsageFile</code> instance for the specified filename.
   * @throws DuplicatedUsageFileException
   *           if an UsageFile for the given filename already exists.
   * @throws FileNotFoundException
   *           If the file denoted by the specified filename doesn't exist
   * @throws IOException
   *           If an I/O error occurs
   * @throws UsageFileParseException
   *           If any error occurs while parsing the file
   */
  public static UsageFile parse(File file, boolean parseRecords) 
  throws DuplicatedUsageFileException, FileNotFoundException, IOException, UsageFileParseException {
    if (! file.exists()) {
      throw new FileNotFoundException("File " + file + " does not exist.");
    }
    UsageFile uf = new UsageFile(file);
    if (parseRecords) {
      uf.getParser().parse();
    } else {
      uf.getParser().parseHeader(true);
    }
    return uf;
  }
  
  public UsageFileParser getParser() {
    if (this.parser == null) {
      this.parser = new UsageFileParser(this);
    }
    return this.parser;
  }
  
  public UsageFileSerializer getSerializer() {
    if (this.serializer == null) {
      this.serializer = new UsageFileSerializer(this);
    }
    return this.serializer;
  }
  
  /**
   * Opens and locks the file specified by the <code>filename</code> parameter
   * in the constructor. This means the file will be locked until
   * {@link #close()} is called.
   * 
   * @throws IOException
   *           If an I/O error occurs
   */
  final void openFile() throws DuplicatedUsageFileException, IOException {
    if (this.fileChannel == null || !this.fileChannel.isOpen()) {
      this.fileChannel = new RandomAccessFile(this.usageFile, "rwd").getChannel();
      this.fileChannel.force(false);
      if (this.fileLock != null) {
        this.fileLock.release();
        this.fileLock = null;
      }
    }
  }
  
  final void aquireWriteFileLock() throws IOException {
    if (this.fileChannel == null || !this.fileChannel.isOpen()) {
      throw new IllegalStateException("Cannot aquire lock: file is not open.");
    }
    if (this.fileLock == null) {
      this.fileLock = this.fileChannel.lock();
    } else if (!this.fileLock.isValid() || this.fileLock.isShared()) {
      this.fileLock.release();
      this.fileLock = this.fileChannel.lock();
    }
  }
  
  final void aquireReadFileLock() throws IOException {
    if (this.fileChannel == null || !this.fileChannel.isOpen()) {
      throw new IllegalStateException("Cannot aquire lock: file is not open.");
    }
    if (this.fileLock == null || !this.fileLock.isValid() || !this.fileLock.isShared()) {
      if (this.fileLock != null) {
        this.fileLock.release();
      }
      this.fileLock = this.fileChannel.lock(0, Long.MAX_VALUE, true);
    }
  }
  
  final void releaseFileLock() throws IOException {
    if (this.fileLock != null) {
      this.fileLock.release();
      this.fileLock = null;
    }
  }
  
  final void openBackup() throws DuplicatedUsageFileException, IOException {
    try {
      if (this.backupLock == null || this.backupChannel == null || !this.backupChannel.isOpen()) {
        this.backupChannel = new FileOutputStream(this.backupFile).getChannel();
        this.backupChannel.force(false);
        this.backupLock = this.backupChannel.lock();
      } else if (!this.backupLock.isValid()) {
        this.backupLock = this.backupChannel.lock();
      }
    } catch (OverlappingFileLockException e) {
      closeBackup();
      throw new DuplicatedUsageFileException("Backup file " + this.backupFile.getAbsolutePath()
                                             + " is already being used by another UsageFile"
                                             + " or was locked by another application", e);
    }
  }
  
  /**
   * Closes the file channel, releasing any valid locks. This method is
   * automatically called by the {@link #finalize()} method and should never be
   * called before that, otherwise strange things could happen (like allowing to
   * create another UsageFile instance using the same filename as this one).
   * <p>
   * The downside of not calling <code>close()</code> before the
   * {@link #finalize()} method runs, is that you won't be able to create a new
   * <code>UsageFile</code> instance for this filename until the old one is
   * garbage-collected.
   * </p>
   * 
   * @throws IOException
   *           If an I/O error occurs
   */
  public void close() throws IOException {
    closeFile();
    closeBackup();
  }
  
  final void closeFile() throws IOException {
    if (this.fileChannel != null) {
      this.fileChannel.close();
      this.fileChannel = null;
    }
    this.fileLock = null;
    if (this.usageFile.exists() && this.usageFile.length() == 0) {
      this.usageFile.delete();
    }
  }
  
  final void closeBackup() throws IOException {
    if (this.backupChannel != null) {
      this.backupChannel.close();
      this.backupChannel = null;
    }
    this.backupLock = null;
    if (this.backupFile.length() == 0) {
      this.backupFile.delete();
    }
  }
  
  @Override
  protected void finalize() throws Throwable {
    try {
      close();
    } catch (Throwable t) {}
  }
  
  /**
   * Creates a backup of the current persisted file. This should be called
   * before serializing anything new to an existing file.
   * <p>
   * If a backup file already exists, it will first be deleted.
   * </p>
   * 
   * @throws IOException
   */
  final void createBackup() throws IOException {
    closeBackup();
    if (this.backupFile.exists()) {
      this.backupFile.delete();
    }
    openFile();
    try {
      aquireWriteFileLock();
      openBackup();
      long copied = 0L, size = this.fileChannel.size();
      while ((copied += this.fileChannel.transferTo(0, size, this.backupChannel)) != size);
      if (this.backupChannel.size() != this.fileChannel.size()) {
        closeBackup();
        this.backupFile.delete();
        throw new IOException("Failed to backup the original file - cannot proceed.");
      }
    } finally {
      releaseFileLock();
    }
  }
  
  /**
   * Restores an existing backup of the usage file. This should be called
   * whenever a serialization error occurs, given that a backup was previously
   * created.
   * 
   * @throws FileNotFoundException
   *           If the backup file doesn't exist.
   * @throws IOException
   *           If the restore has failed.
   */
  final void commitBackup() throws FileNotFoundException, IOException {
    if (!this.backupFile.exists()) {
      throw new FileNotFoundException("Commit fatal error: Backup file "
                                      + this.backupFile.getAbsolutePath() + " doesn't exist.");
    }
    close();
    if (this.usageFile.exists()) {
      this.usageFile.delete();
    }
    if (!this.backupFile.renameTo(this.usageFile)) {
      throw new IOException("Failed to commit the backup file " + this.usageFile.getAbsolutePath() 
                            + " - cannot proceed.");
    }
    isModificationsCommitted = true;
  }
  
  /**
   * Deletes the usage file and backup (if it exists). This will clear
   * the following instance fields:
   * 
   * <ul>
   * <li>fileLock</li>
   * <li>backupLock</li>
   * <li>usageMap</li>
   * <li>usageCount</li>
   * <li>usages</li>
   * </ul>
   */
  final void delete() {
    deleteFile();
    deleteBackup();
  }
  
  /**
   * Deletes this usage file. This will clear the following instance fields:
   * 
   * <ul>
   * <li>fileLock</li>
   * <li>backupLock</li>
   * <li>usageMap</li>
   * <li>usageCount</li>
   * <li>usages</li>
   * </ul>
   */
  final void deleteFile() {
    try {
      closeFile();
      this.usageFile.delete();
      for (int i = 0; i < this.usageMap.length; i++) {
        this.usageMap[i] = 0;
      }
      this.usageCount = 0;
      this.usages = null;
    } catch (IOException e) {
      log.error("Error while removing usage file: " + this.usageFile, e);
    }
  }
  
  /**
   * Deletes the usage backup file (if it exists).
   */
  final void deleteBackup() {
    try {
      closeBackup();
      this.backupFile.delete();
    } catch (IOException e) {
      log.error("Error while removing usage backup file: " + this.backupFile, e);
    }
  }
  
}
