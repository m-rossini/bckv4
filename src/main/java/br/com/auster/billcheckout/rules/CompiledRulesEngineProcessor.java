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
 * Created on 04/09/2007
 */
package br.com.auster.billcheckout.rules;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.agent.AgentEventListener;
import org.drools.agent.RuleAgent;
import org.drools.rule.Package;
import org.w3c.dom.Element;

/**
 * This is a concrete implementation of and Rules Engine Processor, that
 * relies on a Drools Agent (See on java docs) to create a rule base.
 *
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class CompiledRulesEngineProcessor extends AbstractRulesEngineProcessor {
//TODO Test Rule agent, new rules while running and so on
	private Logger log = Logger.getLogger(CompiledRulesEngineProcessor.class);
	
	private RuleAgent	agent;

	private AgentEventListener	agentListener;

	/**
	 * Creates a new instance of the class <code>CompiledRulesEngineProcessor</code>.
	 * @param name
	 */
	public CompiledRulesEngineProcessor(String name) {
		super(name);
	}

	/**
	 * Configures an agent.
	 * When using this class there is no need to build from source, once we do expect
	 * the rules are already compiled by the agent (Or someone else, visible to agent).
	 * 
	 * Used configurations are:
	 * Rule Base Properties (The same way as super class)
	 * Agent Properties (The same way as Rule Base Properties)
	 * 
	 * @param config
	 * @throws Exception
	 * @see br.com.auster.billcheckout.rules.AbstractRulesEngineProcessor#configure(org.w3c.dom.Element)
	 */
	@Override
	public void configure(Element config) throws Exception {					
		Properties ruleAgentProperties = getProperties(config, ELMT_RULEAGENT_CONFIG, ATTR_PROPERTIES);
		ruleAgentProperties.load(this.getClass().getClassLoader().getResourceAsStream("ruleagent.properties"));

		this.ruleBaseProps = getProperties(config, ELMT_RULEBASE_CONFIG, ATTR_PROPERTIES);	
		RuleBaseConfiguration ruleBaseConf = new RuleBaseConfiguration(this.ruleBaseProps);
		
		this.configureListeners(config);		
		
		agent = RuleAgent.newRuleAgent(ruleAgentProperties, this.agentListener, ruleBaseConf);		
	}

	/**
	 * This method in this class, has nothing to do.
	 * The source code from rules has no meaning in a compiled environment, so we can return null.
	 * Indeed, we can return any thing, once this method is expected to not be called for this class.
	 * This method is called fromc configure method of AbstractRulesEngineProcessor, which is
	 * overriden by this class.
	 * 
	 * @param config
	 * @return
	 * @see br.com.auster.billcheckout.rules.AbstractRulesEngineProcessor#buildRulesList(org.w3c.dom.Element)
	 */
	@Override
	protected Map<String, RulePackageSourceHook> buildRulesList(Element config) {
		return null;
	}

	/**
	 * This method on this class does not create an rule base, so it is the reason
	 * of not using the one provided by super class.
	 * Super class creates a rule base explicitly, while this class just get it from agent. 
	 * 
	 * @return A Rule Base
	 * @throws IOException
	 * @see br.com.auster.billcheckout.rules.AbstractRulesEngineProcessor#getRuleBase()
	 */
	@Override
	public RuleBase getRuleBase() throws IOException {
		return agent.getRuleBase();
	}
	
	/**
	 * This method is almost useless on this class.
	 * Different from super class, here we just add injected globals in order
	 * to avoid engine blow up IF developers forget to add global statements on rules.
	 * 
	 * @param globals
	 * @throws Exception
	 * @see br.com.auster.billcheckout.rules.AbstractRulesEngineProcessor#addPackages(java.util.Map)
	 */
	@Override
	protected void addPackages(Map globals) throws Exception {
		//Below addGlobals has the function of avoid Engine Blow Up if
		//Not declared as global on Rules. See javadoc for this method.
		Package[] pack = this.ruleBase.getPackages();
		for (int i=0; i < pack.length;i++) {
			pack[i].addGlobal(CTE_RESULTS_LIST, List.class);
			pack[i].addGlobal(CTE_RESULTS_MAP, Map.class);			
		}
	}

}
