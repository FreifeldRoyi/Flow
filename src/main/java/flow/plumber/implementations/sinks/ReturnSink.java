package flow.plumber.implementations.sinks;

import flow.plumber.Sink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Freifeld Royi
 * @since 25-Sep-15.
 */
public class ReturnSink<T> extends Sink<T>
{
	private List<T> stuff;

	public ReturnSink()
	{
		super();
		this.stuff = null;
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		this.stuff = new ArrayList<>(data);
	}

	public List<T> getStuff()
	{
		return this.stuff;
	}
}
