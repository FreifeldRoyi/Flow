package flow.plumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Freifeld Royi
 * @since 12-Sep-15.
 */
public class Flow<T, R> implements AutoCloseable
{
	private final Source<T> source;
	private final List<DecoratedFlowObject> pipes;
	private final MultiSink<R> sinks;

	private Flow(Source<T> source, List<DecoratedFlowObject> pipes, MultiSink<R> multiSink)
	{
		this.source = source;
		this.pipes = pipes;
		this.sinks = multiSink;
	}

	public void start()
	{
		this.sinks.start();
		this.pipes.forEach(DecoratedFlowObject::start);
		this.source.start();
	}

	@Override
	public void close()
	{
		this.source.close();
		this.pipes.forEach(DecoratedFlowObject::close);
		this.sinks.close();
	}

	public static class Plumber<T, R>
	{
		private Source<T> source;
		private List<Sink<R>> sinks;
		private List<DecoratedFlowObject> pipes;

		public Plumber()
		{
			this.init(null, null);
		}

		public Plumber(Source<T> source, List<Sink<R>> sinks)
		{
			this.init(source, sinks);
		}

		public Plumber(Source<T> source, Sink<R> sink)
		{
			this.init(source, Arrays.asList(sink));
		}

		public Plumber<T, R> from(Source<T> source)
		{
			this.source = source;
			return this;
		}

		public Plumber<T, R> to(Sink<R> output)
		{
			this.sinks.add(output);
			return this;
		}

		/**
		 * Add a pipe to the flow.
		 * Note that the type is raw. Type safety is left for the user
		 *
		 * @param pipe - a pipe
		 * @return this
		 */
		@SuppressWarnings("raw")
		public Plumber<T, R> pipe(DecoratedFlowObject pipe)
		{
			this.pipes.add(pipe);
			return this;
		}

		public Flow<T, R> build()
		{
			if (this.source == null)
			{
				throw new IllegalStateException("Cannot build Flow without a source");
			}
			else if (this.sinks == null || this.sinks.isEmpty())
			{
				throw new IllegalStateException("Cannot build a Flow without at least one Sink");
			}

			MultiSink<R> multiSink = new MultiSink<>(this.sinks);
			if (!this.pipes.isEmpty()) // at least one reservoir was added
			{
				this.source.setNext(this.pipes.get(0));
				for (int i = 1; i < this.pipes.size(); ++i)
				{
					this.pipes.get(i - 1).setNext(this.pipes.get(i));
				}
				this.pipes.get(this.pipes.size() - 1).setNext(multiSink);
			}
			else
			{
				this.source.setNext(multiSink);
			}
			return new Flow<>(this.source, this.pipes, multiSink);
		}

		private void init(Source<T> source, List<Sink<R>> sinks)
		{
			this.source = source;
			this.pipes = new ArrayList<>();
			this.sinks = Optional.ofNullable(sinks).orElse(new ArrayList<>());
		}
	}
}
