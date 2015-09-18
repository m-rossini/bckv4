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
package br.com.auster.billcheckout.model;

import java.util.Date;

import br.com.auster.billcheckout.caches.CacheableKey;
import br.com.auster.billcheckout.caches.CacheableVO;
import br.com.auster.billcheckout.consequence.telco.GeographicDimension;
import br.com.auster.om.reference.CustomizableEntity;

/**
 * 
 * @author pvieira
 * @version $Id$
 */
public class TaxRate extends CustomizableEntity implements CacheableVO {


	private float taxRate;
	private GeographicDimension geoDimension;
	private TaxType taxType;
	private FiscalCode fiscalCode;
	private Date dtEffective;
	private Date dtExpiration;
	
	private CacheableKey key;
	private CacheableKey alternateKey;
	
	

	public TaxRate() {
		super(0);
	}
	
	public TaxRate(long uid) {
		super(uid);
	}

	public FiscalCode getFiscalCode() {
		return fiscalCode;
	}

	public void setFiscalCode(FiscalCode fiscalCode) {
		this.fiscalCode = fiscalCode;
	}

	public float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(float taxRate) {
		this.taxRate = taxRate;
	}

	public TaxType getTaxType() {
		return taxType;
	}

	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	public GeographicDimension getGeoDimension() {
		return geoDimension;
	}

	public void setGeoDimension(GeographicDimension geoDimension) {
		this.geoDimension = geoDimension;
	}

	public Date getDtEffective() {
		return dtEffective;
	}

	public void setDtEffective(Date dtEffective) {
		this.dtEffective = dtEffective;
	}

	public Date getDtExpiration() {
		return dtExpiration;
	}

	public void setDtExpiration(Date dtExpiration) {
		this.dtExpiration = dtExpiration;
	}

	public long getGeoDimensionUid() {
		return this.getGeoDimension() != null? this.getGeoDimension().getUid(): 0;
	}

	public void setGeoDimensionUid(long geoDMUid) {
		this.setGeoDimension(new GeographicDimension(geoDMUid));
	}

	public long getTaxTypeUid() {
		return this.getTaxType() != null? this.getTaxType().getUid(): 0;
	}

	public void setTaxTypeUid(long taxTypeUid) {
		this.setTaxType(new TaxType(taxTypeUid));
	}

	public long getFiscalCodeUid() {
		return this.getFiscalCode() != null? this.getFiscalCode().getUid(): 0;
	}

	public void setFiscalCodeUid(long fiscalCodeUid) {
		this.setFiscalCode(new FiscalCode(fiscalCodeUid));
	}

	public boolean equals(Object obj) {
		TaxRate anotherObj = (TaxRate) obj;
		boolean eq = (this.geoDimension == null ? (anotherObj.geoDimension == null) : (this.geoDimension.equals(anotherObj.geoDimension)));
		eq &= (this.taxType == null ? (anotherObj.taxType == null) : (this.taxType.equals(anotherObj.taxType)));
		eq &= (this.taxRate == anotherObj.taxRate);

		return eq;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = super.hashCode();
		result = result*37 + (this.getGeoDimension() == null? 0 : 
			                        this.getGeoDimension().hashCode());
		result = result*37 + (this.getTaxType() == null? 0 : 
                                    this.getTaxType().hashCode());
		return result;
	}	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString() +  
		" : GeoDimension=[" + (this.getGeoDimension() == null ? "null" :  this.getGeoDimension().toString()) + 
		"].TaxType=[" + (this.getTaxType() == null ? "null" : this.getTaxType().toString()) +
		"].FiscalCode=[" + (this.getFiscalCode() == null ? "null" : this.getFiscalCode().toString()) +
		"].taxRate=[" + this.getTaxRate() +
		"]";
	}

	public CacheableKey getKey() {
		if (this.key == null) {
			this.key = createKey( (this.getGeoDimension() == null ? 0 : this.getGeoDimensionUid()), 
								  (this.getTaxType() == null ? 0 : this.getTaxTypeUid()), 
								  (this.getFiscalCode() == null ? 0 : this.getFiscalCodeUid()) );
		}
		return this.key;
	}
	
	public static class InnerKey implements CacheableKey {
		
		private long geoDimensionUid;
		private long taxTypeUid;
		private long fiscalCodeUid;
		
		public InnerKey(long geoDimensionUid, long taxTypeUid, long fiscalCodeUid) {
			this.geoDimensionUid = geoDimensionUid;
			this.taxTypeUid = taxTypeUid;
			this.fiscalCodeUid = fiscalCodeUid;
		}

		public boolean equals(Object obj) {
			InnerKey k = (InnerKey) obj;
			boolean eq = this.geoDimensionUid == k.geoDimensionUid;
			eq &= this.taxTypeUid == k.geoDimensionUid;
			eq &= this.fiscalCodeUid == k.fiscalCodeUid;

			return eq;
		}
		
		public int hashCode() {
			long code = 17 + this.geoDimensionUid * 37;
			code += this.taxTypeUid * 37;
			code += this.fiscalCodeUid * 37;
			return (int) code;
		}
		
		public long getGeographics() { return this.geoDimensionUid; }
		public long getTaxType() { return this.taxTypeUid; }
		public long getFiscalCode() { return this.fiscalCodeUid; }
	}
	
	public CacheableKey getAlternateKey() {
		if (this.alternateKey == null) {
			this.alternateKey = createAlternateKey( 
								  (this.getGeoDimension() == null ? null : this.getGeoDimension().getState()), 
								  (this.getTaxType() == null ? null : this.getTaxType().getTaxCode()), 
								  (this.getFiscalCode() == null ? null : this.getFiscalCode().getFiscalCode()) );
		}
		return this.alternateKey;
	}
	
	public static class InnerAlternateKey implements CacheableKey {
		
		private String uf;
		private String taxType;
		private String fiscalCode;
		
		public InnerAlternateKey(String _uf, String _taxType, String _fiscalCode) {
			this.uf = _uf;
			this.taxType = _taxType;
			this.fiscalCode = _fiscalCode;
		}

		public boolean equals(Object obj) {
			InnerAlternateKey k = (InnerAlternateKey) obj;
			boolean eq = true;
			if (this.uf == null) {
				eq &= (k.uf == null);
			} else {
				eq &= (this.uf.equals(k.uf));
			}
			if (this.taxType == null) {
				eq &= (k.taxType == null);
			} else {
				eq &= (this.taxType.equals(k.taxType));
			}
			if (this.fiscalCode == null) {
				eq &= (k.fiscalCode == null);
			} else {
				eq &= (this.fiscalCode.equals(k.fiscalCode));
			}
			return eq;
		}
		
		public int hashCode() {
			long code = 17 + (this.uf == null ? 0 : this.uf.hashCode() * 37);
			code += (this.taxType == null ? 0 : this.taxType.hashCode() * 37);
			code += (this.fiscalCode == null ? 0 : this.fiscalCode.hashCode() * 37);
			return (int) code;
		}
		
		public String getGeographics() { return this.uf; }
		public String getTaxType() { return this.taxType; }
		public String getFiscalCode() { return this.fiscalCode; }
	}	

	
	
	// ---------------------------
	// Helper methods
	// ---------------------------
	
	public static final CacheableKey createKey(long _geoUid, long _taxTypeUid, long _fiscalCodeUid) {
		return new TaxRate.InnerKey(_geoUid, _taxTypeUid, _fiscalCodeUid);
	}

	public static final CacheableKey createAlternateKey(String _uf, String _taxType, String _fiscalCode) {
		return new TaxRate.InnerAlternateKey(_uf, _taxType, _fiscalCode);
	}
}
