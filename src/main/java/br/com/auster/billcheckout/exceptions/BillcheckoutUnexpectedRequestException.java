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
 * Created on 04/04/2007
 */
package br.com.auster.billcheckout.exceptions;


/**
 * TODO What this class is responsible for
 *
 * @author mtengelm
 * @version $Id$
 * @since 04/04/2007
 */
public class BillcheckoutUnexpectedRequestException extends
		BillcheckoutException {

	/**
	 * Creates a new instance of the class <code>BillcheckoutUnexpectedRequestException</code>.
	 */
	public BillcheckoutUnexpectedRequestException() {
		super();
	}

	/**
	 * Creates a new instance of the class <code>BillcheckoutUnexpectedRequestException</code>.
	 * @param arg0
	 * @param arg1
	 */
	public BillcheckoutUnexpectedRequestException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Creates a new instance of the class <code>BillcheckoutUnexpectedRequestException</code>.
	 * @param arg0
	 */
	public BillcheckoutUnexpectedRequestException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new instance of the class <code>BillcheckoutUnexpectedRequestException</code>.
	 * @param arg0
	 */
	public BillcheckoutUnexpectedRequestException(Throwable arg0) {
		super(arg0);
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
