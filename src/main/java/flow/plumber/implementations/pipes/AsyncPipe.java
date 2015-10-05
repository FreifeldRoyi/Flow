package flow.plumber.implementations.pipes;

import flow.plumber.DecoratedFlowObject;
import flow.utilities.CachedThreadsPoolFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiFunction;

/**
 * @author Freifeld Royi
 * @since 25-Sep-15.
 */
public abstract class AsyncPipe<T, E> extends DecoratedFlowObject<T>
{
	private BiFunction<String, Collection<T>, List<RecursiveTask<E>>> taskProducer;

	public AsyncPipe()
	{
		super();
		this.taskProducer = this.createTaskProducer();
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		List<RecursiveTask<E>> tasks = this.taskProducer.apply(name, data);
		for (RecursiveTask<E> task : tasks)
		{
			// Note that there is no need to save the ForkJoinTask that is returned since 'task' is returned
			CachedThreadsPoolFactory.getInstance().getForkJoinPool().submit(task);
		}
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

	protected abstract BiFunction<String, Collection<T>, List<RecursiveTask<E>>> createTaskProducer();
}
