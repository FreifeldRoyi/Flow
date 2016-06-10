package flow.plumber.injection.jms;

import com.google.inject.Provides;
import flow.plumber.injections.FlowAbstractModule;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author royif
 * @since 06/05/16.
 */
public class JMSModule extends FlowAbstractModule
{
	private InitialContext context;

	public JMSModule()
	{
		try
		{
			this.context = new InitialContext();
		}
		catch (NamingException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Provides
	private ConnectionFactory provideConnectionFactory() throws NamingException
	{
		return (ConnectionFactory) this.context.lookup("ConnectionFactory");
	}

	@Provides
	private Topic provideTopic() throws NamingException
	{
		return (Topic) this.context.lookup("topics/topic");
	}
}
