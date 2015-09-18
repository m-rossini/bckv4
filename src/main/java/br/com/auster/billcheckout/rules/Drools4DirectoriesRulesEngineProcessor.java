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

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.util.I18n;
import br.com.auster.common.xml.DOMUtils;

/**
 * This class adds DRL and DSL files to a rule base. It looks for rule-uri TAGS
 * (Represents DRL Files) and for each one it does: Get the base attribute that
 * must be a filesystem directory Get the mask attribute. List all files on the
 * given base directory matching the mask. For each that are successuful it adds
 * to rule base
 * 
 * Tha mask is implemented in this class using commons-io WildcardFileFilter.
 * 
 * It does the same for expander-uri TAGS (Represents DSL Files)
 * 
 * @author mtengelm
 * @version $Id$
 */
public class Drools4DirectoriesRulesEngineProcessor extends AbstractRulesEngineProcessor {
	// TODO Fully test this class
	private static final Logger	log							= Logger
																									.getLogger(Drools4DirectoriesRulesEngineProcessor.class);
	private static final I18n		i18n						= I18n
																									.getInstance(Drools4DirectoriesRulesEngineProcessor.class);


	public Drools4DirectoriesRulesEngineProcessor(String name) {
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

			String name = DOMUtils.getAttribute(rulePackage, ATTR_NAME, true);
			
			String base = DOMUtils.getAttribute(rulePackage, ATTR_BASE, true);
			File fileBase = new File(base);
			
			String drlMask = DOMUtils.getAttribute(rulePackage, ATTR_DRL_MASK, true);
			File[] drlFiles = getFiles(fileBase,drlMask);
			
			String dslMask = DOMUtils.getAttribute(rulePackage, ATTR_DSL_MASK, false);
			File[] dslFiles = getFiles(fileBase,dslMask);
			
			RulePackageSourceHook hook = new RulePackageSourceHook();
			if (drlFiles != null) {
				for (int j=0; j < drlFiles.length;j++) {
					log.trace("Adding a DRL File named:"  + drlFiles[j]);
					hook.addDrlFile(drlFiles[j]);
				}
			}
			if (dslFiles != null) {
				for (int j=0; j < dslFiles.length;j++) {
					hook.addDslFile(dslFiles[j]);
				}
			}

			results.put(name, hook);
		} // Each of Rule Package

		return results;
	}

	/**
	 * Helper method to get an array of files given a File representing a base directory and a string
	 * representing the WildCard for file name existing in the base directory
	 * 
	 * If base or mask is null, then it returns null.
	 * For a better understanding of base, mask and how the search is done, see
	 * apache common-io WildcardFileFilter class.
	 * 
	 * @param base
	 * @param drlMask
	 * @return an array of files
	 */
	protected File[] getFiles(File base, String mask) {
		if (mask == null || base == null) {
			return null;
		}
		FileFilter filter = new WildcardFileFilter(mask);
		return base.listFiles(filter);
	}
}
