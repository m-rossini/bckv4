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
package br.com.auster.billcheckout.consequence.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import br.com.auster.om.reference.PKEnabledEntity;

/**
 * @author framos
 * @version $Id$
 */
public interface ReadOnlyDimensionDAO {

	/**
	 * Returns the unique identifier from the specified object, loading it from the database if
	 * 	not in cache yet. 
	 * 
	 * @param _conn
	 * @param _dimensionObj
	 * @return
	 * @throws SQLException
	 */
	public long getUID(Connection _conn, PKEnabledEntity _dimensionObj) throws SQLException;
	
	/**
	 * Resets the statement and connection objects
	 * @param _conn
	 * @throws SQLException
	 */
	public void reset(Connection _conn) throws SQLException;

	/**
	 * Identifies if the current connection is closed. If no connection was already specified to this
	 * 	dao (using the {@link #reset(Connection)} method, then this method will return <code>true</code>.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public boolean isConnectionClosed() throws SQLException;
	
	/**
	 * Returns all rows of the object this dao handles 
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Collection<PKEnabledEntity> selectAll() throws SQLException;
	
}
