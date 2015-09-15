package flow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public class SimpleState
{
	final private Map<String, String> propertyMapper;

	public SimpleState(String propertiesFile)
	{
		this.propertyMapper = new ConcurrentHashMap<>();
		this.init(propertiesFile);
	}

	private void init(String propertiesFile)
	{
		// TODO read file
	}
}
