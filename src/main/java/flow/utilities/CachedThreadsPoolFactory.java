package flow.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

/**
 * @author Freifeld Royi
 * @since 30-Sep-15.
 */
public class CachedThreadsPoolFactory implements ThreadPoolFactory
{
	public final static String FORK_JOIN_NAME = CachedThreadsPoolFactory.class.getName() + "_ForkJoinPool";

	private Map<String, ExecutorService> executors;

	private CachedThreadsPoolFactory()
	{
		this.executors = new HashMap<>();
	}

	public static CachedThreadsPoolFactory getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	@Override
	public ForkJoinPool getForkJoinPool()
	{
		return this.createNewExecutor(FORK_JOIN_NAME, ForkJoinPool::commonPool, ForkJoinPool.class);
	}

	@Override
	public ExecutorService getSingleThreadedExecutor(String name)
	{
		return this.createNewExecutor(name, Executors::newSingleThreadExecutor, ExecutorService.class);
	}

	@Override
	public ExecutorService getExecutorService(String name, int poolSize)
	{
		return this.createNewExecutor(name, () -> Executors.newFixedThreadPool(poolSize), ExecutorService.class);
	}

	@Override
	public ExecutorService getExecutorService(String name)
	{
		return this.createNewExecutor(name, Executors::newCachedThreadPool, ExecutorService.class);
	}

	@Override
	public void close()
	{
		this.executors.values().forEach(ExecutorService::shutdown);
	}

	private <T extends ExecutorService> T createNewExecutor(String name, Supplier<T> supplier, Class<T> cls)
	{
		T toReturn = null;
		if (name != null)
		{
			if (!this.executors.containsKey(name))
			{
				this.executors.put(name, supplier.get());
			}

			toReturn = cls.cast(this.executors.get(name));
		}

		return toReturn;
	}

	private static class SingletonHolder
	{
		private static CachedThreadsPoolFactory INSTANCE = new CachedThreadsPoolFactory();
	}
}
