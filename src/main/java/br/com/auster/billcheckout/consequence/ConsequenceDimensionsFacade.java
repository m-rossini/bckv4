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
 * Created on 23/08/2006
 */
package br.com.auster.billcheckout.consequence;

import java.util.Collection;

import br.com.auster.billcheckout.ruleobjects.Rule;
import br.com.auster.om.reference.facade.ReferenceFacades;

/**
 * @author framos
 * @version $Id$
 *
 */
public interface ConsequenceDimensionsFacade extends ReferenceFacades {

	/**
	 * Returns the Rule object loaded with the rule identified by the
	 * 	unique id passed as parameter. If such id does not exists, than
	 * 	<code>null</code> is returned.
	 * <p>
	 * If an exception is raised during the query process, then an <code>RuleQueryException</code>
	 *   is thrown.
	 * 
	 * @param _uid the unique id of a rule
	 * 
	 * @return a Rule instance loaded with the rule info, or <code>null</code>
	 */
	public Rule getRule(long _uid);
	
	/**
	 * Same as {@link #getRule(long)}, except that the incoming parameter
	 * 	 is not the rule unique id but its code. In the database model such
	 *   column is also defined as unique.
	 * <p>
	 * Again, if there is no rule with such name then <code>null</code> is returned.
	 * <p>
	 * If an exception is raised during the query process, then an <code>RuleQueryException</code>
	 *   is thrown.
	 *   
	 * @param _ruleCode the rule unique code
	 * 
	 * @return a Rule instance loaded with the rule info, or <code>null</code>
	 */
	public Rule getRule(String _ruleCode);

	/**
	 * Returns a Collection with all the rules currently registered in the database.
	 * 	 The returning collection does not garantees the order of the rules when
	 * 	 iterated.
	 * <p>
	 * If there are no rules in the database, then the collection is returned empty.   
	 * <p>
	 * If an exception is raised during the query process, then an <code>RuleQueryException</code>
	 *   is thrown.
	 * 
	 * @return a Collection instance with all the rules currently registered
	 */
	public Collection<Rule> getRules();
}
