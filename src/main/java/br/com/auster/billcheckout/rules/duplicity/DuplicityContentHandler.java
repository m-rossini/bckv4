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
 * Created on 26/01/2007
 */
package br.com.auster.billcheckout.rules.duplicity;

import java.lang.ref.WeakReference;

import org.w3c.dom.Element;

import br.com.auster.common.xml.sax.SAXStylesheet;

/**
 * Abstract base class for duplicity's usage files serialization through an
 * {@link DuplicityContentHandlerFilter}.
 * <p>
 * Implementations os this class must call
 * {@link DuplicityContentHandlerFilter#addUsageRecord(UsageFileMetadata, UsageRecord)}
 * to add each {@link UsageRecord} with a specific {@link UsageFileMetadata}.
 * </p>
 * After finished, the implementation must call
 * {@DuplicityContentHandlerFilter#serialize()} - this abstract class
 * automatically calls this method when {@code onEndDocument()} is called (if
 * you dont't want this behaviour, override <code>onEndDocument()</code>.
 * 
 * @author rbarone
 * @version $Id$
 */
public abstract class DuplicityContentHandler extends SAXStylesheet {
  
  protected WeakReference<DuplicityContentHandlerFilter> filter;
  
  public DuplicityContentHandler(Element config, DuplicityContentHandlerFilter filter) {
    super(config);
    if (filter == null) {
      throw new IllegalArgumentException("DuplicityContentHandlerFilter cannot be null.");
    }
    this.filter = new WeakReference<DuplicityContentHandlerFilter>(filter);
  }
  
  /**
   * Calls {@link DuplicityContentHandlerFilter#serialize()}. If you override
   * this method, remember to call <code>super.onEndDocument()</code>,
   * otherwise the filter won't be serialized.
   */
  public void onEndDocument() {
    this.filter.get().serialize();
  }
  
}
