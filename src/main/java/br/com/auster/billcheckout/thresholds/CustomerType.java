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

import br.com.auster.om.reference.CustomizableEntity;



/**
 * @author framos
 * @version $Id$
 */
public class CustomerType extends CustomizableEntity {


	protected String code;
	protected String description;
	
	
	
	
	public CustomerType() {
		this(0);
	}
	
	public CustomerType(long _uid) {
		super(_uid);
	}
	
	
	public final String getCustomDescription(){
		return this.getCustomerType() + " - " + getTypeDescription();
	}
	
	public final String getCustomerType() {
		return this.code;
	}
	public final void setCustomerType(String _type) {
		this.code = _type;
	}
	
	public final String getTypeDescription() {
		return this.description;
	}
	public final void setTypeDescription(String _desc) {
		this.description = _desc;
	}

	
	@Override
	public boolean equals(Object _obj) {
		if (this.code == null) {
			return (((CustomerType)_obj).code == null);
		}
		return this.code.equals(((CustomerType)_obj).code);
	}

	@Override
	public int hashCode() {
		if (this.code == null) {
			return 0;
		}
		return this.code.hashCode();
	}
	
}
