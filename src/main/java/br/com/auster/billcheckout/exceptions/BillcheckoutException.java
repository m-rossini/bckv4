/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
 * Created on 28/02/2007
 */
package br.com.auster.billcheckout.exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import br.com.auster.common.io.IOUtils;

/**
 * @author mtengelm
 *
 */
// TODO Comment this class br.com.auster.billcheckout.exceptions "." BillcheckoutException.java
public class BillcheckoutException extends Exception {

	private long errorCode;
	
	private static final String propertiesFilePath = "billcheckoutExceptionCodes.properties";
	private static Properties prop = null;
	private static Lock wLock = new ReentrantReadWriteLock().writeLock();
	private static Lock rLock = new ReentrantReadWriteLock().readLock();	
	
	public static long getCommonErrorCode(String className) {
		try {
			if (prop != null) {
				return getFromLoadedProperties(className);
			} 
			wLock.lock();
			prop = new Properties();		
			InputStream is= BillcheckoutException.class.getResourceAsStream(propertiesFilePath);
//			 = IOUtils.openFileForRead(propertiesFilePath, false);
			prop.load(is);
			return getFromLoadedProperties(className);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BillcheckoutRuntimeException("Error while loading properties file.");
		} finally {
			wLock.unlock();
		}
	}
	
	private static long getFromLoadedProperties(String className) {
		String temp = prop.getProperty(className);
		if (temp == null || temp.equals("")) {
			return Long.MAX_VALUE;
		}
		return Long.parseLong(temp);
	}
	/**
	 * 
	 */
	public BillcheckoutException() {
	}

	/**
	 * @param arg0
	 */
	public BillcheckoutException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public BillcheckoutException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BillcheckoutException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * TODO why this methods was overriden, and what's the new expected behavior.
	 * 
	 * @return
	 * @see br.com.auster.billcheckout.exceptions.BillcheckoutException#getErrorCode()
	 */
	public long getErrorCode() {
		return BillcheckoutException.getCommonErrorCode(this.getClass().getName());
	}
	
	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}
}
