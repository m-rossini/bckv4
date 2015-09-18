/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
 * Created on 08/03/2007
 */
package br.com.auster.billcheckout.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.billcheckout.exceptions.BillcheckoutDataSourceConfigurationException;
import br.com.auster.billcheckout.exceptions.BillcheckoutException;
import br.com.auster.billcheckout.exceptions.BillcheckoutRuntimeException;
import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.common.stats.ProcessingStats;
import br.com.auster.common.stats.StatsMapping;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.dware.Bootstrap;
import br.com.auster.dware.graph.Request;
import br.com.auster.dware.listeners.resume.ResumeRequestsBootstrapListener;

/**
 * TODO What this class is responsible for
 *
 * @author mtengelm
 * @version $Id$
 * @since 08/03/2007
 */
public class BootstrapListener extends ResumeRequestsBootstrapListener {



	private static final Logger log = Logger.getLogger(BootstrapListener.class);


	protected static final String USER_EMAIL_PROP = "billcheckout.transaction.owner";


	protected static final String DB_ELEMENT = "database";
	protected static final String POOL_NAME_ATTR = "pool-name";
	protected static final String CONFIG_ELEMENT = "config";

	private static final int REQUEST_PROCESSING = 2;

	private static final String CYCLE_CODE_PROP = "cycle.id";
	private static final String REQUEST_SIZE_PROP = "request.size";


	protected static final String SELECT_WEB_USER =
		"select user_id from web_user where user_email = ?";

	protected static final String UPDATE_WEB_REQUEST_STATUS =
		"update web_request set request_status = " + REQUEST_PROCESSING + " where request_id = ?";

	protected static final String INSERT_WEB_REQUEST =
		"insert into web_request values " + "(WEB_REQUEST_SEQUENCE.nextval, ? , ? , ? , ?)";

	protected static final String INSERT_WEB_REQUEST_INFO = " insert into web_request_info values (?, ? , ?)";



	protected String poolName;
	protected SQLConnectionManager sqlMan;

	/**
	 * TODO why this methods was overriden, and what's the new expected
	 * behavior.
	 *
	 * @param _configuration
	 * @see br.com.auster.dware.BootstrapListener#configure(org.w3c.dom.Element)
	 */
	public void configure(Element _configuration) {
		super.configure(_configuration);
		try {
			configDatabase(_configuration);
		} catch (BillcheckoutException e) {
			BillcheckoutRuntimeException rte = new BillcheckoutRuntimeException();
			rte.setErrorCode(e.getErrorCode());
			rte.initCause(e);
			throw rte;
		}
	}

	/**
	 * TODO why this methods was overriden, and what's the new expected
	 * behavior.
	 *
	 * @param _instance
	 * @param _chainName
	 * @param _args
	 * @param _desiredRequests
	 * @param _requests
	 * @see br.com.auster.dware.BootstrapListener#onProcess(br.com.auster.dware.Bootstrap,
	 *      java.lang.String, java.util.Map, java.util.List,
	 *      java.util.Collection)
	 */
	public void onProcess(Bootstrap _instance, String _chainName, Map _args, List _desiredRequests, Collection _requests) {
		super.onProcess(_instance, _chainName, _args, _desiredRequests, _requests);
		try {
			String cycleCode = getCycleCode(_requests);

			Map<String, String> transactionProperties = new HashMap<String, String>();
			if (cycleCode != null) {
				transactionProperties.put(CYCLE_CODE_PROP, cycleCode);
			}
			transactionProperties.put(REQUEST_SIZE_PROP, String.valueOf(_requests.size()));

			Request req = (Request) _requests.iterator().next();
			log.debug("TransactioID=" + req.getTransactionId());
			if (req.getTransactionId() == null) {
				long tid = createTransactionDataForUser(getUserID(), transactionProperties);
				log.debug("Updating transaction requests.");
				updateTransactionRequests(_requests, tid);
			}
			log.debug("Creating transaction requests.");
			createTransactionInfoForTransaction(Long.parseLong(req.getTransactionId()), transactionProperties);
			updateTransactionToProcessingStatus(req.getTransactionId());
		} catch (SQLException e) {
			log.error(e.toString(), e);
		} catch (BillcheckoutException e) {
			log.error(e.toString(), e);
		}
		log.trace("Created a new Batch Transaction.");
	}

	/**
	 * Gets the cycle information from the first request in the request collection to be posted to DWare
	 */
	protected String getCycleCode(Collection<Request> reqs) throws BillcheckoutException {
		String results = (String) reqs.iterator().next().getAttributes().get(CYCLE_CODE_PROP);
		return results;
	}

	protected void updateTransactionRequests(Collection<Request> reqs, long tid)
			throws BillcheckoutException {
		for (Iterator<Request> it = reqs.iterator(); it.hasNext();) {
			Request request = it.next();
			if (request.getTransactionId() == null) {
				request.setTransactionId(String.valueOf(tid));
			} else {
				log.warn("TransactionID " + request.getTransactionId() + " was found in Request. TransactionID "
						  + tid + " will not be set.");
			}
		}
	}

	/**
	 * Tries to find the user account in WEB_USER table. If the user does not exist,
	 *   or if the user account was not properly configured, it should raise an exception
	 *   and no transaction should be created.
	 */
	protected long getUserID() throws SQLException, BillcheckoutException {
		String email = System.getProperty(USER_EMAIL_PROP);
		if (email != null && !email.equals("")) {
			// If no e-mail Set, returns default
			Connection conn = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			StatsMapping stats = ProcessingStats.starting(getClass(), "getUserID()");
			try {
				conn = sqlMan.getConnection();
				st = conn.prepareStatement(SELECT_WEB_USER);
				st.setString(1, email);
				rs = st.executeQuery();
				if (rs.next()) {
					return rs.getLong(1);
				}
			} finally {
				stats.finished();
				try {
					if (rs != null) { rs.close(); }
					if (st != null) { st.close(); }
					if (conn != null) { conn.close(); }
				} catch (SQLException e) {
					log.error("Error while releaseing database resources on acessing user data for:" + email);
				}
			}
		}
		throw new BillcheckoutException("Could not find user account information.");
	}

	protected long createTransactionDataForUser(long userID, Map<String, String> transactionProperties)
		throws SQLException {

		long tid = -1;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = sqlMan.getConnection();
			stmt = conn.prepareStatement(INSERT_WEB_REQUEST, new String[] { "REQUEST_ID" });
			Date date = new Date();
			stmt.setTimestamp(1, new Timestamp(date.getTime()));
			stmt.setInt(2, REQUEST_PROCESSING);
			stmt.setNull(3, Types.TIMESTAMP);
			stmt.setLong(4, 1);
			stmt.executeUpdate();
			ResultSet keys = stmt.getGeneratedKeys();
			keys.next();
			tid = keys.getLong(1);
			log.info("Generated Transaction ID (Web Request ID) :" + tid);
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
		} finally {
			if (stmt != null) { stmt.close(); }
			if (conn != null) { conn.close(); }
		}
		return tid;
	}

	protected void createTransactionInfoForTransaction(long tid, Map<String, String> transactionProperties)
			throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = sqlMan.getConnection();
			stmt = conn.prepareStatement(INSERT_WEB_REQUEST_INFO);
			for (String key : transactionProperties.keySet()) {
				String value = transactionProperties.get(key);
				try {
					stmt.setLong(1, tid);
					stmt.setString(2, key);
					stmt.setString(3, value);
					stmt.executeUpdate();
					stmt.clearParameters();
					log.info("Created request attribute for key " + key + " with value " + value);
				} catch (SQLException sqle) {
					log.warn("Could not insert attribute " + key + " due to the following error.", sqle);
				}
			}
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
		} finally {
			if (stmt != null) { stmt.close(); }
			if (conn != null) { conn.close(); }
		}
	}

	/**
	 * Marks the web request as processing (status '2')
	 */
	protected void updateTransactionToProcessingStatus(String _transactionId) throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = sqlMan.getConnection();
			stmt = conn.prepareStatement(UPDATE_WEB_REQUEST_STATUS);
			stmt.setLong(1, Long.parseLong(_transactionId));
			stmt.executeUpdate();
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
		} finally {
			if (stmt != null) { stmt.close(); }
			if (conn != null) { conn.close(); }
		}
	}

	protected void configDatabase(Element config) throws BillcheckoutException {
		Element configElmt = DOMUtils.getElement(config, CONFIG_ELEMENT, true);
		Element dbElement = DOMUtils.getElement(configElmt, DB_ELEMENT, true);
		if (null == dbElement) {
			return;
		}

		poolName = DOMUtils.getAttribute(dbElement, POOL_NAME_ATTR, true);
		if ((poolName == null) || (poolName.trim().length() == 0)) {
			throw new BillcheckoutRuntimeException("pool-name was not informed.");
		}

		try {
			sqlMan = SQLConnectionManager.getInstance(poolName);
		} catch (NamingException e) {
			BillcheckoutException ex = new BillcheckoutDataSourceConfigurationException("Unable to find the pool informed:" + poolName);
			ex.initCause(e);
			throw ex;
		}
	}
}
