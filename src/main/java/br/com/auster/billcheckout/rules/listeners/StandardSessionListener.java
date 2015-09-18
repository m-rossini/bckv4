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
import org.drools.event.ObjectInsertedEvent;
import org.drools.event.ObjectRetractedEvent;
import org.drools.event.ObjectUpdatedEvent;
import org.drools.event.WorkingMemoryEventListener;

/**
 * This class is a simple class to trap Working Memory Events in Drools.
 * It just prints to log the events as it comes up.
 * A Specific logger must be configured in order to one be able to watch what happends
 * The logger is named "billcheckout.memoryListener"
 *
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class StandardSessionListener implements WorkingMemoryEventListener {
	//TODO ADD Trace calls with details of objects passed to methods
	private static final Logger log = Logger.getLogger("billcheckout.memoryListener");
	
	/**
	 * Creates a new instance of the class <code>StandardSessionListener</code>.
	 */
	public StandardSessionListener() {
		log.debug("A New Working Memory Listener was instantiated.");
	}

	/**
	 * 
	 * @param arg0
	 * @see org.drools.event.WorkingMemoryEventListener#objectInserted(org.drools.event.ObjectInsertedEvent)
	 */
	public void objectInserted(ObjectInsertedEvent ev) {
		log.debug("Object Inserted Event.");
//		log.trace(ev.getObject()); //The Object Inserted
		log.trace(ev.getFactHandle().toExternalForm());	
	}

	/**
	 * 
	 * @param arg0
	 * @see org.drools.event.WorkingMemoryEventListener#objectRetracted(org.drools.event.ObjectRetractedEvent)
	 */
	public void objectRetracted(ObjectRetractedEvent ev) {
		log.debug("Object Retracted Event.");
		log.trace(ev.getFactHandle().toExternalForm());		
	}

	/**
	 * 
	 * @param arg0
	 * @see org.drools.event.WorkingMemoryEventListener#objectUpdated(org.drools.event.ObjectUpdatedEvent)
	 */
	public void objectUpdated(ObjectUpdatedEvent ev) {
		log.debug("Object Updated Event.");
		log.trace(ev.getFactHandle().toExternalForm());	
	}

}
