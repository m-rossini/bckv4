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
 * Created on 07/02/2007
 */
package br.com.auster.billcheckout.listeners;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.billcheckout.exceptions.BillcheckoutRuntimeException;
import br.com.auster.common.io.IOUtils;
import br.com.auster.common.stats.ProcessingStats;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.dware.manager.QueueProcessedListener;

/**
 * @author mtengelm
 *
 */
public class BillcheckoutQueueProcessedListener implements QueueProcessedListener {



	private static final Logger log = Logger.getLogger(BillcheckoutQueueProcessedListener.class);

	protected static final String CONFIG_ELMT = "config";
	protected static final String THREAD_SIZE_ATTR = "thread-size";
	protected static final String MAX_ATTEMPTS_ATTR = "thread-size";
	protected static final String POLL_INTERVAL_ATTR = "pool-timer";
	protected static final String JMS_INFO_ATTR = "jms-path";
	protected static final String DBS_POOL_NAME = "pool-name";

	// Default pooling interval in milliseconds
	protected static final int DEFAULT_POLL_INTERVAL = 60000;
	protected static final int DEFAULT_THREAD_SIZE = 1;
	protected static final int DEFAULT_MAX_ATTEMPTS = 10;


	private int pollingInterval;
	private Properties jmsProperties;
	private String dbPool;
	private int maxAttempts;



	/**
	 * @see br.com.auster.dware.manager.QueueProcessedListener#init(org.w3c.dom.Element)
	 */
	public void init(Element _config) {
		try {
			Element configuration = DOMUtils.getElement(_config, CONFIG_ELMT, true);
			this.pollingInterval = DOMUtils.getIntAttribute(configuration, POLL_INTERVAL_ATTR, false);
			if (this.pollingInterval < 1000) {
				this.pollingInterval = DEFAULT_POLL_INTERVAL;
			}
			log.info("Poll interval set for " + this.pollingInterval + " miliseconds.");
			// jms properties
			String filename = DOMUtils.getAttribute(configuration, JMS_INFO_ATTR, true);
			this.jmsProperties = new Properties();
			this.jmsProperties.load(IOUtils.openFileForRead(filename));
			log.info("JMS properties defined at file " + filename);
			// database pool
			this.dbPool = DOMUtils.getAttribute(configuration, DBS_POOL_NAME, true);
			log.info("Database pool set to " + this.dbPool);
			// thread pool size
			int threadSize = DOMUtils.getIntAttribute(configuration, THREAD_SIZE_ATTR, false);
			if (threadSize <= 0) {
				threadSize = DEFAULT_THREAD_SIZE;
			}
			log.info("Runner pool size set to " + threadSize);
			this.maxAttempts = DOMUtils.getIntAttribute(configuration, MAX_ATTEMPTS_ATTR, false);
			if (this.maxAttempts <= 0) {
				this.maxAttempts = DEFAULT_MAX_ATTEMPTS;
			}
			log.info("Max. number of attempts is " + this.maxAttempts);
		} catch (Exception e) {
			throw new BillcheckoutRuntimeException("Could not initialize queue listener", e);
		}
	}

	/**
	 * @see br.com.auster.dware.manager.QueueProcessedListener#onQueueProcessed(java.lang.String, int)
	 */
	public void onQueueProcessed(String _transactionId, int _size) {
		log.info("Transaction id " + _transactionId + " with total number of requests of " + _size + " has finished.");
		ProcessingStats.dumpAllStats();
		SimplePollRunner simplePool = new SimplePollRunner(_transactionId, this.dbPool, this.jmsProperties , this.pollingInterval, this.maxAttempts);
		(new Thread(simplePool)).start();
	}

}
