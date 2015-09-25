package flow.plumber.implementations.sinks;

import flow.plumber.Sink;

/**
 * @author Freifeld Royi
 * @since 25-Sep-15.
 */
public class ReturnSink<T> extends Sink
{
	private T[] stuff;

	public ReturnSink()
	{
		this.stuff = null;
	}

	@Override
	public void pump(Object... data)
	{
		this.stuff = (T[]) data;
	}

	public T[] getStuff()
	{
		return this.stuff;
	}
}
