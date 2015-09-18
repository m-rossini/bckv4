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
 * Created on 25/08/2006
 */
package br.com.auster.billcheckout.consequence.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * @author framos
 * @version $Id$
 * 
 */
public class EnumToStringMapping<E extends Enum<E>> implements UserType {

	

	// ---------------------------
	// Instance variables
	// ---------------------------
	
	private Class<E> klazz = null;
	private static final int[] SQL_TYPES = { Types.VARCHAR };

	
	
	// ---------------------------
	// Constructor
	// ---------------------------
	
	protected EnumToStringMapping(Class<E> _klass) {
		this.klazz = _klass;
	}

	
	
	// ---------------------------
	// Public methods
	// ---------------------------
	
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public Class returnedClass() {
		return this.klazz;
	}

	public Object nullSafeGet(ResultSet _resultSet, String[] _names, Object _owner) throws HibernateException, SQLException {
		String name = _resultSet.getString(_names[0]);
		E result = null;
		if (!_resultSet.wasNull()) {
			result = Enum.valueOf(this.klazz, name);
		}
		return result;
	}

	public void nullSafeSet(PreparedStatement _stmt, Object _value, int _index) throws HibernateException, SQLException {
		if (null == _value) {
			_stmt.setNull(_index, Types.VARCHAR);
		} else {
			_stmt.setString(_index, ((Enum) _value).name());
		}
	}

	public Object deepCopy(Object _value) throws HibernateException {
		return _value;
	}

	public boolean isMutable() {
		return false;
	}

	public Object assemble(Serializable _cached, Object _owner) throws HibernateException {
		return _cached;
	}

	public Serializable disassemble(Object _value) throws HibernateException {
		return (Serializable) _value;
	}

	public Object replace(Object _original, Object _target, Object _owner) throws HibernateException {
		return _original;
	}

	public int hashCode(Object _x) throws HibernateException {
		return _x.hashCode();
	}

	public boolean equals(Object _x, Object _y) throws HibernateException {
		if (_x == _y)
			return true;
		if (null == _x || null == _y)
			return false;
		return _x.equals(_y);
	}
}
