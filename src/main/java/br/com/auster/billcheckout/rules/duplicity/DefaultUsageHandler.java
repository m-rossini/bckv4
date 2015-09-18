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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;

/**
 * TODO: class comments
 *
 * @author rbarone
 * @version $Id$
 */
public class DefaultUsageHandler implements UsageHandler {
  
  private static final Logger log = LogFactory.getLogger(DefaultUsageHandler.class);
  
  /**
   * {@value}
   */
  public static final String BASEDIR_PROPERTY = "br.com.auster.billcheckout.rules.duplicity.basedir";
  public static final File BASEDIR;
  static {
    String basedir = System.getProperty(BASEDIR_PROPERTY, ".");
    BASEDIR = new File(basedir);
    if (!BASEDIR.exists()) {
      BASEDIR.mkdirs();
    } else if (!BASEDIR.isDirectory()) {
      log.fatal("Duplicity's basedir is not a valid directory - duplicity files won't be read/written.");
      throw new IllegalStateException("Duplicity's basedir is not a valid directory - duplicity won't be read/written.");
    }
  }
  
  private static final String FILE_SUFFIX = ".usg";
  
  private Map<UsageFileMetadata,Set<UsageRecord>> usageMap = 
    new HashMap<UsageFileMetadata,Set<UsageRecord>>();
  
  private Set<UsageFile> pendingUsageFiles = new HashSet<UsageFile>();
  
  protected DefaultUsageHandler() {
    // does nothing
  }
  
  /**
   * {@inheritDoc}
   */
  public void initTransaction() {
    this.usageMap.clear();
    for (UsageFile uf : this.pendingUsageFiles) {
      uf.deleteBackup();
      try {
        uf.close();
      } catch (Exception e) {
        log.error("Could not close the UsageFile " + uf.getFile().getAbsolutePath(), e);
      }
    }
    this.pendingUsageFiles.clear();
  }
  
  /**
   * {@inheritDoc}
   */
  public void addUsageRecord(UsageFileMetadata metadata, UsageRecord usage) {
    if (metadata == null) {
      throw new IllegalArgumentException("UsageFileMetadata param cannot be null.");
    } else if (usage == null) {
      throw new IllegalArgumentException("UsageRecord param cannot be null.");
    }
    Set<UsageRecord> set = this.usageMap.get(metadata);
    if (set == null) {
      set = new HashSet<UsageRecord>();
      this.usageMap.put(metadata, set);
    }
    set.add(usage);
  }
  
  /**
   * {@inheritDoc}
   */
  public void serializeUsageMap(boolean overwritesBackup) {
    for (Map.Entry<UsageFileMetadata,Set<UsageRecord>> entry : this.usageMap.entrySet()) {
      UsageFile uf = serialize(entry.getKey(), entry.getValue(), overwritesBackup, false);
      if (uf != null) {
        this.pendingUsageFiles.add(uf);
      }
    }
    this.usageMap.clear();
  }
  
  /**
   * {@inheritDoc}
   */
  public void serializeUsageMap() {
    serializeUsageMap(false);
  }
  
  /**
   * {@inheritDoc}
   */
  public List<File> commitTransaction() {
    List<File> failedFiles = new ArrayList<File>();
    for (UsageFile uf : this.pendingUsageFiles) {
      try {
        uf.commitBackup();
      } catch (FileNotFoundException e) {
        log.info("Nothing to commit for file " + uf.getFile().getAbsolutePath());
      } catch (Exception e) {
        failedFiles.add(uf.getBackupFile());
        log.error("Failed to commit usage file backup " + uf.getBackupFile().getAbsolutePath(), e);
      } finally {
        try {
          uf.close();
        } catch (Exception e) {
          log.error("Could not close the UsageFile " + uf.getFile().getAbsolutePath(), e);
        }
      }
    }
    return failedFiles;
  }
  
  /**
   * {@inheritDoc}
   */
  public void rollbackTransaction() {
    initTransaction();
  }

  /**
   * Serializes and commits a random <code>UsageRecord</code> object (not yet
   * associated to any usage file).
   * 
   * <p>
   * This method will first try to find an existing file that should contain the
   * usage - any existing backup files will be updated. If none is found, a new
   * <code>UsageFile</code> will be created.
   * </p>
   * <p>
   * For an existing file, this usage will be inserted at the end of the file.
   * </p>
   * <p>
   * The usage map will be updated accordingly and the usage file will be
   * committed.
   * </p>
   * 
   * @param metadata
   *          the metadata of the usage's unique {@link UsageFile}.
   * @param usage
   *          the usage to serialize.
   * @throws IllegalArgumentException
   *           if any of the mandatory fields is missing.
   * @see UsageHandler#serialize(UsageRecord)
   */
  public void serialize(UsageFileMetadata metadata, UsageRecord usage) {
    if (metadata == null) {
      throw new IllegalArgumentException("UsageFileMetadata param cannot be null.");
    } else if (usage == null) {
      throw new IllegalArgumentException("UsageRecord param cannot be null.");
    }
    Set<UsageRecord> l = new HashSet<UsageRecord>(1);
    l.add(usage);
    serialize(metadata, l, false, true);
  }
  
  /**
   * Serializes and commits a map of <code>UsageRecord</code> objects, where
   * each entry has a list of records that belongs to the same
   * <code>UsageFile</code>.
   * <p>
   * This is the same as:
   * </p>
   * 
   * <pre>
   * for (Map.Entry&lt;UsageFileMetadata, List&lt;UsageRecord&gt;&gt; usageList : usages) {
   *   serialize(usageList.getKey(), usageList.getValue());
   * }
   * </pre>
   * 
   * @param usages
   *          a map of {@link UsageRecord} objects to serialize - the key is the
   *          metadata of the usage set's unique {@link UsageFile}.
   * @throws IllegalArgumentException
   *           if any of the mandatory fields is missing.
   * @see UsageHandler#serialize(HashMap)
   */
  public void serialize(Map<UsageFileMetadata,Set<UsageRecord>> usages) {
    for (Map.Entry<UsageFileMetadata,Set<UsageRecord>> entry : usages.entrySet()) {
      serialize(entry.getKey(), entry.getValue(), false, true);
    }
  }
  
  /**
   * Serializes and commits a list of <code>UsageRecord</code> objects that
   * belongs to the same <code>UsageFile</code>.
   * 
   * @param metadata
   *          the metadata of the usages' unique {@link UsageFile}.
   * @param usages
   *          a set of {@link UsageRecord} objects to serialize.
   * @throws IllegalArgumentException
   *           if any of the mandatory fields is missing.
   * @see UsageHandler#serialize(HashMap)
   */
  public void serialize(UsageFileMetadata metadata, Set<UsageRecord> usages) {
    serialize(metadata, usages, false, true);
  }

  /**
   * {@inheritDoc}
   */
  public UsageRecord search(UsageFileMetadata metadata, UsageRecord usage) {
    if (metadata == null) {
      throw new IllegalArgumentException("UsageFileMetadata param cannot be null.");
    } else if (usage == null) {
      throw new IllegalArgumentException("UsageRecord param cannot be null.");
    }
    UsageFile uf = null;
    try {
      uf = getUsageFileForRead(metadata);
      if (uf == null) {
        return null;
      }
      if ( ! hasUsageAt(uf.getUsageMap(), usage.getStartSecondsWithinDay()) ) {
        return null;
      }
      UsageFileParser parser = uf.getParser();
      uf.openFile();
      uf.aquireReadFileLock();
      for (int i = 0; i < uf.getUsageCount(); i++) {
        UsageRecord u = parser.parseRecord(i, false); // incremental parsing
        if (usage.getHashCode() == u.getHashCode() && usage.equals(u)) {
          return u;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (uf != null) {
        try {
          uf.close();
        } catch (IOException e) {
          log.error("Could not close the UsageFile " + uf.getFile(), e);
        }
      }
    }
    return null;
  }
  
  protected File getUsageFilePath(UsageFileMetadata metadata) {
    StringBuilder b = new StringBuilder(BASEDIR.getPath()).append(File.separatorChar);
    
    // Create a directory using the last 3 chars of the Account ID
    int accLength = metadata.getAccountId().length();
    if (accLength > 3) {
      b.append(metadata.getAccountId().substring(accLength - 3));
      b.append(File.separatorChar);
    }
    b.append(metadata.getAccountId()).append(File.separatorChar);
    
    //  Create a directory using the last 2 chars of the Subscription ID
    int subsLength = metadata.getSubscriptionId().length();
    if (subsLength > 2) {
      b.append(metadata.getSubscriptionId().substring(subsLength - 2));
      b.append(File.separatorChar);
    }
    b.append(metadata.getSubscriptionId()).append(File.separatorChar);
    
    int[] d = UsageFileMetadata.parseDate(metadata.getDate());
    b.append(d[0]).append(File.separatorChar); // year
    b.append(d[1]).append(File.separatorChar); // month
    b.append(d[2]).append(FILE_SUFFIX); // day
    
    return new File(b.toString());
  }
  
  protected UsageFile getUsageFileForReadWrite(UsageFileMetadata metadata) throws IOException {
    File file = getUsageFilePath(metadata);
    UsageFile uf;
    if (file.exists()) {
      uf = UsageFile.parse(file, true);
    } else {
      file.getParentFile().mkdirs();
      uf = new UsageFile(file, metadata, null, null);
    }
    return uf;
  }
  
  protected UsageFile getUsageFileForRead(UsageFileMetadata metadata) throws IOException {
    File file = getUsageFilePath(metadata);
    try {
      if (file.exists()) {
        UsageFile f = UsageFile.parse(file, false);
        return f;
      }
    } catch (FileNotFoundException e) {
    }
    return null;
  }
  
  protected boolean hasUsageAt(byte[] usageMap, int startSecondsWithinDay) {
    int startMinuteWithinDay = startSecondsWithinDay / 60;
    int index = startMinuteWithinDay / Byte.SIZE;
    int bitMask = (startMinuteWithinDay % Byte.SIZE) + 1;
    return (usageMap[index] & bitMask) != 0;
  }
  
  private void updateUsageMap(byte[] usageMap, int startSecondsWithinDay) {
    int startMinuteWithinDay = startSecondsWithinDay / 60;
    int index = startMinuteWithinDay / Byte.SIZE;
    int bitMask = (startMinuteWithinDay % Byte.SIZE) + 1;
    usageMap[index] |= bitMask;
  }
  
  protected UsageFile serialize(UsageFileMetadata metadata, Set<UsageRecord> usages, 
                                boolean overwritesBackup, boolean commit) {
    UsageFile uf = null;
    if (metadata == null) {
      throw new IllegalArgumentException("UsageFileMetadata param cannot be null.");
    } else if (usages == null) {
      throw new IllegalArgumentException("Set<UsageRecord> param cannot be null.");
    } else if (usages.size() == 0) {
      return uf;
    }
    try {
      uf = getUsageFileForReadWrite(metadata);
      boolean hasChanged = false;
      OUTER: for (UsageRecord usage : usages) {
        if (usage.getStartSecondsWithinDay() == Integer.MIN_VALUE) {
          throw new IllegalArgumentException("Mandatory field not set: UsageRecord->startSecondsWithinDay");
        } else if ( hasUsageAt(uf.getUsageMap(), usage.getStartSecondsWithinDay()) ) {
          // need to find out if it's duplicated
          for (UsageRecord u : uf.getUsageRecords()) {
            if (usage.getHashCode() == u.getHashCode() && usage.equals(u)) {
              // duplicated usage - no need to add it again
              continue OUTER;
            }
          }
        }
        uf.addUsageRecord(usage);
        updateUsageMap(uf.getUsageMap(), usage.getStartSecondsWithinDay());
        hasChanged = true;
      }
      if (hasChanged) {
        uf.getSerializer().serialize(overwritesBackup, commit);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (uf != null) {
        try {
          uf.close();
        } catch (IOException e) {
          log.error("Could not close the UsageFile " + uf.getFile(), e);
        }
      }
    }
    return uf;
  }
  
}
