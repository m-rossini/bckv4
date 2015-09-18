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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataFileParser {

	protected final String DEFAULT_LINE_SEPARATOR = "\r?\n";
	protected final String DEFAULT_FIELD_SEPARATOR = ";";
	
	private String lineSeparator;

	private String fieldSeparator;
	
	public DataFileParser() {		
	}

	public String getFieldSeparator() {
		return (this.fieldSeparator == null)? DEFAULT_FIELD_SEPARATOR: this.fieldSeparator;
	}

	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public String getLineSeparator() {
		return (this.lineSeparator == null)? DEFAULT_LINE_SEPARATOR: this.lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	/**
	 * 
	 * @param inputFile file to be parsed
	 * @param fileType file type
	 * @return List of <code>DataFileVO</code>
	 * @throws FileNotFoundException
	 */
	public List loadDataFromFile(File inputFile) 
			throws FileNotFoundException {

		Scanner scanner = new Scanner(inputFile);
		scanner.useDelimiter(this.getLineSeparator());

		List fileData = new ArrayList();

		while (scanner.hasNext()) {
			String line = scanner.next();
			if (line.trim().length() == 0) {
				continue;
			}
			String[] parts = line.split(this.getFieldSeparator());

			DataFileVO dataVO = new DataFileVO();
			dataVO.addValues(parts);

			fileData.add(dataVO);
		}
		System.out.println(fileData);

		scanner.close();

		return fileData;
	}
}
