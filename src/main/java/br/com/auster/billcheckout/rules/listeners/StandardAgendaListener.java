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
import org.drools.common.EqualityKey;
import org.drools.common.InternalFactHandle;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;

/**
 * This class is a simple class to trap Agenda Events in Drools. It just prints
 * to log the events as it comes up. A Specific logger must be configured in
 * order to one be able to watch what happends The logger is named
 * "billcheckout.agendaListener"
 * 
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class StandardAgendaListener implements AgendaEventListener {
	// TODO ADD Trace calls with details of objects passed to methods
	private static final Logger	log	= Logger.getLogger("billcheckout.agendaListener");

	private StringBuilder				sb	= new StringBuilder();

	/**
	 * Creates a new instance of the class <code>StandardAgendaListener</code>.
	 */
	public StandardAgendaListener() {
		log.debug("Agenda Listener Instantiated.");
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.AgendaEventListener#activationCancelled(org.drools.event.ActivationCancelledEvent,
	 *      org.drools.WorkingMemory)
	 */
	public void activationCancelled(ActivationCancelledEvent ace, WorkingMemory wm) {
		log.debug("Activation Cancelled Event.");

	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.AgendaEventListener#activationCreated(org.drools.event.ActivationCreatedEvent,
	 *      org.drools.WorkingMemory)
	 */
	public void activationCreated(ActivationCreatedEvent ace, WorkingMemory wm) {
		log.debug("Activation Created Event.Current Agenda Size:"
				+ wm.getAgenda().agendaSize());

		if (log.isTraceEnabled()) {
			sb.setLength(0);
			sb.append(".#:");
			sb.append(ace.getActivation().getActivationNumber());
			sb.append(".Package:");
			sb.append(ace.getActivation().getRule().getPackage());
			sb.append(".Name");
			sb.append(ace.getActivation().getRule().getName());
			sb.append(".IsActivated:" + ace.getActivation().isActivated());
			sb.append(".Tuple Recency:" + ace.getActivation().getTuple().getRecency());
			sb.append(".Dependencies:" + ace.getActivation().getLogicalDependencies());
			log.trace(sb.toString());
			
			log.trace("Fact Handles>>>");			
			InternalFactHandle[] factHandles = ace.getActivation().getTuple().getFactHandles();
			for (int i = 0; i < factHandles.length; i++) {
				sb.setLength(0);
				sb.append("ID:");
				sb.append(factHandles[i].getId());	
				sb.append(".Is Shadow:");
				sb.append(factHandles[i].isShadowFact());
				log.trace(sb.toString());
				
				
			}
			log.trace(sb.toString());
			log.trace("------------------------------------------------------------------------");
		}

	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.AgendaEventListener#afterActivationFired(org.drools.event.AfterActivationFiredEvent,
	 *      org.drools.WorkingMemory)
	 */
	public void afterActivationFired(AfterActivationFiredEvent ace, WorkingMemory wm) {
		log.debug("After Activation Fired Event.");
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.AgendaEventListener#agendaGroupPopped(org.drools.event.AgendaGroupPoppedEvent,
	 *      org.drools.WorkingMemory)
	 */
	public void agendaGroupPopped(AgendaGroupPoppedEvent ace, WorkingMemory wm) {
		log.debug("Agenda Group is Popped.");
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.AgendaEventListener#agendaGroupPushed(org.drools.event.AgendaGroupPushedEvent,
	 *      org.drools.WorkingMemory)
	 */
	public void agendaGroupPushed(AgendaGroupPushedEvent ace, WorkingMemory wm) {
		log.debug("Agenda Group is Pushed.");
	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @see org.drools.event.AgendaEventListener#beforeActivationFired(org.drools.event.BeforeActivationFiredEvent,
	 *      org.drools.WorkingMemory)
	 */
	public void beforeActivationFired(BeforeActivationFiredEvent ace, WorkingMemory wm) {
		log.debug("Before Activation Fired Event.");
	}

}
