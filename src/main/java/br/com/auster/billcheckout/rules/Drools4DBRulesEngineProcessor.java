/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 10/04/2006
 */
package br.com.auster.billcheckout.rules;

import java.io.File;
import java.io.FileFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class works like Drools4DirectoriesRulesEngineProcessor. 
 * The main difference is that it retrieves the information of the 
 * packages to process from the database.
 * 
 * @author gportuga
 * @version $Id$
 */
public class Drools4DBRulesEngineProcessor extends AbstractRulesEngineProcessor {
	private static final Logger log = Logger.getLogger(Drools4DBRulesEngineProcessor.class);

	public static final String PACKAGES_LIST_HIST_QUERY = "SELECT DISTINCT PACKAGE_ID, RELATIVE_BASE, MASK " +
															"FROM BCK_RULE_EXEC_HIST H, BCK_RULE_LIST L " +
															"WHERE  H.PACKAGE_UID = L.PACKAGE_ID " +
															"AND REQUEST_UID = ? ";
	
	public static final String MOST_RECENT_REQUEST_QUERY = "SELECT REQUEST_UID FROM BCK_RULE_EXEC_HIST " +
															"ORDER BY REQUEST_UID DESC";

	private static String poolName;

	public Drools4DBRulesEngineProcessor(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.billcheckout.drools.AbstractRulesEngineProcessor#buildRulesList(org.w3c.dom.Element)
	 */
	@Override
	protected Map<String, RulePackageSourceHook> buildRulesList(Element config) {
		Map<String, RulePackageSourceHook> results = new HashMap<String, RulePackageSourceHook>();

		Element rulesDB = DOMUtils.getElement(config, "RuleDBConfig", true);
		this.poolName = DOMUtils.getAttribute(rulesDB, "pool-name", true);
		
		if (poolName == null) {
			log.debug("No pool configurated for Package List retrieving.");
			throw new RuntimeException("No pool configurated for Package List retrieving.");
		}
		
		try {
			return refreshRulesList(null);
		} catch (Exception e) {
			log.error("Error while updating Package List.");
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method retrieve Packages list from the pool specified.
	 * 
	 * @param poolName
	 * @return an map of <String, File[]>
	 */
	public Map<String, File[]> getPackagesList(String reqId) {
		Map<String, File[]> packagesMap = new HashMap<String, File[]>();

		if (log.isDebugEnabled()) {	log.debug("Retrieving Package List from database pool:" + poolName); }

		SQLConnectionManager sqlMan;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			sqlMan = SQLConnectionManager.getInstance(this.poolName);
			connection = sqlMan.getConnection();

			pstmt = connection.prepareStatement(PACKAGES_LIST_HIST_QUERY);
			pstmt.setString(1, reqId);
			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				String packageName = resultSet.getString("PACKAGE_ID");
				String relativeBase = resultSet.getString("RELATIVE_BASE");
				String mask = resultSet.getString("MASK");
				
				// check for null fields
				if (packageName != null && relativeBase != null && mask != null) {
					File[] files = getFiles(new File(relativeBase), mask);
					if (files != null) {
						packagesMap.put(packageName, files);
					} else {
						log.info("No files found for: " + relativeBase + mask);
					}
				}
			}
		} catch (SQLException e) {
			log.error("Error running query to retrieve Packages List", e);
			throw new RuntimeException("Error running query to retrieve Packages List", e);
		} catch (NamingException e) {
			log.error("Error connecting to database pool: " + poolName, e);
			throw new RuntimeException("Error connecting to database pool: " + poolName, e);
		} finally {
			 try {
				 if (connection != null) { connection.close(); }
				 if (pstmt != null) { pstmt.close(); }
				 if (resultSet != null) { resultSet.close(); }
			} catch (SQLException e) { /* do nothing */ } 
		}

		return packagesMap;
	}
	
	public String getMostRecentRequest() {
		if (log.isDebugEnabled()) {	log.debug("Retrieving the most recent RequestId from database pool:" + poolName); }

		SQLConnectionManager sqlMan;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			sqlMan = SQLConnectionManager.getInstance(this.poolName);
			connection = sqlMan.getConnection();

			pstmt = connection.prepareStatement(MOST_RECENT_REQUEST_QUERY);
			resultSet = pstmt.executeQuery();
			
			if (resultSet.next()) {
				String reqId = resultSet.getString("REQUEST_UID");
				log.info("Most recent RequestId returned: " + reqId);
				return reqId;
			}
			throw new RuntimeException("Could not retrieve the most recent RequestId to process");
		} catch (SQLException e) {
			log.error("Error running query to retrieve the most recent RequestId", e);
			throw new RuntimeException("Error running query to retrieve the most recent RequestId", e);
		} catch (NamingException e) {
			log.error("Erro ao conectar-se ao database pool: " + poolName, e);
			throw new RuntimeException("Erro ao conectar-se ao database pool: " + poolName, e);
		} finally {
			 try {
				 if (connection != null) { connection.close(); }
				 if (pstmt != null) { pstmt.close(); }
				 if (resultSet != null) { resultSet.close(); }
			} catch (SQLException e) { /* do nothing */ } 
		}
	}


	/**
	 * Helper method to get an array of files given a File representing a base directory and a string
	 * representing the WildCard for file name existing in the base directory
	 * 
	 * If base or mask is null, then it returns null.
	 * For a better understanding of base, mask and how the search is done, see
	 * apache common-io WildcardFileFilter class.
	 * 
	 * @param base
	 * @param drlMask
	 * @return an array of files
	 */
	protected File[] getFiles(File base, String mask) {
		if (mask == null || base == null) {
			return null;
		}
		FileFilter filter = new WildcardFileFilter(mask);
		return base.listFiles(filter);
	}

	protected Map<String, RulePackageSourceHook> refreshRulesList(String reqId) {
		Map<String, RulePackageSourceHook> results = new HashMap<String, RulePackageSourceHook>();
		
		if (reqId == null) {
			log.info("No RequestId set. Retrieving the most recent one.");
			reqId = getMostRecentRequest();
		}
		Map<String, File[]> packagesMap = getPackagesList(reqId);
		
		Iterator<Map.Entry<String, File[]>> it = packagesMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, File[]> packageInfo = it.next();
			File [] packageFiles = packageInfo.getValue();

			RulePackageSourceHook hook = new RulePackageSourceHook();
			for (int j = 0; j < packageFiles.length; j++) {
				log.info("Adding a DRL File named: " + packageFiles[j]);
				hook.addDrlFile(packageFiles[j]);
			}

			results.put(packageInfo.getKey(), hook);
		}

		return results;
	}

	@Override
	public void refresh(String reqId, Map<String, Object> map) {
		try {
			this.rulesList = refreshRulesList(reqId);
			init(map);
		} catch (Exception e) {
			log.error("Error while refreshing rules. Unable to re-create RuleBase.");
			throw new RuntimeException("Error while refreshing rules. Unable to re-create RuleBase.", e);
		}
	}

}
