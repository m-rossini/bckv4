/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
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
 * Created on 02/11/2006
 */
package br.com.auster.billcheckout.filter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;

import br.com.auster.billcheckout.consequence.Consequence;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.dware.graph.DefaultFilter;
import br.com.auster.dware.graph.FilterException;
import br.com.auster.dware.graph.ObjectProcessor;

/**
 * @author framos
 * @version $Id$
 */
public class JDBCBillcheckoutPersistenceFilter extends DefaultFilter implements ObjectProcessor {

	
	
	public static final String INPUTMAP_LISTKEY_ATTR = "input-list-tag";
	public static final String JDBC_POOLNAME_ATTR  = "pool-name";
	public static final String JDBC_COMMIT_ATTR = "commit-count";

	private String listKey;
	private String poolName;
	private int commitCount;
	
	
	
	public JDBCBillcheckoutPersistenceFilter(String _name) {
		super(_name);
	}


	public void configure(Element _configuration) throws FilterException {
		this.listKey = DOMUtils.getAttribute(_configuration, INPUTMAP_LISTKEY_ATTR, true);
		this.poolName = DOMUtils.getAttribute(_configuration, JDBC_POOLNAME_ATTR, true);
		this.commitCount = DOMUtils.getIntAttribute(_configuration, JDBC_COMMIT_ATTR, false);
		if (this.commitCount <= 0) {
			this.commitCount = 100;
		}
	}
	
	public void processElement(Object _objects) throws FilterException {

		try {
			if ((_objects instanceof Map) && ((this.listKey != null) && (this.listKey.trim().length() > 0))) {
				saveObject( ((Map)_objects).get(this.listKey) );
			} else {
				saveObject( _objects );
			}
		} catch (SQLException sqle) {
			throw new FilterException(sqle);
		}
	}

	protected void saveObject(Object _object) throws SQLException {
		// logging object classname and content
		int counter=0;
		Connection conn = null;
		try {
			Collection objCollection;
			if (!(_object instanceof Collection)) {
				objCollection = new ArrayList();
				objCollection.add(_object);
			} else {
				objCollection = (Collection) _object;
			}
			
			for (Iterator it = objCollection.iterator(); it.hasNext(); ) {
				Consequence persistedObject = (Consequence) it.next();
				if (checkDimensions(conn, persistedObject) > 0) {
					counter += saveConsequence(conn, persistedObject);
				}
				// commit interval check
				if (counter == this.commitCount) {
					commitSession(conn);
					counter = 0;
				}
			}
		} finally {
			if (conn != null) { 
				commitSession(conn); 
				conn.close(); 
			}
		}
	}
	
	protected void commitSession(Connection _conn) throws SQLException {
		_conn.commit();
	}
	
	protected int checkDimensions(Connection _conn, Consequence _consequence) throws SQLException {
		return 0;
	}
	
	protected int saveConsequence(Connection _conn, Consequence _consequence) throws SQLException {
		return 0;
	}
	
}
