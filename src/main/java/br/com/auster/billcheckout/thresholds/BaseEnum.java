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
 * Created on 12/03/2007
 */
package br.com.auster.billcheckout.thresholds;


/**
 * @author framos
 * @version $Id$
 */
public abstract class BaseEnum {

	
	private int id;
	private String value;
	
	
	
	public BaseEnum() {	}
	
	protected BaseEnum(int _id, String _value) {
		this.id = _id;
		this.value = _value; 
	}
	
	
	public boolean equals(Object _obj) { 
		return this.id == ((BaseEnum)_obj).id; 
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	public final String getValue() { 
		return this.value; 
	}
	
	public final int getSequenceId() { 
		return this.id; 
	}		
	
	/**
	 * This method should never be called. It's only define to ensure 
	 * 	hibernate operations will correctly create instances of BaseEnum.
	 */
	public void setSequenceId(int _id) {
		this.id = _id;
	}
}
