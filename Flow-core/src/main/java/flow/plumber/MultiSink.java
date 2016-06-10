package flow.plumber;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public final class MultiSink<T> extends Sink<T>
{
	protected final Map<String, List<Sink<T>>> sinks;

	public MultiSink(List<? extends Sink<T>> sinks)
	{
		this("*", sinks);
	}

	public MultiSink(String name, List<? extends Sink<T>> sinks)
	{
		super(name);
		this.sinks = sinks.stream().collect(Collectors.groupingByConcurrent(Sink::getName));
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		List<Sink<T>> toSend = this.sinks.get(name);
		Objects.requireNonNull(toSend, "No Sinks with name " + name);

		toSend.forEach(sink -> sink.pump(name, data));
	}
}
