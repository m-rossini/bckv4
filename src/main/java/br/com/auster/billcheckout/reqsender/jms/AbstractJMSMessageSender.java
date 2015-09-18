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
 * Created on 19/01/2007
 */
package br.com.auster.billcheckout.reqsender.jms;

import java.io.Serializable;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.com.auster.billcheckout.reqsender.exceptions.SendMessageException;

/**
 * @author mtengelm
 *
 */
public abstract class AbstractJMSMessageSender {
	private Logger log = Logger.getLogger(AbstractJMSMessageSender.class);
	protected String jmsFactory;

	public String getJMSFactoryName() {
  	return jmsFactory;
  }

	public void connect(String _queueName) throws NamingException, JMSException, SendMessageException {
  	connect(null, _queueName);
  }

	public void connect(Hashtable _properties, String _queueName) throws NamingException, JMSException, SendMessageException {
  
  	Context jndiContext = null;
  	try {
  		if (_properties != null) {
  			jndiContext = new InitialContext(_properties);
  		} else {
  			jndiContext = new InitialContext();
  		}
  	} catch (Throwable e) {
  		log.fatal("Error connecting to JMS Server.");
  		throw new SendMessageException(e);
  	}
  	this.buildSender(jndiContext, _queueName);  
  }
	
	public abstract void buildSender(Context jndiContext, String _destinationName) throws JMSException, NamingException;
	public abstract void sendMessage(Serializable _message) throws JMSException;
}