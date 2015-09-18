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
 * Created on 19/02/2007
 */
package br.com.auster.billcheckout.repo;

import java.io.File;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.Session;

import junit.framework.TestCase;
import br.com.auster.repo.RepositoryConfigurator;
import br.com.auster.repo.RuleItem;
import br.com.auster.repo.RulesRepository;
import br.com.auster.repo.tools.RepositoryTools;

/**
 * @author framos
 * @version $Id$
 */
public class RepositoryTest extends TestCase {

	
	public void testGo() {
		try {
			RepositoryConfigurator cfg = new RepositoryConfigurator();
			Repository rp = cfg.createRepository("src/test/resources/repo/repository.xml", "target/repo");
			Session s = cfg.login(rp);
			cfg.setupRulesRepository(s);			
			RulesRepository repoInstance = new RulesRepository(s);
			RepositoryTools.importDrlToRepository(new File("src/test/resources/repo/rule1.drl"),  repoInstance);
			repoInstance.save();
			repoInstance.logout();
			repoInstance = null;
			s = null;
			rp = null;
			cfg = null;
			
			cfg = new RepositoryConfigurator();
			rp = cfg.createRepository("src/test/resources/repo/repository.xml", "target/repo");
			s = cfg.login(rp);
			repoInstance = new RulesRepository(s);
			
			List<RuleItem> list = repoInstance.listRuleItems();
			for (RuleItem it : list) {
				System.out.println(it.getName());
				System.out.println(it.getDescription());
				System.out.println(it.getVersionNumber());
				System.out.println(it.getLastModified());
			}
			System.out.println("========>  SIZE IS " + list.size());
			repoInstance.save();
			repoInstance.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testDump() {
		try {
			RepositoryConfigurator cfg = new RepositoryConfigurator();
			Repository rp = cfg.createRepository("src/test/resources/repo/repository.xml", "target/repo");
			Session s = cfg.login(rp);
//			cfg.clearRulesRepository(s);
			cfg.setupRulesRepository(s);			
			RulesRepository repoInstance = new RulesRepository(s);
			repoInstance.dumpRepository();
			repoInstance.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testGetRules() {
		try {
			RepositoryConfigurator cfg = new RepositoryConfigurator();
			Repository rp = cfg.createRepository("src/test/resources/repo/repository.xml", "target/repo");
			Session s = cfg.login(rp);
			cfg.setupRulesRepository(s);			
			RulesRepository repoInstance = new RulesRepository(s);
			RuleItem it = repoInstance.loadRule("rule1");
//			List<RuleItem> list = repoInstance.listRuleItems();
//			for (RuleItem it : list) {
				System.out.println(it.getName());
				System.out.println(it.getDescription());
				System.out.println(it.getVersionNumber());
				System.out.println(it.getLastModified());
//			}
//			System.out.println("========>  SIZE IS " + list.size());
			repoInstance.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
