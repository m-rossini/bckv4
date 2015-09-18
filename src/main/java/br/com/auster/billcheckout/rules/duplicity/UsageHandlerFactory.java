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

import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;

/**
 * Responsible for creating instances of an <code>UsageHandler</code>.
 * 
 * <p>
 * The {@link UsageHandler} implementation can be changed through the system
 * property defined by {@link #DEFAULT_FACTORY_CLASSNAME}.
 * 
 * @author rbarone
 * @version $Id$
 */
public class UsageHandlerFactory {

  /**
   * {@value}
   */
  public static final String FACTORY_PROPERTY = "br.com.auster.billcheckout.rules.duplicity.usagehandlerfactory";

  /**
   * {@value}
   */
  public static final String DEFAULT_FACTORY_CLASSNAME = DefaultUsageHandler.class.getName();

  private static final Logger log = LogFactory.getLogger(UsageHandlerFactory.class);

  public static UsageHandler getUsageHandlerInstance() throws UsageHandlerFactoryException {
    String configuredFactory = System.getProperty(FACTORY_PROPERTY, DEFAULT_FACTORY_CLASSNAME);

    try {
      return (UsageHandler) Class.forName(configuredFactory).newInstance();
    } catch (InstantiationException e) {
      log.error("Configured UsageHandler class has no empty constructors: " + configuredFactory, e);
      throw new UsageHandlerFactoryException(e);
    } catch (IllegalAccessException e) {
      log.error("Configured UsageHandler class has no accessible empty constructors: "
                + configuredFactory, e);
      throw new UsageHandlerFactoryException(e);
    } catch (ClassNotFoundException e) {
      log.error("Configured UsageHandler class was not found - check your classpath : "
                + configuredFactory, e);
      throw new UsageHandlerFactoryException(e);
    } catch (ClassCastException e) {
      log.error("Configured UsageHandler class does not implement interface "
                + UsageHandler.class.getName() + " : " + configuredFactory, e);
      throw new UsageHandlerFactoryException(e);
    }
  }

}
