package flow.plumber.injection.jms;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.jms.ConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * @author royif
 * @since 06/05/16.
 */
public class JMSModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		//		bind(String.class).annotatedWith(Qualifiers.namedFlowElement(JMSSink.class,));
		//		bindConstant().annotatedWith(Names.named("sink/name.default"))
	}

	@Provides
	private ConnectionFactory provideConnectionFactory()
	{
		ConnectionFactory factory = null;
		try
		{
			Properties env = new Properties();
			InitialContext context = new InitialContext(env);
			factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		}
		catch (NamingException e)
		{
			throw new IllegalStateException(e);
		}

		return factory;
	}
}
