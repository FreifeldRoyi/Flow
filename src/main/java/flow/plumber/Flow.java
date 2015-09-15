package flow.plumber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Freifeld Royi
 * @since 12-Sep-15.
 */
public class Flow<T>
{
	private final Source source;
	private final List<Reservoir> reservoirs;
	private final MultiSink sinks;
	private final List<Predicate<T>> filters;
	private final Class<T> inputClass;

	private Flow(Source source, List<Reservoir> reservoirs, MultiSink multiSink,
			List<Predicate<T>> filters, Class<T> inputClass)
	{
		this.source = source;
		this.reservoirs = reservoirs;
		this.sinks = multiSink;
		this.filters = filters;
		this.inputClass = inputClass;
	}

	public static class Plumber<E>
	{
		private Source source;
		private List<Sink> sinks;
		private List<Reservoir> reservoirs; // TODO the generics can cause a problem here...
		private List<Predicate<E>> filters;

		public Plumber()
		{
			this.source = null;
			this.sinks = null;
			this.reservoirs = null;
		}

		public Plumber<E> from(Source source)
		{
			this.source = source;
			return this;
		}

		public Plumber<E> to(Sink output)
		{
			if (this.sinks == null)
			{
				this.sinks = new ArrayList<>();
			}
			this.sinks.add(output);

			return this;
		}

		public Plumber<E> reservoir(Reservoir reservoir)
		{
			if (this.reservoirs == null)
			{
				this.reservoirs = new ArrayList<>();
			}
			this.reservoirs.add(reservoir);

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
			if (this.reservoirs != null) // at least one reservoir was added
			{
				this.source.setNext(this.reservoirs.get(0));
				for (int i = 1; i < this.reservoirs.size(); ++i)
				{
					this.reservoirs.get(i - 1).setNext(this.reservoirs.get(i));
				}
				this.reservoirs.get(this.reservoirs.size() - 1).setNext(multiSink);
			}
			else
			{
				this.source.setNext(multiSink);
			}
			return new Flow<>(this.source, this.reservoirs, multiSink, filters, null);
		}

		public Plumber<E> filter(Predicate<E> predicate)
		{
			if (this.filters == null)
			{
				this.filters = new ArrayList<>();
			}
			this.filters.add(predicate);
			return this;
		}
	}
}
