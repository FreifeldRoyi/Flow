package flow.plumber.implementations.sinks;

import flow.plumber.Sink;

import java.util.Collection;

/**
 * @author royif
 * @since 29/04/16.
 */
public class JMSSink<T> extends Sink<T>
{
	public JMSSink()
	{
	}

	public JMSSink(String name)
	{
		super(name);
	}

	@Override
	public void pump(String name, Collection<T> data)
	{

	}
}
