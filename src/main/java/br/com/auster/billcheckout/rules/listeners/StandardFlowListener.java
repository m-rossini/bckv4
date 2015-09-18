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
 * Created on 28/08/2007
 */
package br.com.auster.billcheckout.rules.listeners;

import org.apache.log4j.Logger;
import org.drools.WorkingMemory;
import org.drools.event.RuleFlowCompletedEvent;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.event.RuleFlowStartedEvent;

/**
 * This class is a simple class to trap Rule Flow Events in Drools.
 * It just prints to log the events as it comes up.
 * A Specific logger must be configured in order to one be able to watch what happends
 * The logger is named "billcheckout.flowListener"
 *
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class StandardFlowListener implements RuleFlowEventListener {
	//TODO ADD Trace calls with details of objects passed to methods
	private static final Logger log = Logger.getLogger("billcheckout.flowListener");
	
	/**
	 * Creates a new instance of the class <code>StandardFlowListener</code>.
	 */
	public StandardFlowListener() {
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.RuleFlowEventListener#ruleFlowCompleted(org.drools.event.RuleFlowCompletedEvent, org.drools.WorkingMemory)
	 */
	public void ruleFlowCompleted(RuleFlowCompletedEvent arg0, WorkingMemory arg1) {
		log.debug("Rule Flow Completed Event.");
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.RuleFlowEventListener#ruleFlowGroupActivated(org.drools.event.RuleFlowGroupActivatedEvent, org.drools.WorkingMemory)
	 */
	public void ruleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0, WorkingMemory arg1) {
		log.debug("Rule Flow Group Activated Event.");

	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.RuleFlowEventListener#ruleFlowGroupDeactivated(org.drools.event.RuleFlowGroupDeactivatedEvent, org.drools.WorkingMemory)
	 */
	public void ruleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0,
			WorkingMemory arg1) {
		log.debug("Rule Flow Group Deactivated Event.");

	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.RuleFlowEventListener#ruleFlowStarted(org.drools.event.RuleFlowStartedEvent, org.drools.WorkingMemory)
	 */
	public void ruleFlowStarted(RuleFlowStartedEvent arg0, WorkingMemory arg1) {
		log.debug("Rule Flow Started Event.");

	}

}
