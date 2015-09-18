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
 * Created on Jun 8, 2005
 */
package br.com.auster.billcheckout.reqsender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

import br.com.auster.billcheckout.reqsender.exceptions.CorruptedInformationException;
import br.com.auster.billcheckout.reqsender.exceptions.MissConfigurationException;
import br.com.auster.billcheckout.reqsender.exceptions.NoFilesFoundException;
import br.com.auster.billcheckout.reqsender.exceptions.SendMessageException;
import br.com.auster.billcheckout.reqsender.jms.JMSMessageSender;
import br.com.auster.billcheckout.reqsender.jms.JMSMessageTopicSender;
import br.com.auster.common.io.FileSet;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.util.I18n;
import br.com.auster.dware.console.flowcontrol.FlowControlMessage;
import br.com.auster.dware.console.flowcontrol.Stage;
import br.com.auster.dware.console.listener.RequestCreation;
import br.com.auster.facelift.requests.web.interfaces.WebRequestBuilder;
import br.com.auster.facelift.requests.web.model.NotificationEmail;

/**
 * <p>
 * <b>Title:</b> Command Line Request Manager
 * </p>
 * <p>
 * <b>Description:</b> A Request Manager interface for creating requests from
 * command line
 * </p>
 * <p>
 * <b>Copyright:</b> Copyright (c) 2005
 * </p>
 * <p>
 * <b>Company:</b> Auster Solutions
 * </p>
 * 
 * @author etirelli
 * @version $Id: RequestSender.java 1355 2007-04-19 21:21:50Z pvieira $
 */
public class RequestSender {

	private static Logger log = null;

	private static I18n i18n = I18n.getInstance(RequestSender.class);

	public static final String CONFIG_JNDI_TOPIC_NAME = "jndi.topic.processcontrol";

	public static final String CONFIG_JNDI_QUEUE_NAME = "jndi.queue";

	public static final String CONFIG_JNDI_FACTORY_NAME = "jndi.factory";

	protected static final String REQUEST_SIZE_PROP = "request.size";

	protected static final String REQUEST_CYCLE_ID_PROP = "cycle.id";

	protected static final String REQUEST_CMDLINE_TOKEN_PROP = "cmdline.token";

	private BlockingQueue syncronizer = new SynchronousQueue();

	private String token = null;

	// private SirsAccountParser sirsHandler = null;

	/**
	 * Public constructor
	 */
	public RequestSender() {
	}

	/**
	 * Loads configuration file
	 * 
	 * @param configFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NamingException
	 */
	protected static Properties loadConfigFile(String configFile)
			throws FileNotFoundException, IOException, NamingException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(configFile)));
		return properties;
	}

	/**
	 * Create CLI Option objects for command line parsing
	 * 
	 * @return Options list of Option Objects
	 */
	protected static Options createOptions() {
		OptionBuilder.withArgName("filemask");
		OptionBuilder.withLongOpt("filemask");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Especifica os arquivos a serem processados. Aceita os curingas * e ?.");
		Option mask = OptionBuilder.create("f");

		OptionBuilder.withArgName("userId");
		OptionBuilder.withLongOpt("userId");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Usuario dono da requisicao que sera criada.");
		Option user = OptionBuilder.create("u");

		OptionBuilder.withArgName("e-mail");
		OptionBuilder.withLongOpt("email");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Lista separada por virgula, com mails para os quais enviar a notificacao de fim de processamento.");
		Option mail = OptionBuilder.create("m");

		OptionBuilder.withArgName("output");
		OptionBuilder.withLongOpt("output");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Formato do resultado. Os valores possiveis sao \"asbck\" e \"aspva\".");
		Option output = OptionBuilder.create("o");

		OptionBuilder.withArgName("config");
		OptionBuilder.withLongOpt("config");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Arquivo de configuração contendo informações de acesso ao servidor de billcheckout");
		Option config = OptionBuilder.create("c");

//		OptionBuilder.withArgName("layout");
//		OptionBuilder.withLongOpt("layout");
//		OptionBuilder.hasArg();
//		OptionBuilder.withDescription("Arquivo de descrição do formato SIRS");
//		Option layout = OptionBuilder.create("l");

		OptionBuilder.withDescription("Mostra o help de uso.");
		OptionBuilder.withLongOpt("help");
		Option help = OptionBuilder.create("h");

		Options options = new Options();
		options.addOption(mask);
		options.addOption(user);
		options.addOption(mail);
		options.addOption(output);
		options.addOption(config);
//		options.addOption(layout);
		options.addOption(help);
		return options;
	}

	/**
	 * Prints command line help
	 * 
	 * @param options
	 *            list of available options
	 */
	protected static void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(RequestSender.class.getName(), options);
	}

	/**
	 * Creates the file set based on the specified mask
	 * 
	 * @param mask
	 *            mask to select files to process
	 * @return List<String> with absolute filename paths for files to process
	 */
	protected Set createFileSet(String mask) {
		log.info(i18n.getString("cmdline.reqmanager.creatingFileList", mask));
		Set filelist = new HashSet();
		String basedir = ".";
		if (mask.startsWith(File.separator)) {
			basedir = File.separator;
			mask = mask.substring(1);
		} else if (mask.charAt(1) == ':') {
			basedir = mask.substring(0, 3);
			mask = mask.substring(3);
		}
		File[] files = FileSet.getFiles(basedir, mask);
		for (int i = 0; i < files.length; i++) {
			log.debug(i18n.getString("cmdline.reqmanager.fileFound", files[i].getAbsolutePath()));
			filelist.add(files[i].getAbsolutePath());
		}
		log.info(i18n.getString("cmdline.reqmanager.totalFilesFound", new Integer(filelist.size())));
		return filelist;
	}

	/**
	 * Creates and returns a map of output formats
	 * 
	 * @param The
	 *            string containing the comma separated list of formats
	 * @return a Map<String, String> containing <"format", "true">
	 */
	private Map getOutputMap(String _options) {
		String[] format = _options.split(",");
		Map outputMap = new HashMap();
		for (int i = 0; i < format.length; i++) {
			if ("asbck".equals(format[i])) {
				outputMap.put("requests.format.bck", "true");
				log.debug(i18n
						.getString("cmdline.reqmanager.output", format[i]));
			} else if ("aspva".equals(format[i])) {
				outputMap.put("requests.format.pva", "true");
				log.debug(i18n.getString("cmdline.reqmanager.output", format[i]));
			} else {
				log.warn(i18n.getString("cmdline.reqmanager.output.unknown",
						format[i]));
			}
		}
		return outputMap;
	}

	/**
	 * Adds the notifications to the web request
	 * 
	 * @param line
	 * @param builder
	 */
	private void addNotifications(String notificationList,
			WebRequestBuilder builder) {
		String[] mail = notificationList.split(",");
		for (int i = 0; i < mail.length; i++) {
			NotificationEmail email = new NotificationEmail();
			email.setEmailAddress(mail[i]);
			builder.addEmailNotification(email);
			log.info(i18n.getString("cmdline.reqmanager.notification.added", mail[i]));
		}
	}

	/**
	 * Creates the actual request of files to be processed
	 * 
	 * @param line
	 *            parsed command line with request options
	 * @throws JMSException
	 * @throws NamingException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws NoFilesFoundException
	 * @throws SendMessageException
	 * @throws MissingResourceException
	 * @throws MissConfigurationException
	 * @throws CorruptedInformationException
	 */
	protected void createRequest(CommandLine line) throws JMSException,
			NamingException, FileNotFoundException, IOException,
			NoFilesFoundException, SendMessageException,
			MissConfigurationException, MissingResourceException,
			CorruptedInformationException {

		log.info(i18n.getString("cmdline.reqmanager.newrequest"));

		// Create a Unique token identifying this commandline run to be inserted
		// as
		// additional info into the request
		token = createToken();
		// Creating the helper builder
		WebRequestBuilder builder = new WebRequestBuilder();
		builder.buildWebRequest(0);

		// finish notifications - like e-mail and others
		if (line.hasOption("m")) {
			this.addNotifications(line.getOptionValue("m"), builder);
		}

		// // output formats
		Map outputMap = this.getOutputMap(line.getOptionValue("o"));
		builder.setFormatsMap(outputMap);

		// files
		String filemask = line.getOptionValue("f");
		Set inputFiles = this.createFileSet(filemask);
		if ((inputFiles == null) || (inputFiles.size() <= 0)) {
			System.err.println(i18n.getString(
					"cmdline.reqmanager.noFilesFound", filemask));
			throw new NoFilesFoundException();
		}
		builder.setInputFilesSet(inputFiles);

		// request list (accounts)
		// this.addRequestList(builder, outputMap, inputFiles,
		// line.getOptionValue("l"));

		log.debug(i18n.getString("cmdline.reqmanager.requestObjectCreated"));

		log.debug(i18n.getString("cmdline.reqmanager.callingBusinessObjects"));

		Properties p = new Properties();
		p.load(new FileInputStream((String) line.getOptionValue("c")));

		// Register on topic
		String tName = p.getProperty(CONFIG_JNDI_TOPIC_NAME);
		if ((tName != null) && (!tName.equals(""))) {
			JMSMessageTopicSender topic = new JMSMessageTopicSender(p
					.getProperty(CONFIG_JNDI_FACTORY_NAME), syncronizer);
			topic.connect(p, p.getProperty(CONFIG_JNDI_TOPIC_NAME));
		} else {
			System.err.println(i18n
					.getString("cmdline.reqmanager.topicnotconfigured"));
			throw new MissConfigurationException(i18n
					.getString("cmdline.reqmanager.topicnotconfigured"));
		}

		// Avisando JMS Queue de novo Request Creation
		JMSMessageSender sender = new JMSMessageSender(p
				.getProperty(CONFIG_JNDI_FACTORY_NAME));
		sender.connect(p, p.getProperty(CONFIG_JNDI_QUEUE_NAME));
		RequestCreation message = new RequestCreation(line.getOptionValue("u"),
				builder.getWebRequest());

		log.info("Sending request with "
				+ builder.getWebRequest().getInputFiles().size()
				+ " files to JMS Queue.");
		sender.sendMessage(message);

		log.info(i18n.getString("cmdline.reqmanager.requestCreated"));

	}

	/**
	 * @return
	 */
	private String createToken() {
		Date date = new Date();
		return new Long(date.getTime()).toString();
	}

	/**
	 * Outputs command line options to the log file
	 * 
	 * @param line
	 */
	public static void logCommandLine(CommandLine line) {
		log.info(i18n.getString("cmdline.reqmanager.args"));
		Option[] options = line.getOptions();
		for (int i = 0; i < options.length; i++) {
			log.info("PARAMETER: [" + options[i].getOpt() + "/"
					+ options[i].getLongOpt() + "] = [" + options[i].getValue()
					+ "]");
		}
	}

	/**
	 * Command line entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// for (int i=0; i < args.length;i++) {
		// System.out.println("I:"+ i + ".Args:" + args[i]);
		// }
		int exitcode = 0;
		Options options = RequestSender.createOptions();
		// create the parser
		CommandLineParser parser = new PosixParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			if ((line.hasOption("h"))
					|| (!(line.hasOption("f") && line.hasOption("u")
							&& line.hasOption("o") && line.hasOption("c")))) {
				RequestSender.printHelp(options);
			} else {
				LogFactory
						.configureLogSystem((String) line.getOptionValue("c"));
				log = LogFactory.getLogger(RequestSender.class);

				RequestSender.logCommandLine(line);
				RequestSender manager = new RequestSender();
				manager.createRequest(line);

				// Now, let's wait for Billcheckout server to finish this
				// transaction
				// the next line (syncronizer.take() blocks until someone puts
				// something
				// on syncrnous queue
				Object obj = manager.syncronizer.take();
				if (obj instanceof Exception) {
					// xiiii... we got problems....
					log.fatal(obj);
					exitcode = 12;
				} else {
					ObjectMessage topicMessage = (ObjectMessage) obj;

					// Good...we did receive a notification
					topicMessage.acknowledge();

					FlowControlMessage response = (FlowControlMessage) topicMessage
							.getObject();
					Stage lastStage = (Stage) response.getStages().get(
							(int) response.getCurrentStage() - 1);
					exitcode = (int) lastStage.getReturnCode();
					log.info("Source Message:" + lastStage.getTextMessage()
							+ " coming from class:" + lastStage.getClassName());
					log.info("Acknoledge date of transaction finished:"
							+ lastStage.getAcknowledgeDate());
					if (lastStage.hasException()) {
						log.error("The arriving message has an exception. See below:");
						lastStage.getException().printStackTrace();
					}
				}
			}
		} catch (ParseException e) {
			// oops, something went wrong
			// no processing takes place on server
			// Wrong command line :) :) :)
			System.out.println(i18n.getString("cmdline.reqmanager.parseError"));
			System.out.println(e.getMessage());
			RequestSender.printHelp(options);
			exitcode = 1;
		} catch (JMSException e) {
			// Some JMS Exception
			log.fatal(i18n.getString("cmdline.reqmanager.persistenceError"), e);
			System.err.println(i18n
					.getString("cmdline.reqmanager.persistenceError"));
			exitcode = 2;
		} catch (NamingException e) {
			// JNDI Lookup failed
			log.fatal(i18n.getString("cmdline.reqmanager.userManagerError"), e);
			System.err.println(i18n
					.getString("cmdline.reqmanager.userManagerError"));
			exitcode = 3;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			exitcode = 4;
		} catch (IOException e) {
			e.printStackTrace();
			exitcode = 5;
		} catch (NoFilesFoundException e) {
			// no processing takes place on server
			log.fatal(i18n.getString("cmdline.reqmanager.nofiles"), e);
			System.err.println(i18n.getString("cmdline.reqmanager.nofiles"));
			exitcode = 6;
		} catch (SendMessageException e) {
			// failed to connect to JMS Server
			log
					.fatal(i18n
							.getString("cmdline.reqmanager.sendmessagefailed"),
							e);
			System.err.println(i18n
					.getString("cmdline.reqmanager.sendmessagefailed"));
			exitcode = 7;
		} catch (MissConfigurationException e) {
			// There is no topic configuration set
			log.error(i18n.getString("cmdline.reqmanager.topicnotconfigured"),
					e);
			System.err.println(i18n
					.getString("cmdline.reqmanager.topicnotconfigured"));
			exitcode = 8;
		} catch (InterruptedException e) {
			// because blocking queue
			exitcode = 9;
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			// because JMSTopicSender.sendMessage()
			exitcode = 10;
			e.printStackTrace();
		} catch (ClassCastException e) {
			// because some FDP put an unexpected object in topic....
			exitcode = 11;
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// Cause JMS went down in the middle of processing
			exitcode = 12;
			e.printStackTrace();
		} catch (MissingResourceException e) {
			exitcode = 13;
			e.printStackTrace();
		} catch (CorruptedInformationException e) {
			exitcode = 14;
			e.printStackTrace();
		}

		shutdown(exitcode);

	}

	private static void shutdown(int exitcode) {
		if (exitcode != 0) {
			if (log != null) {
				log.error("Leaving with exit code:" + exitcode);
			}
		}
		System.out.println("Leaving with exit code:" + exitcode);
		System.exit(exitcode);
	}
}
