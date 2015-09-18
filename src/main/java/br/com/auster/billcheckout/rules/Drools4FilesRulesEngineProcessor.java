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
 * Created on 10/04/2006
 */
package br.com.auster.billcheckout.rules;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class has being under a huge refactor from Billcheckout 3 to Billcheckout 4.
 * The behavior is almost the same as of previous version, except it does not support encryption of
 * source rule files anymore.
 * The inner class that wrapped rule files is not used anymore.
 * 
 * Thje configuration of this class is exactly the same as the one supporting directories except for:
 * 1-The mask attributes are not supported, instead change mask by name for naming the needed DRL and DSL Files.
 * 2-The name attribute is not used, so it does not need to be specified(Althrought it can be).
 * 
 * Now, for each file declaration, we create a new Package Builder (As previous version use to do also), but
 * althrought un previous version that was optional, now it is mandatory due to drools changes on this matter.
 * 
 * @author rbarone
 * @version $Id$
 */
public class Drools4FilesRulesEngineProcessor extends AbstractRulesEngineProcessor {
	// TODO Test this class as same behavior as of BCK V3

	private static final Logger	log												= Logger
																														.getLogger(Drools4FilesRulesEngineProcessor.class);

	private static final I18n		i18n											= I18n
																														.getInstance(Drools4FilesRulesEngineProcessor.class);

	public Drools4FilesRulesEngineProcessor(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.auster.billcheckout.drools.AbstractRulesEngineProcessor#buildRulesList(org.w3c.dom.Element)
	 */
	@Override
	protected Map<String, RulePackageSourceHook> buildRulesList(Element config) {
		Map<String, RulePackageSourceHook> results = new HashMap<String, RulePackageSourceHook>();

		Element rulePackages = DOMUtils.getElement(config, ELMT_RULES_PACKAGES, true);
		NodeList list = DOMUtils.getElements(rulePackages, ELMT_RULE_PACKAGE);
		int qt = list.getLength();

		for (int i = 0; i < qt; i++) {
			Element rulePackage = (Element) list.item(i);

			String drlFile = DOMUtils.getAttribute(rulePackage, ATTR_DRL_NAME, true);
			String dslFile = DOMUtils.getAttribute(rulePackage, ATTR_DSL_NAME, false);
			RulePackageSourceHook hook = new RulePackageSourceHook();
			hook.addDrlFile(drlFile);
			hook.addDslFile(dslFile);
			results.put(drlFile, hook);
		}

		return results;
	}

}
