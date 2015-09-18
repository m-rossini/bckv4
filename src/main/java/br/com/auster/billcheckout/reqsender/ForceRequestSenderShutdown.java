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
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
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

import br.com.auster.billcheckout.reqsender.exceptions.MissConfigurationException;
import br.com.auster.billcheckout.reqsender.exceptions.SendMessageException;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.util.I18n;
import br.com.auster.dware.console.flowcontrol.FlowControlMessage;
import br.com.auster.dware.console.flowcontrol.Stage;

/**
 * <p>
 * <b>Title:</b> Ends a Command Line Request Manager
 * </p>
 * <p>
 * <b>Description:</b> A Request Manager interface for stoping a command line
 * </p>
 * <p>
 * <b>Copyright:</b> Copyright (c) 2007
 * </p>
 * <p>
 * <b>Company:</b> Auster Solutions
 * </p>
 * 
 * @author mtengelm
 */
public class ForceRequestSenderShutdown {
	private static Logger log = null;

	private static I18n i18n = I18n.getInstance(ForceRequestSenderShutdown.class);

	public static final String CONFIG_JNDI_TOPIC_NAME = "jndi.topic.processcontrol";

	public static final String CONFIG_JNDI_FACTORY_NAME = "jndi.factory";

	// private SirsAccountParser sirsHandler = null;

	/**
	 * Public constructor
	 */
	public ForceRequestSenderShutdown() {
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
		OptionBuilder.withArgName("config");
		OptionBuilder.withLongOpt("config");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired();
		OptionBuilder
				.withDescription("Arquivo de configuração contendo informações de acesso ao servidor de billcheckout");
		Option config = OptionBuilder.create("c");

		OptionBuilder.withDescription("Mostra o help de uso.");
		Option help = OptionBuilder.create("h");

		OptionBuilder
				.withDescription("Indica se o término será ou não bem sucedido. True é bem sucedido, False é falha.");
		OptionBuilder.withLongOpt("success");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired();
		Option success = OptionBuilder.create("s");

		OptionBuilder
				.withDescription("Informe o transaction-id(Também conhecido como request-id) a ser finalizado.");
		OptionBuilder.withLongOpt("transaction");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(true);
		Option transaction = OptionBuilder.create("t");

		Options options = new Options();
		options.addOption(config);
		options.addOption(success);
		options.addOption(transaction);
		options.addOption(help);
		return options;
	}

	/**
	 * Prints command line help
	 * 
	 * @param options
	 *          list of available options
	 */
	protected static void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(ForceRequestSenderShutdown.class.getName(), options);
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws JMSException
	 * @throws NamingException
	 * @throws SendMessageException
	 * @throws MissingResourceException
	 * @throws MissConfigurationException
	 */
	protected void finishComandLine(CommandLine line)
			throws FileNotFoundException, IOException, SendMessageException,
			NamingException, JMSException, MissConfigurationException,
			MissingResourceException {

		log.info("**************************");
		log.info("Shutting down command line");

		Properties p = new Properties();
		p.load(new FileInputStream((String) line.getOptionValue("c")));

		// Register on topic
		String tName = p.getProperty(CONFIG_JNDI_TOPIC_NAME);
		if ((tName != null) && (!tName.equals(""))) {
			Context jndiContext = null;
			Topic topic = null;
			TopicConnection topicConnection = null;
			TopicSession topicSession = null;
			TopicPublisher publisher = null;
			try {
				jndiContext = new InitialContext(p);
				TopicConnectionFactory connectionFactory = (TopicConnectionFactory) jndiContext
						.lookup(p.getProperty(CONFIG_JNDI_FACTORY_NAME));
				topic = (Topic) jndiContext.lookup(p
						.getProperty(CONFIG_JNDI_TOPIC_NAME));
				topicConnection = connectionFactory.createTopicConnection();
				topicSession = topicConnection.createTopicSession(false,
						Session.AUTO_ACKNOWLEDGE);
				publisher = topicSession.createPublisher(topic);

				ObjectMessage msg = topicSession.createObjectMessage();

				FlowControlMessage message = new FlowControlMessage(line
						.getOptionValue("t"));
				Stage stage = new Stage();
				stage.setException(null);
				stage.setTextMessage("Finished by Force Command Line.");
				stage.setClassName(this.getClass().getName());

				if (Boolean.parseBoolean(line.getOptionValue("s"))) {
					stage.setReturnCode(Stage.PROC_OK);
				} else {
					stage.setReturnCode(Stage.PROC_NOT_OK);
				}
				stage.setAcknowledgeDate(Calendar.getInstance());
				message.addStage(stage);

				msg.setObject(message);
				// sending message
				publisher.publish(msg);

				topicConnection.start();
			} catch (Throwable e) {
				log.fatal("Error connecting to JMS Server.");
				throw new SendMessageException(e);
			} finally {
				if (publisher != null) {
					publisher.close();
				}
				if (topicSession != null) {
					topicSession.close();
				}
				if (topicConnection != null) {
					topicConnection.close();
				}
			}
		} else {
			System.err.println(i18n
					.getString("cmdline.reqmanager.topicnotconfigured"));
			throw new MissConfigurationException(i18n
					.getString("cmdline.reqmanager.topicnotconfigured"));
		}

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

		int exitcode = 0;
		Options options = ForceRequestSenderShutdown.createOptions();
		// create the parser
		CommandLineParser parser = new PosixParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			LogFactory.configureLogSystem((String) line.getOptionValue("c"));
			log = LogFactory.getLogger(ForceRequestSenderShutdown.class);

			ForceRequestSenderShutdown.logCommandLine(line);
			ForceRequestSenderShutdown manager = new ForceRequestSenderShutdown();
			manager.finishComandLine(line);
		} catch (ParseException e) {
			// oops, something went wrong
			// no processing takes place on server
			// Wrong command line :) :) :)
			System.out.println(i18n.getString("cmdline.reqmanager.parseError"));
			System.out.println(e.getMessage());
			ForceRequestSenderShutdown.printHelp(options);
			exitcode = 1;
		} catch (JMSException e) {
			// Some JMS Exception
			log.error(i18n.getString("cmdline.reqmanager.persistenceError"), e);
			exitcode = 2;
		} catch (NamingException e) {
			// JNDI Lookup failed
			log.error(i18n.getString("cmdline.reqmanager.userManagerError"), e);
			exitcode = 3;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			exitcode = 4;
		} catch (IOException e) {
			e.printStackTrace();
			exitcode = 5;
		} catch (SendMessageException e) {
			// failed to connect to JMS Server
			log
					.error(i18n
							.getString("cmdline.reqmanager.sendmessagefailed"),
							e);
			exitcode = 7;
			e.printStackTrace();
		} catch (MissConfigurationException e) {
			// There is no topic configuration set
			exitcode = 8;
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			// because JMSTopicSender.sendMessage()
			exitcode = 10;
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
