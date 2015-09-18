/*
 *
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
 * Created on 05/03/2007
 *
 * @(#)DeleteWebRequestBatch.java 05/03/2007
 */
package br.com.auster.billcheckout.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import br.com.auster.common.io.IOUtils;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * 
 * The class <code>DeleteWebRequestBatch</code> it is responsible to delete a
 * WebRequests marked´s whith a REQUEST_STATUS = 199 calling a
 * CLEANUP_WEB_REQUEST procedure.
 * 
 * @author Gilberto Brandão
 * @version $Id$
 * @since JDK1.4
 */
public class DeleteWebRequestBatch {

	private static Logger								log;
	private static I18n									i18n										= I18n
																																	.getInstance(DeleteWebRequestBatch.class);

	// public static final String ARGS_DELETE_CLASS_LEVEL = "delete-class";
	// public static final String ARGS_DELETE_CLASS_MNEMONIC = "c";

	public static final String					ARGS_SQL_CONF_LEVEL			= "sql-configuration";
	public static final String					ARGS_SQL_CONF_MNEMONIC	= "q";

	public static final String					ARGS_HELP_LEVEL					= "help";
	public static final String					ARGS_HELP_MNEMONIC			= "h";

	// private static final String JOCL_CONF = "tiamat-sql-connection.xml";
	// private static final String TIAMAT_POOL_NAME = "sql/tiamat";

	private String											sqlConfigFile;
	// private String className;
	private static SQLConnectionManager	man											= null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Init logging
		try {
			initLogging(args[0]);
		} catch (ParserConfigurationException e1) {
			exitWithCode(1, e1);
		} catch (SAXException e1) {
			exitWithCode(2, e1);
		} catch (IOException e1) {
			exitWithCode(3, e1);
		} catch (GeneralSecurityException e1) {
			exitWithCode(4, e1);
		} catch (Exception e1) {
			exitWithCode(5, e1);
		}

		printArgs(args);

		// Init Class
		DeleteWebRequestBatch webRequestBatch = new DeleteWebRequestBatch();

		// Create and parse command line options
		Options options = webRequestBatch.createOptions();
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;

		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			exitWithCode(5, e);
		}

		// Handle Options
		if (line.hasOption(ARGS_HELP_MNEMONIC)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(DeleteWebRequestBatch.class.getName(), options);
		}

		try {
			webRequestBatch.handleOptions(line);
		} catch (NumberFormatException ex) {
			exitWithCode(6, ex);
		} catch (java.text.ParseException ex) {
			exitWithCode(7, ex);
		}

		webRequestBatch.printEffectiveOptions();

		// Connection
		try {
			initSQLConnection(line.getOptionValue(ARGS_SQL_CONF_MNEMONIC));
		} catch (Exception e) {
			exitWithCode(3, e);
		}
		// chama os metodos de deleção
		try {
			deleteAllWebRequest();

		} catch (Exception e) {
			exitWithCode(6, e);
		}
	}

	/**
	 * 
	 * Is responsible in
	 * 
	 * @param line
	 * @throws java.text.ParseException
	 */
	protected void handleOptions(CommandLine line)
			throws java.text.ParseException {

		this.sqlConfigFile = line.getOptionValue(ARGS_SQL_CONF_MNEMONIC);
		// this.className = line.getOptionValue(ARGS_DELETE_CLASS_MNEMONIC);
	}

	/**
	 * 
	 * Print options.
	 * 
	 */
	protected void printEffectiveOptions() {
		log.info(i18n.getString("delete.loader.parameters.inuse.q",
				this.sqlConfigFile));
		// log.info(i18n.getString("delete.loader.parameters.inuse.c",
		// this.className));
	}

	/**
	 * 
	 * This method is responsible in to make a parse of the configuration file and
	 * get a instance of Logger.
	 * 
	 * @param configFile
	 *          name of the SQLConnectionPool configuration file
	 * @throws Exception
	 *           A Exception occours
	 */

	private static void initLogging(String filePath) throws ParserConfigurationException,
			SAXException, IOException, GeneralSecurityException {
		LogFactory.configureLogSystem(DOMUtils.openDocument(IOUtils
				.openFileForRead(filePath)));
		log = LogFactory.getLogger(DeleteWebRequestBatch.class);
	}

	/**
	 * 
	 * This method is responsible in to make a parse of the configuration file and
	 * get a instance of SQLConnectionMenager.
	 * 
	 * @param configFile
	 *          name of the SQLConnectionPool configuration file
	 * @throws Exception 
	 * @throws Exception
	 *           A Exception occours
	 */
	private static void initSQLConnection(String configFile)
			throws Exception {

		try {
			Element config = DOMUtils.openDocument(configFile, false);
			// Element sql = DOMUtils.getElement(config,
			// SQLConnectionManager.SQL_NAMESPACE_URI,
			// SQLConnectionManager.CONFIG_ELEMENT, true);
			// if (sql != null) {
			// SQLConnectionManager.init(config);
			// }

			Element cfgElemt = DOMUtils.getElement(config, "configuration", true);
			Element dbElmt = DOMUtils.getElement(cfgElemt, "database", true);
			String poolName = DOMUtils.getAttribute(dbElmt, "name", true);
			SQLConnectionManager.init(config);
			man = SQLConnectionManager.getInstance(poolName);

		} catch (FileNotFoundException e) {
			throw e;
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (SAXException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (GeneralSecurityException e) {
			throw e;
		} catch (NamingException e) {
			throw e;
		} 
	}

	/**
	 * 
	 * This method is responsible in to get a connection in the pool.
	 * 
	 * @return Connection a connection of the pool
	 * @throws Exception
	 *           Any exception occours
	 */
	private static Connection getConnection() throws Exception {
		Connection conn = null;

		try {
			conn = man.getConnection();
		} catch (Exception e) {
			throw e;
		}
		return conn;
	}

	/**
	 * 
	 * Create Options obeject.
	 * 
	 * @return Options
	 */
	public Options createOptions() {

		OptionBuilder.withArgName(ARGS_SQL_CONF_LEVEL);
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(true);
		OptionBuilder.withDescription(i18n
				.getString("delete.loader.parameters.help.q"));
		Option sqlConf = OptionBuilder.create(ARGS_SQL_CONF_MNEMONIC);

		OptionBuilder.withArgName(ARGS_HELP_LEVEL);
		OptionBuilder.withDescription(i18n
				.getString("delete.loader.parameters.help.h"));
		Option helpOption = OptionBuilder.create(ARGS_HELP_MNEMONIC);

		Options options = new Options();
		options.addOption(sqlConf);
		options.addOption(helpOption);

		return options;
	}

	protected static void exitWithCode(int code, Throwable e) {
		String msg = getMessage(code);
		// if (code > 4) {
		// log.fatal(msg,e);
		// log.info(i18n.getString("delete.loader.0",code));
		// }
		System.out.println(msg + '\n' + e);
		System.out.println(i18n.getString("delete.loader.0", code));
		System.exit(code);
	}

	protected static String getMessage(int code) {
		return i18n.getString("delete.loader.error." + Integer.toString(code));
	}

	private static void printArgs(String[] args) {
		log.info(i18n.getString("delete.loader.parameters.1"));
		StringBuilder sb = new StringBuilder();
		sb.append(':');
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			sb.append(' ');
		}
		log.info(sb.toString());
	}

	/**
	 * 
	 * List´s all web_request marked whith REQUEST_STATUS= 199.
	 * 
	 * @param conn
	 *          one connection of the pool
	 * @return List A List whith webrequest ids to be deleted
	 * @throws SQLException
	 *           If occours any Exception
	 */
	private static List getWebRequestFromDelete(Connection conn)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List webReqs = new ArrayList();

		try {
			String sql = "select * from WEB_REQUEST where REQUEST_STATUS = 199";
			log.debug("Executing the research of  web request to be deleted " + sql);
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			long reqId;
			while (rs.next()) {
				reqId = rs.getLong(1);
				webReqs.add(reqId);
			}
		} catch (SQLException e) {
			log
					.error(
							"An exception occours while a researsh of web request to be deleted ",
							e);
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			log.debug("closing connection ");
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw sqle;
			}
		}
		return webReqs;
	}

	/**
	 * This method is responsible in delete a web request calling a
	 * cleanup_billcheckout procedure.
	 * 
	 * @param conn
	 *          one connection of the pool
	 * @param reqId
	 *          Unique identifier web request
	 * @throws SQLException
	 *           If occours any Exception
	 */
	private static void deleteWebRequest(Connection conn, long reqId)
			throws SQLException {
		CallableStatement cstmt = null;
		boolean success;
		try {
			log.info("Starting to delete from database the transaction:" + reqId);
			cstmt = conn.prepareCall("{call cleanup_billcheckout(?)}");
			cstmt.setLong(1, reqId);
			cstmt.execute();
			log.info("Transaction deleted succssfully:" + reqId);

		} catch (SQLException e) {
			log
					.error(
							"An exception occours while a procedure cleanup_billcheckout to be called ",
							e);

			throw e;
		} finally {
			if (cstmt != null) {
				cstmt.close();
			}
			log.debug("closing connection ");
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw sqle;
			}
		}
	}

	/**
	 * 
	 * This method is responsible in to list all web requests merked whith status
	 * to delete, delete files associated and registers in the table.
	 * 
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void deleteAllWebRequest() throws SQLException, Exception {

		try {
			List webRequests = getWebRequestFromDelete(getConnection());
			StringBuffer requestList = new StringBuffer("");
			for (Iterator it = webRequests.iterator(); it.hasNext();) {
				requestList.append((Long) it.next());
				requestList.append(",");
			}
			
			if(requestList.length() > 0) {
				requestList.deleteCharAt(requestList.length()-1);
			}
			
			String cycleUidFromBckConsequence = "";
			
			if(requestList.length() > 0){
				cycleUidFromBckConsequence = getCycleUidFromBckConsequence(getConnection(), requestList.toString());
			}
			
			for (Iterator it = webRequests.iterator(); it.hasNext();) {
				long reqId = (Long) it.next();
				log.info(">>>Starting to remove transaction:" + reqId + " <<<");
				// delete todos os arquivos
				deleteAllWebRequestFiles(reqId);
				// metodo que chama a procedure para delete os registros
				deleteWebRequest(getConnection(), reqId);
			}
			log.info("Finished processing for all marked for delete transactions.");
			
			log.info("Processing BCK_CYCLE_DM.");
			
			int totalLines = 0;
			if(cycleUidFromBckConsequence.length() > 0) {
				totalLines = removeCycleDm(getConnection(), cycleUidFromBckConsequence);
			}
			log.info("Finished processing BCK_CYCLE_DM. Result of deleted lines: "+totalLines);
			
		} catch (Exception e) {
			log.error("Error while deleting web request ");
			throw e;
		}
	}

	private static String getCycleUidFromBckConsequence(Connection connection, String requestList) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer cycleUid = new StringBuffer("");
		
		try {
			StringBuffer sql = new StringBuffer("select distinct cycle_uid from bck_consequence where transaction_id in (");
			sql.append(requestList);
			sql.append(")");
			log.debug("Executing the research of bck consequence to be deleted " + sql);
			stmt = connection.prepareStatement(sql.toString());
			rs = stmt.executeQuery();
			while (rs.next()) {
				cycleUid.append(rs.getLong(1));
				cycleUid.append(",");
			}
			if(cycleUid.length() > 0){
				cycleUid.deleteCharAt(cycleUid.length()-1);
			}
			
			log.debug("Result of Cycles to be deleted " + cycleUid.toString());
		} catch (SQLException e) {
			log.error("An exception occours while a researsh of bck consequence to be deleted ",e);
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			log.debug("closing connection ");
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException sqle) {
				throw sqle;
			}
		}
		return cycleUid.toString();
	}

	private static int removeCycleDm(Connection connection, String cycleUidFromBckConsequence) throws SQLException {
		String property = System.getProperty("dateLimit");
		int totalLines = 0;
		StringBuffer sql = new StringBuffer("DELETE BCK_CYCLE_DM WHERE OBJID IN (");
		sql.append(cycleUidFromBckConsequence);
		sql.append(")");
		
		log.debug("Executing the delete of bck cycle dm to be deleted " + sql);
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql.toString());
			totalLines = ps.executeUpdate(sql.toString());
			connection.commit();
		} catch (SQLException e) {
			log.error("An exception occours while a delete of bck cycle dm to be deleted ",e);
			connection.rollback();
			throw e;
		} finally {
			log.debug("closing connection ");
			if(ps != null) {
				ps.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
		
		return totalLines;
	}
	
	/**
	 * This method is responsible in to list a web-bundle-files.
	 * 
	 * @param conn
	 *          one connection of the pool
	 * @param reqId
	 *          Unique identifier web request
	 * @return webBundleFiles List whith a GenericFileVO
	 * @throws SQLException
	 *           If occours any Exception
	 */
	private static List getWebBundleFiles(Connection conn, long reqId)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List webBundleFiles = new ArrayList();
		GenericFileVO genericFileVO;
		log.trace("Building bundle file list for transaction:" + reqId);
		try {
			String sql = "select * from web_bundlefile where web_request_id = ? ";
			log.debug("Executing the research of  web-bundle-file to be deleted "
					+ sql);
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, reqId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				genericFileVO = new GenericFileVO();
				genericFileVO.setPath(rs.getString(3));
				genericFileVO.setFileId(rs.getLong(1));
				webBundleFiles.add(genericFileVO);
			}
		} catch (SQLException e) {
			log
					.error(
							"An exception occours while a researsh of web-bundle-files to be deleted ",
							e);
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			log.debug("closing connection ");
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw sqle;
			}
		}
		return webBundleFiles;
	}

	/**
	 * This method is responsible in to list a proc_request_outfile.
	 * 
	 * @param conn
	 *          one connection of the pool
	 * @param fileId
	 *          Unique identifier file
	 * @return webBundleFiles List whith a GenericFileVO
	 * @throws SQLException
	 *           If occours any Exception
	 */
	private static List getProcRequestOutfile(Connection conn, long fileId)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List procReqOutfile = new ArrayList();
		GenericFileVO genericFileVO;
		try {
			String sql = "select * from proc_request_outfile where file_id = ?";
			log
					.debug("Executing the research of  proc_request_outfile to be deleted "
							+ sql);
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, fileId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				genericFileVO = new GenericFileVO();
				genericFileVO.setPath(rs.getString(3));
				genericFileVO.setFileId(rs.getLong(1));
				procReqOutfile.add(genericFileVO);
			}
		} catch (SQLException e) {
			log
					.error(
							"An exception occours while a researsh of proc_request_outfile to be deleted ",
							e);
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			log.debug("closing connection ");
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw sqle;
			}
		}
		return procReqOutfile;
	}

	/**
	 * 
	 * This method Is responsible in delete a file specified path.
	 * 
	 * @param path
	 *          path of the archive
	 * @throws If
	 *           any Exception occours
	 */
	private static void deleteFile(String path) throws Exception {

		try {
			File file = new File(path);
			log.trace("Deleting file:" + file);
			if (file != null && file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			log.error("error occurred while deleting a file in the path [" + path
					+ "]", e);
			throw e;
		}
	}

	/**
	 * 
	 * Is responsible in delte all files to the web_request.
	 * 
	 * @param reqId
	 *          Unique identifier web_request_id
	 * 
	 */
	private static void deleteAllWebRequestFiles(long reqId) throws Exception {
		List webBundleFiles;
		List procRequestOutfile;
		GenericFileVO bundlefile;
		GenericFileVO outfile;
		log.info("Starting to delete physical plain files for transaction:" + reqId);
		try {
			// lista todos os web_bundlefile relacionados ao requestId informado
			webBundleFiles = getWebBundleFiles(getConnection(), reqId);
			// percorre os web_bundlefiles
			for (Iterator itBundlefiles = webBundleFiles.iterator(); itBundlefiles
					.hasNext();) {
				bundlefile = (GenericFileVO) itBundlefiles.next();
				// delete web_bundlefile
				deleteFile(bundlefile.getPath());
				// lista proc_request_outfile
				procRequestOutfile = getProcRequestOutfile(getConnection(), bundlefile
						.getFileId());
				// percorre a lista de proc_request_outfile
				for (Iterator itOutfile = procRequestOutfile.iterator(); itOutfile
						.hasNext();) {
					outfile = (GenericFileVO) itOutfile.next();
					// delete proc_request_outfile
					deleteFile(outfile.getPath());
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			log.info("Process of file delete has ended for transaction:" + reqId);
		}
	}

}