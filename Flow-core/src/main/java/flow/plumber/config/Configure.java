package flow.plumber.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author royif
 * @since 06/05/16
 */
public class Configure
{
	private static final Logger logger = LoggerFactory.getLogger(Configure.class);
	private Properties properties;

	/**
	 * Initializes the properties held by this class
	 *
	 * @param filename     - non null path to a properties file
	 * @param defaultsFile - non null path to defaults file
	 */
	public Configure(String filename, String defaultsFile)
	{
		logger.info("Configuring the system. Reading {} file using {} as default", filename, defaultsFile);
		try (InputStream defaultsStream = getClass().getClassLoader().getResourceAsStream(defaultsFile);
				InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream(filename))
		{
			Properties defaults = new Properties();
			defaults.load(defaultsStream);
			this.properties = new Properties(defaults);
			if (propertiesStream != null)
			{
				this.properties.load(propertiesStream);
			}
		}
		catch (IOException e)
		{
			logger.error("Exception", e);
			throw new RuntimeException(e);
		}
	}

	public String getProperty(String propertyName)
	{
		return this.properties.getProperty(propertyName);
	}
}
