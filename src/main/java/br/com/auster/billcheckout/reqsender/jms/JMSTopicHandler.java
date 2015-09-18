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

import java.util.concurrent.BlockingQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import br.com.auster.common.util.I18n;
import br.com.auster.billcheckout.reqsender.RequestSender;

/**
 * @author mtengelm
 * 
 */
public class JMSTopicHandler implements MessageListener {
	private static Logger log = Logger.getLogger(JMSTopicHandler.class);

	private static I18n i18n = I18n.getInstance(RequestSender.class);

	private BlockingQueue queue;

	/***
	 * This constructor is intended to be used by apps that do NOT requires
	 * blocking notification.
	 * If you need it , please use the appropriatted constructor
	 *
	 */
	public JMSTopicHandler() {
		this.queue = null;
	}

	/***
	 * The queue to be notifid.
	 * The callers is responsible to provide  the queue.
	 * The onMessage method will just put on this blocking queue
	 * @param queue
	 */
	public JMSTopicHandler(BlockingQueue queue) {
		this.queue = queue;
	}

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message msg) {
		log.info("Message has arrived:" + msg);
		if (queue != null) {
			log.info("Putting message onf the Synchronous queue.");
			try {
				queue.put(msg);
				log.info("Correlation ID:" + msg.getJMSCorrelationID());
				log.info("Destination ID:" + msg.getJMSDestination());
				log.info("Message ID:" + msg.getJMSMessageID());
				log.info("Redelivery Indicator:" + msg.getJMSRedelivered());
				log.info("Reply To:" + msg.getJMSReplyTo());
				log.info("TimeStamp:" + msg.getJMSTimestamp());
			} catch (InterruptedException e) {
				log.error("Error during queue put wait.");
				e.printStackTrace();
			} catch (JMSException e) {
				log.error("Error during queue put wait.");
				e.printStackTrace();
			}
		}
	}

}
