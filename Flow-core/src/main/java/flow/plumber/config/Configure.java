package flow.plumber.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author royif
 * @since 06/05/16
 */
public class Configure
{
	private Properties properties;

	/**
	 * Initializes the properties held by this class
	 *
	 * @param filename     - non null path to a properties file
	 * @param defaultsFile - non null path to defaults file
	 */
	public Configure(String filename, String defaultsFile)
	{
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
		catch (FileNotFoundException e)
		{
			//TODO log
			throw new RuntimeException(e);
		}
		catch (IOException e)
		{
			//TODO log
			throw new RuntimeException(e);
		}
	}

	public String getProperty(String propertyName)
	{
		return this.properties.getProperty(propertyName);
	}
}
