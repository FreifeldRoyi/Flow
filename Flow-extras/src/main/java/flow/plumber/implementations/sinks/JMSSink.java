package flow.plumber.implementations.sinks;

import flow.plumber.Sink;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import java.util.Collection;

/**
 * @author royif
 * @since 29/04/16
 */
public class JMSSink<T> extends Sink<T>
{
	private ConnectionFactory factory;

	@Inject
	public JMSSink(ConnectionFactory factory)
	{
		super();
		this.factory = factory;
	}

	//TODO allow injections for named elements
	public JMSSink(String name, ConnectionFactory factory)
	{
		super(name);
		this.factory = factory;
	}

	@Override
	public void pump(String name, Collection<T> data)
	{

	}
}
