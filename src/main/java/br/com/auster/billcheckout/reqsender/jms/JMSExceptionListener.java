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
 * Created on 21/01/2007
 */
package br.com.auster.billcheckout.reqsender.jms;

import java.util.concurrent.BlockingQueue;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * @author mtengelm
 *
 */
public class JMSExceptionListener implements ExceptionListener {

  private BlockingQueue syncronizer;

	public JMSExceptionListener() {
  }	
	/**
	 * If we got a BlockingQueue, then we are gonna put a null before re-thrwoing the 
	 * JMS Server Side Exception.
	 * 
   * @param syncronizer
   */
  public JMSExceptionListener(BlockingQueue syncronizer) {
  	this.syncronizer = syncronizer;
  }

	/* (non-Javadoc)
	 * @see javax.jms.ExceptionListener#onException(javax.jms.JMSException)
	 */
	public void onException(JMSException exp) {
    System.out.println("-----");
    System.out.println(exp);
    System.out.println("-----"); 		
		try {
	    syncronizer.put(exp);
    } catch (InterruptedException e) {
	    e.printStackTrace();
    }
   
    RuntimeException nova = new IllegalStateException("JMS Server has returned an exception while subcriber was waiting on topic.");
    nova.initCause(exp);
    throw nova;
	}

}
