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
package br.com.auster.billcheckout.rules.assertion;

import org.w3c.dom.Element;
import br.com.auster.billcheckout.rules.RulesEngineProcessor;
import br.com.auster.om.invoice.InvoiceModelObject;

/**
 * This is an interface to engine that do working memory assertion.
 * It will receive the engine processor (Which has the assertFact method), and
 * also the object model.
 * 
 * It is up to the implementation which objects are gonna be inserted into
 * engine working memory thru assertFact method
 *
 * @author mtengelm
 * @version $Id$
 * @since JDK1.4
 */
public interface AssertionEngine {

	/***
	 * 
	 * Configures the assertion engine.
	 * The contents of the config TAG and also the behavior dependet of configuration,
	 * is totally implementation dependent and therefore unspecified.
	 * <p>
	 * Example:
	 * <pre>
	 *    Create a use example.
	 * </pre>
	 * </p>
	 * 
	 * @param config
	 */
	public void configure(Element config);
	public void assertObjects(RulesEngineProcessor engine, InvoiceModelObject om);
	public long getAssertedFacts();
}
