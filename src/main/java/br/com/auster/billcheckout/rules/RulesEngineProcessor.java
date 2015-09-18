/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
 * Created on Feb 3, 2005
 */
package br.com.auster.billcheckout.rules;

import java.util.List;
import java.util.Map;
import org.drools.QueryResults;
import org.w3c.dom.Element;

/**
 * <p><b>Title:</b> RulesEngineProcessor</p>
 * <p><b>Description:</b> </p>
 * <p><b>Copyright:</b> Copyright (c) 2004</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: RulesEngineProcessor.java 378 2007-03-27 14:30:46Z pvieira $
 * 
 * MT 2007-Aug-26
 * -Added support to get results map
 * 
 */
public interface RulesEngineProcessor {
	//TODO Create Method to run queries and return the results WHEN parameters exists.
  public void configure(Element config) throws Exception;
  public void init(Map properties) throws Exception ;
  public void prepare(Map applicationData) throws Exception;
  public void assertFact(Object fact)  throws Exception;
  public void fireRules() throws Exception;
  public void refresh(String reqId, Map<String, Object> map);
  public List getResults();
  public Map getResultsMap();
   
  /**
   * Call this method to free resources used by the Application Data and Working
   * Memory. Usually after you call (and use) {@link #getResults()}, but the 
   * decision of when to use it is entirely up to you.
   */
  public void clear();
  
  /**
   * @return the name of this <code>RulesEngineProcessor</code>
   */
  public String getName();
	public QueryResults getQueryResults(String name);
}
