package flow.plumber.implementations.pipes;

import flow.plumber.DecoratedFlowObject;
import flow.plumber.Flow;
import flow.plumber.Source;
import flow.plumber.implementations.sinks.ReturnSink;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Freifeld Royi
 * @since 03-Oct-15.
 */
public class AsyncPipeTest
{
	private final static int DEFAULT_COUNT_OUT_COUNTER = 50;
	private final static int ASYNC_EQUAL_COUNT_THRESHOLD = 5;

	private final static int MAX_VALUE = 30;
	private final static int THRESHOLD = 20;

	private Flow<Integer, Long> syncFlow;
	private Flow<Integer, Long> asyncFlow;
	private ReturnSink<Long> syncFlowReturnSink;
	private ReturnSink<Long> asyncFlowReturnSink;

	@BeforeClass
	public void setup()
	{
		this.syncFlowReturnSink = new ReturnSink<>(Long::compare, false, false);
		this.syncFlow = new Flow.Plumber<>(new Source<Integer>()
		{
			@Override
			public void start()
			{
				this.pump("*", IntStream.range(0, MAX_VALUE).boxed().collect(Collectors.toList()));
			}
		}, syncFlowReturnSink).pipe(new DecoratedFlowObject<Integer>()
		{
			@Override
			public void pump(String name, Collection<Integer> data)
			{
				this.nextFlow(name, data.stream().map(AsyncPipeTest::fibonacci).collect(Collectors.toList()));
			}
		}).build();

		this.asyncFlowReturnSink = new ReturnSink<>(Long::compare, true, true);
		this.asyncFlow = new Flow.Plumber<>(new Source<Integer>()
		{
			@Override
			public void start()
			{
				this.pump("*", IntStream.range(0, MAX_VALUE).boxed().collect(Collectors.toList()));
			}
		}, this.asyncFlowReturnSink).pipe(new FibonacciAsyncPipe()).build();
	}

	@Test
	public void dummyTest()
	{
		this.syncFlow.start();
		this.asyncFlow.start();
		asyncEqualTester(this.syncFlowReturnSink::getStuff, this.asyncFlowReturnSink::getStuff, (longs1, longs2) -> {
			List<Long> list1ShouldBeEmpty = new ArrayList<>(longs1);
			List<Long> list2ShouldBeEmpty = new ArrayList<>(longs2);
			longs1.forEach(list2ShouldBeEmpty::remove);
			longs2.forEach(list1ShouldBeEmpty::remove);
			return list2ShouldBeEmpty.isEmpty() && list1ShouldBeEmpty.isEmpty();
		});
		this.asyncFlow.close();
		this.syncFlow.close();
	}

	private static <T> void asyncEqualTester(Supplier<T> supplier1, Supplier<T> supplier2, BiPredicate<T, T> predicate)
	{
		int testingCounter = 0, countOutCounter = 0;

		while (testingCounter < ASYNC_EQUAL_COUNT_THRESHOLD && countOutCounter < DEFAULT_COUNT_OUT_COUNTER)
		{
			T obj1 = supplier1.get();
			T obj2 = supplier2.get();
			if (predicate.test(obj1, obj2))
			{
				++testingCounter;
			}
			else
			{
				testingCounter = 0;
			}
			++countOutCounter;
		}

		Assert.assertTrue(testingCounter >= ASYNC_EQUAL_COUNT_THRESHOLD);
	}

	private static long fibonacci(int n)
	{
		if (n <= 1)
		{
			return n;
		}
		else
		{
			return fibonacci(n - 1) + fibonacci(n - 2);
		}
	}

	private static class FibonacciAsyncPipe extends AsyncPipe<Integer, Long, FibonacciTask>
	{
		public FibonacciAsyncPipe()
		{
			super();
		}

		@Override
		protected BiFunction<String, Collection<Integer>, List<FibonacciTask>> createTaskProducer()
		{
			return (s, intList) -> intList.stream().map(n -> new FibonacciTask(n, s, this::nextFlow)).collect(Collectors.toList());
		}
	}

	private static class FibonacciTask extends RecursiveTask<Long>
	{
		private BiConsumer<String, Collection<Long>> onCompleteFunction;

		private int starterFibNumber;
		private String name;

		public FibonacciTask(int starterFibNumber, String name, BiConsumer<String, Collection<Long>> onCompleteFunction)
		{
			this.init(starterFibNumber, name, onCompleteFunction);
		}

		public FibonacciTask(int starterFibNumber, String name)
		{
			this.init(starterFibNumber, name, null);
		}

		@Override
		public Long compute()
		{
			Long result;
			if (this.starterFibNumber <= THRESHOLD)
			{
				result = fibonacci(this.starterFibNumber);
			}
			else
			{
				FibonacciTask nMin1 = new FibonacciTask(this.starterFibNumber - 1, this.name);
				FibonacciTask nMin2 = new FibonacciTask(this.starterFibNumber - 2, this.name);

				nMin1.fork();
				result = nMin2.compute() + nMin1.join();
			}

			this.onCompletion(result);
			return result;
		}

		private void init(int starterFibNumber, String name, BiConsumer<String, Collection<Long>> onCompleteFunction)
		{
			this.onCompleteFunction = onCompleteFunction;
			this.starterFibNumber = starterFibNumber;
			this.name = name;
		}

		private void onCompletion(Long result)
		{
			if (this.onCompleteFunction != null)
			{
				this.onCompleteFunction.accept(this.name, Collections.singletonList(result));
			}
		}
	}
}
