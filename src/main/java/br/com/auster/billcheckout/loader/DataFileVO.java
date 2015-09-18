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
 * Created on 21/10/2006
 */
package br.com.auster.billcheckout.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataFileVO {

	private List<String> content;

	public DataFileVO() {
		this.content = new ArrayList<String>();
	}

	public void addValues(List content) {
		this.content = content;
	}

	public void addValues(String[] parts) {

		for (int i = 0; i < parts.length; i++) {
			this.content.add(i, parts[i]);
		}
	}

	public void addValue(String value) {

		this.content.add(value);
	}

	public void setValue(int index, String value) {
		this.content.set(index, value);
	}

	public String getValue(int index) {

		return this.content.get(index);
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();

		Iterator it = this.content.iterator();

		while (it.hasNext()) {
			String element = (String) it.next();
			sb.append(element);
			sb.append(";");
		}
		sb.setLength(sb.length() - 1); // ;)

		return sb.toString();
	}

}
