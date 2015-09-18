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
 * Created on 12/12/2006
 */
package br.com.auster.billcheckout.rules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.w3c.dom.Element;

import br.com.auster.billcheckout.consequence.Consequence;
import br.com.auster.billcheckout.consequence.DimensionCache;
import br.com.auster.billcheckout.consequence.telco.TelcoConsequenceBuilder;
import br.com.auster.billcheckout.rules.assertion.AssertionEngine;
import br.com.auster.billcheckout.rules.assertion.ReflectiveInvoiceAssertionEngine;
import br.com.auster.common.io.IOUtils;
import br.com.auster.om.invoice.Account;

/**
 * @author framos
 * @version $Id$
 *
 */
public abstract class BaseRuleTest extends TestCase {

	
	
	public static final String LOGGER_FILE = "log4j.xml";
	
	
    protected WorkingMemory workingMemory;
    protected List<Consequence> results;
    protected RuleBase ruleBase;

    protected void startupRuleEngine(String[] _filename) throws Exception {
        ruleBase = createRuleBase(_filename);
        
        workingMemory = ruleBase.newStatefulSession();
        createGlobals();
    }

    protected void createGlobals() throws Exception {
        results = new ArrayList<Consequence>();
        workingMemory.setGlobal( "results", results );
        
        TelcoConsequenceBuilder builder = new TelcoConsequenceBuilder();
        builder.setLenient( true );
        workingMemory.setGlobal( "consequenceBuilder", builder );
        
        DimensionCache dimensionCache = new DimensionCache();
        workingMemory.setGlobal( "dimensionCache", dimensionCache );
    }
    
    protected RuleBase createRuleBase(String[] ruleFileName) throws DroolsParserException, IOException, Exception {
		// pre build the package
		final PackageBuilder builder = new PackageBuilder();
		final RuleBase ruleBase = RuleBaseFactory.newRuleBase(RuleBase.RETEOO);
		for (int i =0; i < ruleFileName.length ; i++) {
			InputStream in = IOUtils.openFileForRead(ruleFileName[i]);
			builder.addPackageFromDrl(new InputStreamReader(in));
			final Package pkg = builder.getPackage();
			// add the package to a rulebase
			ruleBase.addPackage(pkg);
    	}
		return ruleBase;
	}
        
    
    protected void assertAccount(Account _account) throws Exception {
    	AssertionEngine assertion = new ReflectiveInvoiceAssertionEngine();
    	assertion.assertObjects(new TestRulesEngine(this.workingMemory), _account);
    }

    
    public static class TestRulesEngine implements RulesEngineProcessor {

    	private WorkingMemory mem;
    	    	
    	public TestRulesEngine(WorkingMemory _mem) {
    		this.mem = _mem;
    	}
    	
		public void assertFact(Object arg0) throws Exception {
			this.mem.insert(arg0);
		}

		public String getName() { return "TestEngine"; }
		
		public void clear() { }
		public void configure(Element arg0) throws Exception {}
		public void fireRules() throws Exception {}
		public List getResults() { return null; }
		public void init(Map arg0) throws Exception {}
		public void prepare(Map arg0) throws Exception {}

		public Map getResultsMap() {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * TODO why this methods was overriden, and what's the new expected behavior.
		 * 
		 * @param MISSING
		 * @return
		 * @see br.com.auster.billcheckout.rules.RulesEngineProcessor#getQueryResults(java.lang.String)
		 */
		public QueryResults getQueryResults(String name) {
			return null;
		}

		public void refresh(Map<String, Object> map) {
			// do nothing
			
		}

		public void refresh(String reqId, Map<String, Object> map) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
