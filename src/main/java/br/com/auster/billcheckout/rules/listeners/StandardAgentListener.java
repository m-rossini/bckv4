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
package br.com.auster.billcheckout.rules.listeners;

import org.apache.log4j.Logger;
import org.drools.agent.AgentEventListener;

/**
 *
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public class StandardAgentListener implements AgentEventListener {

	private static final Logger log = Logger.getLogger(StandardAgentListener.class);
	
	/**
	 * 
	 * @param message
	 * @see org.drools.agent.AgentEventListener#debug(java.lang.String)
	 */
	public void debug(String message) {
		log.debug(message);
	}

	/**
	 * 
	 * @param e
	 * @see org.drools.agent.AgentEventListener#exception(java.lang.Exception)
	 */
	public void exception(Exception e) {
		log.error("Exception caught at Agent Event Listener:" , e);
	}

	/**
	 * 
	 * @param message
	 * @see org.drools.agent.AgentEventListener#info(java.lang.String)
	 */
	public void info(String message) {
		log.info(message);
	}

	/**
	 * 
	 * @param name
	 * @see org.drools.agent.AgentEventListener#setAgentName(java.lang.String)
	 */
	public void setAgentName(String name) {
		log.debug("Setting agent name to:[" + name + "]");
	}

	/**
	 * 
	 * @param message
	 * @see org.drools.agent.AgentEventListener#warning(java.lang.String)
	 */
	public void warning(String message) {
		log.warn(message);
	}

}
