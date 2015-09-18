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
package br.com.auster.billcheckout.consequence;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.w3c.dom.Element;

import br.com.auster.billcheckout.consequence.telco.AccountDimension;
import br.com.auster.billcheckout.consequence.telco.CarrierDimension;
import br.com.auster.billcheckout.consequence.telco.CycleDimension;
import br.com.auster.billcheckout.consequence.telco.GeographicDimension;
import br.com.auster.billcheckout.consequence.telco.TelcoConsequence;
import br.com.auster.billcheckout.consequence.telco.TimeDimension;
import br.com.auster.billcheckout.filter.telco.TelcoBillcheckoutPersistenceFilter;
import br.com.auster.billcheckout.ruleobjects.Rule;
import br.com.auster.common.xml.DOMUtils;

/**
 * @author framos
 * @version $Id$
 */
public class TestParallelSaving {

	
	
	protected static final Date NOW = Calendar.getInstance().getTime();
	protected static final List datelist = new LinkedList();
	protected static final List carrierlist = new LinkedList();
	protected static final List statelist = new LinkedList();
	static {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			// date list
			datelist.add(sdf.parseObject("2006-10-10"));
			datelist.add(sdf.parseObject("2006-10-05"));
			datelist.add(sdf.parseObject("2006-10-20"));
			datelist.add(sdf.parseObject("2006-10-14"));
			datelist.add(sdf.parseObject("2006-10-15"));
			// carrier
			carrierlist.add("00");
			carrierlist.add("12");
			carrierlist.add("15");
			carrierlist.add("21");
			// state
			statelist.add("SC");
			statelist.add("SP");
			statelist.add("RS");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Element e = DOMUtils.openDocument("parallel/parallel.xml", false);
			for (int i=0; i < 15; i++) {
				TelcoBillcheckoutPersistenceFilter filter = new TelcoBillcheckoutPersistenceFilter("filter");
				filter.configure(e);
				Runnable myRunnable = new MyRunnable(filter);
				Thread t = new Thread(myRunnable);
				t.start();
				Thread t2 = new Thread(myRunnable);
				t2.start();
				Thread t3 = new Thread(myRunnable);
				t3.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class MyRunnable implements Runnable {

	private TelcoBillcheckoutPersistenceFilter filter;
	
	
	public MyRunnable(TelcoBillcheckoutPersistenceFilter _filter) {
		this.filter = _filter;
	}
	
	public void run() {
		try {
			Thread.sleep(5000);
			filter.processElement(createConsequences());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected Map createConsequences() {
		Map m = new HashMap();
		// list of consequences
		List consequenceList = new LinkedList();
		consequenceList.add(createSingleConsequence());
		m.put("consequence-list", consequenceList);
		return m;
	}
	
	
	protected TelcoConsequence createSingleConsequence() {
		Rule r = new Rule("R99", "TestRule");
		Random random = new Random(Calendar.getInstance().getTimeInMillis());
		CycleDimension cycle = new CycleDimension("33", 
										TestParallelSaving.NOW, 
										TestParallelSaving.NOW, 
										(Date)TestParallelSaving.datelist.get(random.nextInt(5))); 
		TimeDimension time = new TimeDimension(TestParallelSaving.NOW);
		GeographicDimension geo = new GeographicDimension("BR", "SUL",  
				                                 (String)TestParallelSaving.statelist.get(random.nextInt(3)), 
				                                 "JOINVILLE");
		AccountDimension acc = new AccountDimension("C", "HOLDER", "001001101");
		CarrierDimension carrier = new CarrierDimension("VIVO", 
											(String)TestParallelSaving.carrierlist.get(random.nextInt(4)), 
				                            (String)TestParallelSaving.statelist.get(random.nextInt(3)));
		
		TelcoConsequence consequence = new TelcoConsequence();
		consequence.setRelatedRule(r);
		consequence.setAccount(acc);
		consequence.setCarrier(carrier);
		consequence.setCycle(cycle);
		consequence.setGeographics(geo);
		consequence.setTime(time);
		return consequence;
	}
}
