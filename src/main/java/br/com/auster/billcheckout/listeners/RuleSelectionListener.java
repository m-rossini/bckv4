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

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
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
public class RuleSelectionListener extends ResumeRequestsBootstrapListener {



	private static final Logger log = Logger.getLogger(RuleSelectionListener.class);


	protected static final String USER_EMAIL_PROP = "billcheckout.transaction.owner";


	protected static final String DB_ELEMENT = "database";
	protected static final String POOL_NAME_ATTR = "pool-name";
	protected static final String CONFIG_ELEMENT = "config";

	private static final int REQUEST_PROCESSING = 2;

	private static final String CYCLE_CODE_PROP = "cycle.id";
	private static final String REQUEST_SIZE_PROP = "request.size";


	public static final String RULES_LIST_QUERY = "SELECT DISTINCT PACKAGE_ID "
		+ "FROM BCK_RULE_LIST  "
		+ "WHERE PACKAGE_ID IN (SELECT PACKAGE_UID FROM BCK_RULE_GROUP_LIST "
		+ "                    WHERE GROUP_UID = (SELECT GROUP_ID  "
		+ "                                       FROM BCK_RULE_GROUP "
		+ "                                       WHERE RUNNABLE_FLG = 'X')) "
		+ "  OR PACKAGE_ID IN (SELECT PACKAGE_ID   "
		+ "                      FROM BCK_RULE_LIST "
		+ "                      WHERE ALWAYS_RUN_FLG = 'X') ";
	
	
	public static final String RULES_EXEC_HIST_QUERY = "INSERT INTO bck_rule_exec_hist (\"REQUEST_UID\",\"PACKAGE_UID\") VALUES (?,?)";
	
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
		try {
			Request req = (Request) _requests.iterator().next();
			if (req.getTransactionId() != null) {
				log.info("Updating Rules Execution History for request=" + req.getTransactionId());
				List<String> rulesList = getRulesList();
				createExecRulesHistory(req.getTransactionId(), rulesList);
			}
		} catch (SQLException e) {
			log.error("SQL Error while Updating Rules Execution History." + e);
		} 
	}

	protected List<String> getRulesList() throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		List<String> rulesList = new ArrayList<String>();
		try {
			conn = sqlMan.getConnection();
			st = conn.prepareStatement(RULES_LIST_QUERY);
			rs = st.executeQuery();
			while (rs.next()) {
				String packageName = rs.getString("PACKAGE_ID");
				rulesList.add(packageName);
			}
		} finally {
			try {
				if (rs != null) { rs.close(); }
				if (st != null) { st.close(); }
				if (conn != null) {	conn.close(); }
			} catch (SQLException e) {
				log.error("Error while running query to retrive rules list: " + e);
			}
		}
		return rulesList;
	}

	protected void createExecRulesHistory(String reqId, List<String> rulesList) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = sqlMan.getConnection();
			stmt = conn.prepareStatement(RULES_EXEC_HIST_QUERY);
			for (String ruleId : rulesList) {
				stmt.setString(1, reqId);
				stmt.setString(2, ruleId);
				stmt.executeUpdate();
			}

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
