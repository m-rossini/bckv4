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
 * Created on 16/01/2007
 */
package br.com.auster.billcheckout.rules.duplicity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import br.com.auster.billcheckout.rules.duplicity.DefaultUsageHandler;
import br.com.auster.billcheckout.rules.duplicity.UsageFile;
import br.com.auster.billcheckout.rules.duplicity.UsageFileMetadata;
import br.com.auster.billcheckout.rules.duplicity.UsageHandler;
import br.com.auster.billcheckout.rules.duplicity.UsageHandlerFactory;
import br.com.auster.billcheckout.rules.duplicity.UsageRecord;

/**
 * TestCase for serialization/parsing of an {@link UsageFile}.
 *
 * @author rbarone
 * @version $Id$
 */
public class UsageDuplicityTest extends TestCase {
  
  private static final UsageFileMetadata METADATA = 
    new UsageFileMetadata("ACC0001", "SUBSCR0001", 20070116);
  
  private static final File FILE = new File("teste" + METADATA.getDate() + ".usage");
  
  static {
    cleanFiles();
  }
  
  public static void cleanFiles() {
    FILE.delete();
    File backup = new File(FILE + UsageFile.BACKUP_SUFFIX);
    backup.delete();
    backup = null;
    try {
      FileUtils.forceDelete(new File("ACC0001"));
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void testSerialize() throws Exception {
    assertNotNull( serialize(FILE, METADATA) );
  }
  
  public void testParse() throws Exception {
    assertNotNull( parse(FILE) );
    cleanFiles();
  }
  
  public void testSerializeAndParseEquals() throws Exception {
    UsageRecord[] u1 = serialize(FILE, METADATA);
    UsageRecord[] u2 = parse(FILE);
    assertEquals(u1.length, u2.length);
    for (int i = 0; i < u1.length; i++) {
      assertEquals(u1[i], u2[i]);
    }
    cleanFiles();
  }
  
  public void testAddUsage() throws Exception {
    serialize(FILE, METADATA);
    
    UsageFile f = UsageFile.parse(FILE, true);
    
    UsageRecord u = new UsageRecord();
    u.setPartialSequence((short)0);
    u.setStartSecondsWithinDay((short)10);
    u.setUsageDuration(36);
    u.setFromNumber("0000000002");
    u.setToNumber("2000000002");
    final String service = "ação áéíà";
    u.setService(service);
    
    f.addUsageRecord(u);
    assertEquals(f.getUsageCount(), 2);
    assertEquals(f.getUsageCount(), f.getUsageRecords().length);
    
    UsageRecord[] u1 = f.getUsageRecords();
    
    f.getSerializer().serialize(true, true);
    f.getParser().parse();
    assertEquals(f.getUsageCount(), 2);
    assertEquals(f.getUsageCount(), f.getUsageRecords().length);
    assertEquals(f.getUsageRecords()[1].getService(), service);
    
    UsageRecord[] u2 = f.getUsageRecords();
    
    for (int i = 0; i < f.getUsageCount(); i++) {  
//      System.out.println( "[" + u2[i].getHashCode() + "];[" +
//                          u2[i].getPartialSequence() + "];[" +
//                          u2[i].getStartDateAndHour() + "];[" +
//                          u2[i].getStartSecondsWithinHour() + "];[" +
//                          u2[i].getDurationInSeconds() + "];[" +
//                          u2[i].getFromNumber() + "];[" +
//                          u2[i].getToNumber() + "];[" +
//                          u2[i].getService() + "]" );
      assertEquals(u1[i], u2[i]);
    }
    
    f.close();
    cleanFiles();
  }
  
//  public void testOpenSameUsageFile() throws Exception {
//    UsageFile f1 = UsageFile.parse(FILE, true);
////    f1.close();
//    UsageFile f2 = null;
//    try {
//      f2 = UsageFile.parse(FILE, true);
//    } catch (DuplicatedUsageFileException e) {
//      return;
//    } finally {
//      f1.close();
//      if (f2 != null) { f2.close(); }
//      cleanFiles();
//    }
//    fail("UsageFile allowed to create two instances using the same filename!");
//  }
//  
//  public void testOpenSameUsageFileAfterGC() throws Exception {
//    UsageFile f1 = UsageFile.parse(FILE, true);
//    f1 = null;
//    System.gc();
//    try {
//      UsageFile.parse(FILE, true);
//    } catch (DuplicatedUsageFileException e) {
//      fail("UsageFile was not finalized yet so another instance could not be created!");
//    } finally {
//      System.gc();
//      cleanFiles();
//    }
//  }
//  
//  public void testWithNoUsages() throws Exception {
//    try {
//      new UsageFile(FILE, METADATA, new byte[UsageFile.USAGE_MAP_FIELD_SIZE], new UsageRecord[0]);
//    } catch (Exception e) {
//      return;
//    }
//    fail("UsageFile allowed to create an instance with no UsageRecord!");
//    cleanFiles();
//  }
  
  public void testRemoveAllUsages() throws Exception {
    UsageRecord[] u = new UsageRecord[1];
    
    u[0] = new UsageRecord();
    u[0].setPartialSequence((short)0);
    u[0].setStartSecondsWithinDay((short)0);
    u[0].setUsageDuration(5);
    u[0].setFromNumber("0000000001");
    u[0].setToNumber("1000000001");
    u[0].setService("a");
    
    UsageFile f = new UsageFile(FILE, METADATA, new byte[UsageFile.USAGE_MAP_FIELD_SIZE], u);
    f.getSerializer().serialize(true, true);
    f.setUsageRecords(new UsageRecord[0]);
    f.getSerializer().serialize(true, true); // this should delete the usage file
    assertFalse(f.usageFile.exists() || f.usageFile.exists());
    f.setUsageRecords(u);
    f.getSerializer().serialize(true, true);
    assertTrue(f.usageFile.exists());
    f.close();
    cleanFiles();
  }

  public void testParseDateAndHour() {
    int date = 20061201;
    int[] correctParsedDate = new int[] { 2006, 12, 1 };
    int[] parsedDate = UsageFileMetadata.parseDate(date);
    assertNotNull(parsedDate);
    assertEquals(correctParsedDate.length, parsedDate.length);
    for (int i = 0; i < parsedDate.length; i++) {
      assertEquals(correctParsedDate[i], parsedDate[i]);
    }
  }
  
  public void testSerializeAndSearchUsingHandler() throws Exception {
    UsageHandler handler = UsageHandlerFactory.getUsageHandlerInstance();
    File f = ((DefaultUsageHandler)handler).getUsageFilePath(METADATA);
    if (f != null) {
      f.delete();
      f = null;
    }
    
    Set<UsageRecord> set = new HashSet<UsageRecord>();
    for (int i = 0; i < 20; i++) {
      UsageRecord u = new UsageRecord();
      u.setPartialSequence((short)0);
      u.setStartSecondsWithinDay((short)i);
      u.setUsageDuration(5);
      u.setFromNumber("0000000001");
      u.setToNumber("1000000001");
      u.setService("a");
      set.add(u);
    }
    
//    long time = System.nanoTime();
    handler.serialize(METADATA, set);
//    System.out.println(System.nanoTime() - time);
    
    UsageRecord dup = new UsageRecord();
    dup.setPartialSequence((short)0);
    dup.setStartSecondsWithinDay((short)19);
    dup.setUsageDuration(5);
    dup.setFromNumber("0000000001");
    dup.setToNumber("1000000001");
    dup.setService("a");
    
//    time = System.nanoTime();
    UsageRecord found = handler.search(METADATA, dup);
//    System.out.println(System.nanoTime() - time);
    assertNotNull(found);
    assertEquals(dup, found);
    assertNotSame(dup, found);
    
    dup.setStartSecondsWithinDay((short)20);
//    time = System.nanoTime();
    found = handler.search(METADATA, dup);
//    System.out.println(System.nanoTime() - time);
    assertNull(found);
    
    dup.setStartSecondsWithinDay((short)60);
//    time = System.nanoTime();
    found = handler.search(METADATA, dup);
//    System.out.println(System.nanoTime() - time);
    assertNull(found);
    
    cleanFiles();
  }
  
  private UsageRecord[] serialize(File file, UsageFileMetadata metadata) throws Exception {
    file.delete();
    
    UsageRecord[] u = new UsageRecord[1];
    u[0] = new UsageRecord();
    u[0].setPartialSequence((short)0);
    u[0].setStartSecondsWithinDay((short)0);
    u[0].setUsageDuration(5);
    u[0].setFromNumber("0000000001");
    u[0].setToNumber("1000000001");
    u[0].setService("a");
    
    UsageFile f = new UsageFile(file, metadata, null, u);
    f.getSerializer().serialize(true, true);
    f.close();
    
    return u;
  }
  
  private UsageRecord[] parse(File file) throws Exception {
    UsageFile f = UsageFile.parse(file, true);
    assertEquals(f.getUsageCount(), f.getUsageRecords().length);
    
    f.close();
    return f.getUsageRecords();
  }

}
