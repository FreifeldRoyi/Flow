package flow.utilities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Freifeld Royi
 * @since 29-Sep-15.
 */
public interface ThreadPoolFactory extends AutoCloseable
{
	default ForkJoinPool getForkJoinPool()
	{
		return ForkJoinPool.commonPool(); // stupid implementation
	}

	default ExecutorService getSingleThreadedExecutor(String name)
	{
		return Executors.newSingleThreadExecutor();
	}

	default ExecutorService getExecutorService(String name, int poolSize)
	{
		return Executors.newFixedThreadPool(poolSize);
	}

	default ExecutorService getExecutorService(String name)
	{
		return Executors.newCachedThreadPool();
	}

	@Override
	default void close()
	{
		// Do nothing!
	}
}