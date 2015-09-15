package flow.plumber.implementations;

import flow.plumber.Sink;

import java.util.Arrays;

/**
 * @author Freifeld Royi
 * @since 15-Sep-15.
 */
public class PrintOutSink extends Sink
{
	protected PrintOutSink(String name)
	{
		super(name);
	}

	@Override
	public void pump(Object... data)
	{
		Arrays.asList(data).forEach(System.out::println);
	}
}
