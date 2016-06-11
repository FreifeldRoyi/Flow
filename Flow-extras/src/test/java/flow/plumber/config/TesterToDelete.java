package flow.plumber.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import flow.plumber.implementations.sinks.JMSSink;
import flow.plumber.injection.jms.JMSModule;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import java.util.Arrays;

/**
 * @author royif
 * @since 07/05/16
 */
public class TesterToDelete
{
	public static void main(String[] args)
	{
		Injector injector = Guice.createInjector(new JMSModule());
		JMSSink<String> instance = injector.getInstance(JMSSink.class);
		instance.pump("asd", Arrays.asList("success","1","2","3","4","5","6","7","8"));
		System.out.println("done");
	}
}
