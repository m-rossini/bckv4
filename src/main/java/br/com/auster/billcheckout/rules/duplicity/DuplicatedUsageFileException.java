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
 * Created on 17/01/2007
 */
package br.com.auster.billcheckout.rules.duplicity;

import java.io.IOException;

/**
 * Thrown when an attempt is made to create an {@link UsageFile} that for a
 * filename that already has an {@link UsageFile} created for it.
 * 
 * @author rbarone
 * @version $Id$
 */
public class DuplicatedUsageFileException extends IOException {

  public DuplicatedUsageFileException() {
  }

  public DuplicatedUsageFileException(String message) {
    super(message);
  }

  public DuplicatedUsageFileException(Throwable cause) {
    this();
    initCause(cause);
  }

  public DuplicatedUsageFileException(String message, Throwable cause) {
    this(message);
    initCause(cause);
  }

}
