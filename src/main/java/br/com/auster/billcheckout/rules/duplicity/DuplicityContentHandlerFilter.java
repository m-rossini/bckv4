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
 * Created on 30/01/2007
 */
package br.com.auster.billcheckout.rules.duplicity;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;
import br.com.auster.dware.filter.ContentHandlerFilter;
import br.com.auster.dware.graph.DefaultFilter;
import br.com.auster.dware.graph.FilterException;
import br.com.auster.dware.graph.Request;

/**
 * Filter used to load usage records to the filesystem.
 * <p>Configuration:</p>
 * <pre>
 * Element "duplicity-content-handler": 
 *   Contains the configuration for the implementing class of this filter.
 *   Attributes:
 *    - "class-name": fully-qualified name of the implementing class.
 * </pre>
 *
 * @author rbarone
 * @version $Id$
 */
public class DuplicityContentHandlerFilter extends DefaultFilter {
  
  /**
   * {@value}
   */
  public static final String CONTENT_HANDLER_ELEMENT = "duplicity-content-handler";

  /**
   * {@value}
   */
  public static final String CLASS_NAME_ATTR = "class-name";
  
  protected UsageHandler usages;

  protected DuplicityContentHandler handler;
  
  private final static Logger log = Logger.getLogger(ContentHandlerFilter.class);
  
  public DuplicityContentHandlerFilter(String name) throws Exception {
    super(name);
    this.usages = UsageHandlerFactory.getUsageHandlerInstance();
  }
  
  /**
   * Configures this filter.
   */
  public synchronized void configure(Element config) throws FilterException {
    // Gets the content handler to understand the SAX events
    try {
      this.handler = getContentHandlerInstance(config);
    } catch (Throwable e) {
      throw new FilterException(e);
    }
  }
  
  /**
   * Given a DuplicityContentHandler configuration, creates an instance of it and returns
   * it.
   * 
   * @param config
   *          the DOM tree corresponding to the ContentHandler configuration to
   *          be passed to its constructor.
   * @return a ContentHandler instance based on the information of the given
   *         config element.
   */
  private DuplicityContentHandler getContentHandlerInstance(Element config)
      throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
      IllegalAccessException, java.lang.reflect.InvocationTargetException {
    config = DOMUtils.getElement(config, CONTENT_HANDLER_ELEMENT, true);
    String className = DOMUtils.getAttribute(config, CLASS_NAME_ATTR, true);

    Class[] c = { Element.class, DuplicityContentHandlerFilter.class };
    Object[] o = { config, this };
    return (DuplicityContentHandler) Class.forName(className).getConstructor(c).newInstance(o);
  }

  /**
   * Gets the ContentHandler.
   */
  public Object getInput(String sourceName) {
    return this.handler;
  }
  
  @Override
  public void prepare(Request request) throws FilterException {
    this.usages.initTransaction();
  }
  
  @Override
  public void commit() {
    this.usages.commitTransaction();
    this.usages.initTransaction();
  }
  
  @Override
  public void rollback() {
    this.usages.initTransaction();
  }
  
  public void serialize() {
    this.usages.serializeUsageMap();
  }
  
  public void addUsageRecord(UsageFileMetadata metadata, UsageRecord usage) {
    this.usages.addUsageRecord(metadata, usage);
  }

}
