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
import java.util.concurrent.BlockingQueue;

import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;


/**
 * @author framos
 * @version $Id: JMSMessageSender.java 35 2005-06-18 13:37:50Z framos $
 */
public class JMSMessageTopicSender extends AbstractJMSMessageSender {

	private Logger	        log	= Logger.getLogger(JMSMessageTopicSender.class);
	private TopicConnection	topicConnection;
	private JMSTopicHandler	handler;
	private BlockingQueue syncronizer;

	// private QueueConnection queueConnection;
	// private QueueSession queueSession;
	// private QueueSender queueSender;

	/**
	 * 
	 */
	public JMSMessageTopicSender() {
	}

	public JMSMessageTopicSender(String _factory) {
		jmsFactory = _factory;
	}

	public JMSMessageTopicSender(BlockingQueue syncronizer) {
		this.syncronizer = syncronizer;
	}


	public JMSMessageTopicSender(String _factory,BlockingQueue syncronizer) {
		this.syncronizer = syncronizer;
		jmsFactory = _factory;		
	}

	public void sendMessage(Serializable _message) throws JMSException {
		throw new UnsupportedOperationException(
		    "Send message for sending Topic Messages is not yet supported by this class.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.vivo.billcheckout.cmdline.JMSMessageSenderBase#buildSender(javax.naming.Context,
	 *      java.lang.String)
	 */
	@Override
	public void buildSender(Context jndiContext, String _destinationName) throws JMSException,
	    NamingException {
		TopicConnectionFactory connectionFactory = (TopicConnectionFactory) jndiContext
		    .lookup(jmsFactory);
		Topic topic = (Topic) jndiContext.lookup(_destinationName);
		topicConnection = connectionFactory.createTopicConnection();
		
		topicConnection.setExceptionListener(new JMSExceptionListener(syncronizer));
		
		ConnectionMetaData data = topicConnection.getMetaData();
		printInfo(data);

		TopicSession topicSession = topicConnection
		    .createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		
		TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);
		handler = new JMSTopicHandler(syncronizer);
		topicSubscriber.setMessageListener(handler);

		log.info("Listening for messages on topic...");
		topicConnection.start();

	}

	/**
   * @param data
   */
  private void printInfo(ConnectionMetaData data) {
  	try {
	    log.debug("JMS Major Version:" + data.getJMSMajorVersion());
	    log.debug("JMS Minor Version:" + data.getJMSMinorVersion());
	    log.debug("JMS Version:" + data.getJMSVersion());
	    log.debug("JMS Provider Name:" + data.getJMSProviderName());
	    log.debug("JMS Provider Major Version:" + data.getProviderMajorVersion());
	    log.debug("JMS Provider Minor Version:" + data.getProviderMinorVersion());
	    log.debug("JMS Provider Version:" + data.getProviderVersion());	    
    } catch (JMSException e) {
    	log.info("Unable to get Topic Connection Metadata.");
    }
	  
  }

}
