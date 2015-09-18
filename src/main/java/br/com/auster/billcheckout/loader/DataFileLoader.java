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
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import br.com.auster.common.util.I18n;

/**
 * 
 * @author framos
 * @version $Id$
 *
 */
public class DataFileLoader {

	private static final I18n i18n = I18n.getInstance(DataFileLoader.class);

	public static void main(String[] args) {

		String fileName = null;

		try {
	        CommandLineParser cmdParser = new PosixParser();

	        CommandLine line = cmdParser.parse(createOptions(), args);

			fileName = line.getOptionValue(LoaderConfigurationConstants.CMDLINE_OPTS_FILENAME_MNEMONIC);
			String fileType = line.getOptionValue(LoaderConfigurationConstants.CMDLINE_OPTS_FILETYPE_MNEMONIC);
			String fileSeparator = line.getOptionValue(LoaderConfigurationConstants.CMDLINE_OPTS_FILESEP_MNEMONIC);

			DataFileLoader loader = new DataFileLoader();
			loader.loadDataFile(fileName, fileType, fileSeparator);
			
		} catch (FileNotFoundException e) {
			System.err.println(i18n.getString("datafile.notFound", fileName));
			System.exit(1);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(DataFileLoader.class.getName(), createOptions(), true);    		
			System.exit(1);
		}
	}

	protected static Options createOptions() {

		OptionBuilder.withArgName(LoaderConfigurationConstants.CMDLINE_OPTS_FILENAME);
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(true);
		OptionBuilder.withDescription(i18n.getString("loader.datafile.fullname"));
		Option fileName = OptionBuilder.create(LoaderConfigurationConstants.CMDLINE_OPTS_FILENAME_MNEMONIC);

		OptionBuilder.withArgName(LoaderConfigurationConstants.CMDLINE_OPTS_FILETYPE);
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(true);
		OptionBuilder.withDescription(i18n.getString("loader.datafile.type"));
		Option fileType = OptionBuilder.create(LoaderConfigurationConstants.CMDLINE_OPTS_FILETYPE_MNEMONIC);

		OptionBuilder.withArgName(LoaderConfigurationConstants.CMDLINE_OPTS_FILESEP);
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(false);
		OptionBuilder.withDescription(i18n.getString("loader.datafile.separator"));
		Option fileSep = OptionBuilder.create(LoaderConfigurationConstants.CMDLINE_OPTS_FILESEP_MNEMONIC);

		Options options = new Options();
		options.addOption(fileName);
		options.addOption(fileType);
		options.addOption(fileSep);

		return options;
	}

	public void loadDataFile(String fileName, String fileType, String fileSeparator)
			throws FileNotFoundException {

		DataFileParser parser = new DataFileParser();

		List list = parser.loadDataFromFile(new File(fileName));
		if (fileSeparator != null) {
			parser.setFieldSeparator(fileSeparator);
		}

		DataFileProcessor processor = DataFileProcessorFactory.createProcessor(fileType);
		processor.processData(list);
	}
}
