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
 * Created on 25/09/2007
 */
package br.com.auster.billcheckout.rules;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.auster.billcheckout.reqsender.RequestSender;
import br.com.auster.common.util.I18n;

/**
 * This is a thin wrapper for DRL Source code handling.
 * 
 * This class provides a base file entry point and two Lists of File objects.
 * 
 * One list if for DRL Files and the other for DSL Files.
 * 
 * This class is intended to be used for feeding Rule Base with one reader only,
 * thru enumeration and SequenceInputStream.
 *
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class RulePackageSourceHook {
	private static I18n i18n = I18n.getInstance(RulePackageSourceHook.class);
	private Logger log = Logger.getLogger(RulePackageSourceHook.class);
	private List<File> drlList = new ArrayList<File>();
	private List<File> dslList = new ArrayList<File>();
	private File base;
	
	/**
	 * Creates a new instance of the class <code>RulePackageSourceHook</code>.
	 */
	public RulePackageSourceHook() {		
	}
	
	/***
	 * 
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * </p>
	 * 
	 * @param file
	 */
	public void addDrlFile(File file) {
		log.trace("Adding DRL file named:" + file.getAbsolutePath());
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			log.error(i18n.getString("drl.file.notexist", file.getAbsoluteFile()));
			return;
		}
		this.drlList.add(file);
	}
	
	/***
	 * 
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * </p>
	 * 
	 * @param fileName
	 */
	public void addDrlFile(String fileName) {
		if ( (fileName == null) || "".equals(fileName)) {
			return;
		}
		this.addDrlFile(new File(fileName));
	}
	
	/***
	 * 
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * </p>
	 * 
	 * @param file
	 */
	public void addDslFile(File file) {
		log.trace("Adding DSL file named:" + file.getAbsolutePath());
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			log.error(i18n.getString("dsl.file.notexist", file.getAbsoluteFile()));
			return;
		}
		this.dslList.add(file);
	}
	
	/***
	 * 
	 * TODO what this method is responsible for
	 * <p>
	 * Example:
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * </p>
	 * 
	 * @param fileName
	 */
	public void addDslFile(String fileName) {
		if ( (fileName == null) || "".equals(fileName)) {
			return;
		}
		this.addDslFile(new File(fileName));
	}
	
	/**
	 * Return the value of a attribute <code>drlList</code>.
	 * @return return the value of <code>drlList</code>.
	 */
	public List<File> getDrlList() {
		Collections.sort(this.drlList);
		return this.drlList;
	}
	
	/**
	 * Set the value of attribute <code>drlList</code>.
	 * @param drlList
	 */
	public void setDrlList(List<File> drlList) {
		this.drlList = drlList;
	}
	
	/**
	 * Return the value of a attribute <code>dslList</code>.
	 * @return return the value of <code>dslList</code>.
	 */
	public List<File> getDslList() {
		Collections.sort(this.dslList);		
		return this.dslList;
	}
	
	/**
	 * Set the value of attribute <code>dslList</code>.
	 * @param dslList
	 */
	public void setDslList(List<File> dslList) {
		this.dslList = dslList;
	}
	
	/**
	 * Return the value of a attribute <code>base</code>.
	 * @return return the value of <code>base</code>.
	 */
	public File getBase() {
		return this.base;
	}
	
	/**
	 * Set the value of attribute <code>base</code>.
	 * @param base
	 */
	public void setBase(File base) {
		this.base = base;
	}

}
