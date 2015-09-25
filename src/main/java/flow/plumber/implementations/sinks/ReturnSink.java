package flow.plumber.implementations.sinks;

import flow.plumber.Sink;

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
	public void pump(String name, List<T> data)
	{
		this.stuff = data;
	}

	public List<T> getStuff()
	{
		return this.stuff;
	}
}
