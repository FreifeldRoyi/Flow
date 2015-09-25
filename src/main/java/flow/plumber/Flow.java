package flow.plumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Freifeld Royi
 * @since 12-Sep-15.
 */
public class Flow<T> implements AutoCloseable
{
	private final Source source;
	private final List<DecoratedFlowObject> pipes;
	private final MultiSink sinks;

	private Flow(Source source, List<DecoratedFlowObject> pipes, MultiSink multiSink)
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

	public static class Plumber<E>
	{
		private Source source;
		private List<Sink> sinks;
		private List<DecoratedFlowObject> pipes;

		public Plumber()
		{
			this.init(null, null);
		}

		public Plumber(Source source, Sink... sinks)
		{
			List<Sink> sinkList = Arrays.asList(sinks);
			this.init(source, sinkList);
		}

		public Plumber<E> from(Source source)
		{
			this.source = source;
			return this;
		}

		public Plumber<E> to(Sink output)
		{
			this.sinks.add(output);
			return this;
		}

		public Plumber<E> pipe(DecoratedFlowObject pipe)
		{
			this.pipes.add(pipe);
			return this;
		}

		public Flow<E> build()
		{
			if (this.source == null)
			{
				throw new IllegalStateException("Cannot build Flow without a source");
			}
			else if (this.sinks == null || this.sinks.isEmpty())
			{
				throw new IllegalStateException("Cannot build a Flow without at least one Sink");
			}

			MultiSink multiSink = new MultiSink(this.sinks);
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

		private void init(Source source, List<Sink> sinks)
		{
			this.source = source;
			this.pipes = new ArrayList<>();
			this.sinks = Optional.ofNullable(sinks).orElse(new ArrayList<>());
		}
	}
}
