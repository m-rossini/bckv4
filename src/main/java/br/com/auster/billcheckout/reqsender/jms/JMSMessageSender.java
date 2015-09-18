/*
 * Copyright (c) 2004 TTI Tecnologia. All Rights Reserved.
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
 * Created on Jun 10, 2005
 */
package br.com.auster.billcheckout.reqsender.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * @author framos
 * @version $Id: JMSMessageSender.java 35 2005-06-18 13:37:50Z framos $
 */
public class JMSMessageSender extends AbstractJMSMessageSender {

	private Logger log = Logger.getLogger(JMSMessageSender.class);
	
	private QueueConnection	queueConnection;
	private QueueSession	  queueSession;
	private QueueSender	    queueSender;

	/**
	 * 
	 */
	public JMSMessageSender() {
	}
	
	public JMSMessageSender(String _factory) {
		jmsFactory = _factory;
	}

	public void sendMessage(Serializable _message) throws JMSException {
		if ((queueConnection == null) || (queueSession == null) || (queueSender == null)) {
			throw new IllegalStateException("cannot execute send before connecting to the JMS provider");
		}
		ObjectMessage msg = queueSession.createObjectMessage();
		queueConnection.start();
		msg.setObject(_message);
		queueSender.send(msg);
	}

	/* (non-Javadoc)
   * @see br.com.auster.vivo.billcheckout.cmdline.JMSMessageSenderBase#buildSender(javax.naming.Context, java.lang.String)
   */
  @Override
  public void buildSender(Context jndiContext, String _destinationName) throws JMSException, NamingException {
  	QueueConnectionFactory ref = (QueueConnectionFactory) jndiContext.lookup(jmsFactory);
  	Queue queue = (Queue) jndiContext.lookup(_destinationName);
  
  	queueConnection = ref.createQueueConnection();
  	queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
  	queueSender = queueSession.createSender(queue);
	  
  }

}
