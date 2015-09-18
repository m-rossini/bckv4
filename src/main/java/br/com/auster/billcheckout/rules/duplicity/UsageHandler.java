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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An UsageHandler is used to {@link #serialize(UsageRecord) serialize} and
 * {@link #search(UsageRecord) search} for usages in the storage system.
 * 
 * <p>
 * Instances of this interface must be created using an
 * {@link UsageHandlerFactory}.
 * </p>
 * <p>
 * UsageHandler instances are stateful: an empty usage map is created after a
 * new instance is created and this usage map can be used to add new records
 * through {@link #addUsageRecord(UsageFileMetadata, UsageRecord)} and
 * {@link #serializeUsageMap()} them after you're finished.
 * </p>
 * <p>
 * The serialization is not commited, and this must be done through the
 * {@link #commitUsageMap()} method, when all usage files in the map will be
 * commited, but with no guarantee of atomicity (some usage files may fail to
 * commit).
 * </p>
 * 
 * @author rbarone
 * @version $Id$
 */
public interface UsageHandler {
  
  /**
   * Prepares this handler for a new transaction consisting of one ore more the
   * following method calls:
   * 
   * <pre>
   *      {@link #addUsageRecord(UsageFileMetadata, UsageRecord)}
   *      {@link #serializeUsageMap()}
   * </pre>
   * 
   * Followed by a call to {@link #commitTransaction()}.
   * 
   * <p>
   * All previous usage files pending to commit or usage records pending to
   * serialize will be discarded. It's backup files will be also deleted.
   * </p>
   */
  public void initTransaction();
  
  /**
   * Adds the specified UsageRecord to the underlying UsageMap, according to
   * it's UsageFileMetadata.
   * 
   * @param metadata
   *          the metadata representing this usage.
   * @param usage
   *          the UsageRecord to be added.
   */
  public void addUsageRecord(UsageFileMetadata metadata, UsageRecord usage);
  
  /**
   * Serializes, but doesn't commit, all usages stored in this UsageHandler to
   * the underlying storage method (filesystem, database, etc). The instance map
   * will then be cleared.
   * 
   * @param overwritesBackup
   *          if <code>true</code> and a backup file already exists for an
   *          usage file, it'll first be deleted; otherwise, the current backup
   *          file will only be truncated and updated.
   * @throws IllegalArgumentException
   *           if any of the mandatory fields is missing.
   */
  public void serializeUsageMap(boolean overwritesBackup);
  
  /**
   * Serializes, but doesn't commit, all usages stored in this UsageHandler to
   * the underlying storage method (filesystem, database, etc). The instance map
   * will then be cleared.
   * 
   * <p>
   * This is the same as calling:
   * </p>
   * <pre>
   * serializeUsageMap(false);
   * </pre>
   * 
   * @throws IllegalArgumentException
   *           if any of the mandatory fields is missing.
   */
  public void serializeUsageMap();
  
  /**
   * Commits any pending usage files stored in this usage handler.
   * 
   * <p>
   * Any non-existant usage backup files will be ignored (nothing to commit).
   * </p>
   * 
   * @return all files that failed to commit.
   */
  public List<File> commitTransaction();
  
  /**
   * Discards all usage files pending for commit by reinitializing the
   * transaction.
   * 
   * @see {@link #initTransaction()}
   */
  public void rollbackTransaction();
  
  /**
   * Serializes and commits the specified usage's content to the underlying
   * storage method (filesystem, database, etc).
   * 
   * <p>
   * Mandatory {@link UsageRecord} fields:
   * <ul>
   * <li><code>startSecondsWithinDay</code></li>
   * </ul>
   * </p>
   * <p>
   * How and where the serialization is performed is implementation-specific.
   * </p>
   * 
   * @param metadata
   *          the metadata of the usage's unique {@link UsageFile}.
   * @param usage
   *          the usage to serialize.
   * @throws IllegalArgumentException
   *           if any of the mandatory fields is missing.
   */
  public void serialize(UsageFileMetadata metadata, UsageRecord usage);
  
  /**
   * Serializes and commits all usages to the underlying storage method
   * (filesystem, database, etc).
   * 
   * <p>
   * Mandatory {@link UsageRecord} fields:
   * <ul>
   * <li><code>startSecondsWithinDay</code></li>
   * </ul>
   * </p>
   * <p>
   * How and where the serialization is performed is implementation-specific.
   * </p>
   * 
   * @param usages
   *          a map of {@link UsageRecord} objects to serialize - the key is the
   *          metadata of the usage set's unique {@link UsageFile}.
   * @throws IllegalArgumentException
   *           if any of the mandatory fields is missing.
   */
  public void serialize(Map<UsageFileMetadata,Set<UsageRecord>> usages);
  
  /**
   * Serializes and commits all usages to the underlying storage method
   * (filesystem, database, etc).
   * 
   * <p>
   * Mandatory {@link UsageRecord} fields:
   * <ul>
   * <li><code>startSecondsWithinDay</code></li>
   * </ul>
   * </p>
   * <p>
   * How and where the serialization is performed is implementation-specific.
   * </p>
   * 
   * @param metadata
   *          the metadata of the usages' unique {@link UsageFile}.
   * @param usages
   *          a set of {@link UsageRecord} objects to serialize.
   * @throws IllegalArgumentException
   *           if any of the mandatory fields is missing.
   */
  public void serialize(UsageFileMetadata metadata, Set<UsageRecord> usages);
  
  /**
   * Searches for an existing usage with the same attributes as the specified
   * one (duplicated).
   * 
   * <p>
   * Details on how the search is conducted is implementation-specific.
   * 
   * @param metadata
   *          the metadata of the usage's unique {@link UsageFile}.
   * @param usage
   *          the usage to search for.
   * @return the usage found (never the same instance as the given usage),
   *         <code>null</code> otherwise.
   */
  public UsageRecord search(UsageFileMetadata metadata, UsageRecord usage);

}
