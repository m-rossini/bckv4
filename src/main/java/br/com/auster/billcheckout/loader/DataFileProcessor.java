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
 * Created on 21/10/2006
 */
package br.com.auster.billcheckout.loader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import br.com.auster.common.sql.SQLConnectionManager;
import br.com.auster.common.xml.DOMUtils;

/**
 * @author pvieira
 *
 */
public abstract class DataFileProcessor {

	private static final String CONFIG_FILE = "bck-config.xml";
	private static final String POOL_NAME = "sql/billcheckoutdb";
	
	private Connection conn;

	public void setConnection(Connection conn) {
		this.conn = conn;
	}
	
	public Connection getConnection() {
		return this.conn;
	}

	protected void configure() throws Exception {

			init(getXMLConfig(CONFIG_FILE, "false"));
			this.conn = SQLConnectionManager.getInstance(POOL_NAME).getConnection();
			this.conn.setAutoCommit(false);
	}

	
	public void processData(List<DataFileVO> dataFileList) {

		try {
			this.configure();

			for (Iterator<DataFileVO> it = dataFileList.iterator(); it.hasNext();) {
				DataFileVO dataVO = it.next();
				insertData(dataVO);
			}
			this.conn.commit();
		} catch (Exception e) {
			try {
				this.conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				this.conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	protected abstract void insertData(DataFileVO dataVO) throws SQLException;

	/**
	 * Helper method that creates the DOM tree root from the given configuration
	 * filename.
	 * 
	 * @param configFilename
	 *          the configuration path and filename.
	 * @param decryptFileFlag
	 *          <code>'true'</code> if configuration file must be decrypted,
	 *          <code>'false'</code> otherwise.
	 */
	public final Element getXMLConfig(String configFilename, String isEncryptedFileFlag)
			throws Exception {

		boolean isEncrypted = Boolean.valueOf(isEncryptedFileFlag).booleanValue();

		return DOMUtils.openDocument(CONFIG_FILE, isEncrypted);
	}

	/**
	 * This method is called before any Data-Aware initialization
	 * and gives implementation classes a chance to manipulate/use
	 * the Data-Aware config element.
	 * 
	 * This implementation initializes the SQLConnectionManager if
	 * the sql:configuration element is found.
	 * 
	 * @param config the Data-Aware config element.
	 */
	public void init(Element config) throws Exception {
		Element sql = DOMUtils.getElement(config, 
				SQLConnectionManager.SQL_NAMESPACE_URI,
				SQLConnectionManager.CONFIG_ELEMENT,
				false);
		if (sql != null) {
			SQLConnectionManager.init(config);
		}
	}


}
