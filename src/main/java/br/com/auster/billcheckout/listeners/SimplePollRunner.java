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
 * Created on 19/12/2006
 */
package br.com.auster.billcheckout.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import br.com.auster.billcheckout.exceptions.BillcheckoutQueryNotFoundException;
import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.common.sql.SQLStatement;

/**
 * @author mtengelm
 * $Id$
 */
public class SimplePollRunner implements Runnable {

	
	public static final String TRANSACTION_FINISHED_UPDATE_SQL = "TransactionFinishedUpdate";
	public static final String REQUEST_FINISHED_SQL = "RequestsFinished";
	
	public static final String	CONFIGURATION_JMS_CONNFACTORY	= "jms.contextFactory";
	public static final String	CONFIGURATION_JMS_TOPICNAME	  = "jms.topicName";
	
	
	private static final Logger log = Logger.getLogger(SimplePollRunner.class);

	
	private String poolName;
	private long transactionId;
	private Properties	jmsProperties;
	private int pollInterval;
	private int maxAttempts;

	
	
	public SimplePollRunner(String _transactionId, String _poolName, Properties _jmsProperties, int _pollInterval, int _maxAttempts) {	
		this.poolName = _poolName;
		this.transactionId = Long.parseLong(_transactionId);
		this.jmsProperties = _jmsProperties;
		this.pollInterval = _pollInterval;
		this.maxAttempts = _maxAttempts;
	}

	/**
	 * @see java.util.concurrent.Callable#call()
	 */
	public boolean finished() throws Exception {
		int status = checkFinished();
		if (status > 0) {
			this.updateTransactionStatus(status);
			this.sendMessageToTopic(status);
		}
		return (status > 0);
	}


	protected int checkFinished() throws Exception {
	
		String query = this.findStatement(REQUEST_FINISHED_SQL);
		if (query == null) {
			throw new BillcheckoutQueryNotFoundException("Asking for statement named:" + REQUEST_FINISHED_SQL + ", but it does no exists.");
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		int finalStatus = 0;
		try {
			log.debug("Runnig query " + query);
			conn = this.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setLong(1, this.transactionId);
			rset = stmt.executeQuery();
			long totalCounter = 0;
			Map<Long, Long> counterMap = new HashMap<Long, Long>();
			while (rset.next()) {
				long latestStatus = rset.getLong(1);
				long requestCouter = rset.getLong(2);
				counterMap.put(new Long(latestStatus), new Long(requestCouter));
				totalCounter += requestCouter;
			}
			// if this next IF is true, then the transaction id is not correctly set in the database
			if (counterMap.size() == 0) {
				finalStatus = -1;
				return finalStatus;
			}
			log.debug("Counter map dump." + counterMap);
			log.info("Total requests on this transaction is " + totalCounter);
			long unCounter = getUnFinishedCounter(counterMap);
			log.debug("Unfinished requests counter is " + unCounter + ". Request processing is done? " + (unCounter == 0));
			if (unCounter == 0) {
				// FINISHED OK
				finalStatus = 3; 
				if (getFinishedWithErrorCounter(counterMap) > 0) {
					// FINISHED NOT OK.					
					finalStatus = 4; 
				}
			}
		} catch (SQLException e) {
			log.error("Error during SQL Processing for query:[" + query + "].");
			throw e;
		} finally {
			try {
				if (rset != null) { rset.close(); }
				if (stmt != null) { stmt.close(); }
				if (conn != null) { conn.close(); }
			} catch (SQLException e) {
				log.error("Problems releasing resources for query.", e);
			}
		}
		return finalStatus;
	}

	protected void updateTransactionStatus(int _status) throws Exception {		
		String query = this.findStatement(TRANSACTION_FINISHED_UPDATE_SQL);
		if (query == null) {
			throw new BillcheckoutQueryNotFoundException("Asking for statement named:" + TRANSACTION_FINISHED_UPDATE_SQL + ", but it does no exists.");
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, _status);
			stmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			stmt.setLong(3, this.transactionId);
			stmt.executeUpdate();
			conn.commit();
			log.debug("Transaction id " + this.transactionId + " information updated with status " + _status);
		} finally {
			if (stmt != null) { stmt.close(); }
			if (conn != null) { conn.close(); }
		}

	}

	protected void sendMessageToTopic(int _status) throws JMSException, NamingException {
		String factory = (String) this.jmsProperties.get(CONFIGURATION_JMS_CONNFACTORY);
		String name = (String) this.jmsProperties.get(CONFIGURATION_JMS_TOPICNAME);
		log.debug("Config: JMSFactory:[" + factory + "]. JMSName:[" + name + "]");
		TopicConnection tcon = null;
		TopicSession tsession = null;
		TopicPublisher tpublisher = null;
		try {
			Context ctx = new InitialContext(this.jmsProperties);
			// getting connection facotry
			TopicConnectionFactory tconFactory = (TopicConnectionFactory) ctx.lookup(factory);
			tcon = tconFactory.createTopicConnection();
			log.debug("TopicConnection created.");
			// connecting to the JMS provider
			tsession = tcon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			log.debug("TopicSession created. Looking up [" + name + "]");
			Topic topic = (Topic) ctx.lookup(name);
			log.debug("Topic created.");
			tpublisher = tsession.createPublisher(topic);
			log.debug("TopicPublisher created.");
			// building message
			ObjectMessage msg = tsession.createObjectMessage();
			log.debug("ObjectMessage created. Type:" + msg.getJMSType());
			tcon.start();
			log.debug("Connection started.");
			// builing message
			msg.setLongProperty("transactionId", this.transactionId);
			msg.setIntProperty("status", _status);
			// sending message
			tpublisher.publish(msg);
			log.info("Message sent for transaction id " + this.transactionId + " with status " + _status);
		} catch (Exception e) {
			log.error("Error while sending notification that requestId "+ this.transactionId + " finished", e); 
		} finally {
			if (tpublisher != null) { tpublisher.close(); }
			if (tsession != null) { tsession.close(); }
			if (tcon != null) { tcon.close(); }
		}
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			int counter=0;
			while (! this.finished()) {
				if (counter++ > this.maxAttempts) {
					log.warn("Maximum attempts to check finish status reached. Transaction id " + this.transactionId + " will not have its status updated.");
					break;
				}
				log.debug("Checking for finished status with transaction id " + this.transactionId + ". Interaction number " + counter);
				Thread.sleep(this.pollInterval);
			}
			if (counter <= this.maxAttempts) {
				log.info("Finished status detected for transaction " + this.transactionId);
			}
		} catch (Exception e) {
			log.info("Error during runner execution... skipped.", e	);
			throw new RuntimeException(e);
		}
	}
	

	
	private String findStatement(String _stmtName) throws NamingException {
		SQLConnectionManager sqlMan = SQLConnectionManager.getInstance(this.poolName);
		if (sqlMan == null) { return null; }
		SQLStatement stmt = sqlMan.getStatement(_stmtName);
		if (stmt == null) { return null; }
		return stmt.getStatementText();
	}
	
	private Connection getConnection() throws NamingException, SQLException {
		SQLConnectionManager sqlMan = SQLConnectionManager.getInstance(this.poolName);
		if (sqlMan == null) { return null; }
		return sqlMan.getConnection();
	}	
	
	private long getFinishedWithErrorCounter(Map map) {
		Long temp = (Long) map.get(new Long(4));
		return (temp == null) ? 0 : temp.longValue();
	}

	private long getUnFinishedCounter(Map map) {
		long unFinishedCounter = 0;
		// Created
		Long temp = (Long) map.get(new Long(1));
		unFinishedCounter += (temp == null) ? 0 : temp.longValue();
		// Running
		temp = (Long) map.get(new Long(2));
		unFinishedCounter += (temp == null) ? 0 : temp.longValue();
		return unFinishedCounter;
	}

	

}
