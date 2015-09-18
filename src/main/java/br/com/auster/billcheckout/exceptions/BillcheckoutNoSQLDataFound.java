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
 * Created on 08/02/2007
 */
package br.com.auster.billcheckout.exceptions;

/**
 * @author mtengelm
 *
 */
public class BillcheckoutNoSQLDataFound extends BillcheckoutException {

	/**
	 * 
	 */
	public BillcheckoutNoSQLDataFound() {
	}

	/**
	 * @param arg0
	 */
	public BillcheckoutNoSQLDataFound(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public BillcheckoutNoSQLDataFound(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BillcheckoutNoSQLDataFound(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * TODO why this methods was overriden, and what's the new expected behavior.
	 * 
	 * @return
	 * @see br.com.auster.billcheckout.exceptions.BillcheckoutException#getErrorCode()
	 */
	@Override
	public long getErrorCode() {
		return BillcheckoutException.getCommonErrorCode(this.getClass().getName());
	}
}
