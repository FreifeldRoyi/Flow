package flow.plumber.implementations.pipes;

import flow.plumber.DecoratedFlowObject;
import flow.utilities.CachedThreadsPoolFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

/**
 * @author Freifeld Royi
 * @since 25-Sep-15.
 */
public abstract class AsyncPipe<T, E, F extends Future<E>> extends DecoratedFlowObject<T>
{
	private BiFunction<String, Collection<T>, List<F>> taskProducer;

	public AsyncPipe()
	{
		super();
		this.taskProducer = this.createTaskProducer();
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		this.thenApply(this.taskProducer.apply(name, data));

	}

	@Override
	public void start()
	{
		// Init the ForkJoinPool
		CachedThreadsPoolFactory.getInstance().getForkJoinPool();
	}

	@Override
	public void close()
	{
		CachedThreadsPoolFactory.getInstance().close();
	}

	protected abstract BiFunction<String, Collection<T>, List<F>> createTaskProducer();

	protected void thenApply(List<F> tasks)
	{
		// Default implementation to do nothing
	}
}
