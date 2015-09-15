package flow.plumber;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public final class MultiSink extends Sink
{
	protected final Map<String, Sink> sinks;

	public MultiSink(List<Sink> sinks)
	{
		super("*");
		this.sinks =
				sinks.stream().collect(Collectors.toConcurrentMap(Sink::getName, sink -> sink));
	}

	@Override
	public void pump(Object... data)
	{
		this.sinks.forEach((key, val) -> val.pump(data));
	}
}
