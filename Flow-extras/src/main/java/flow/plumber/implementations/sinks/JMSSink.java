package flow.plumber.implementations.sinks;

import flow.plumber.Sink;
import flow.plumber.injections.ElementNamingFactory;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Topic;
import java.util.Collection;

/**
 * @author royif
 * @since 29/04/16
 */
public class JMSSink<T> extends Sink<T>
{
	private final ConnectionFactory factory;
	private final JMSContext context;
	private final JMSProducer producer;
	private final Topic topic;

	@Inject
	public JMSSink(ElementNamingFactory namingFactory, ConnectionFactory factory, Topic topic)
	{
		super(namingFactory);
		this.factory = factory;
		this.context = this.factory.createContext();
		this.producer = this.context.createProducer();
		this.topic = topic;
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		data.stream().forEach(t -> this.producer.send(topic, t.toString()));
	}
}
