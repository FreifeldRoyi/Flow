package flow.plumber.implementations.pipes;

import flow.plumber.DecoratedFlowObject;
import flow.plumber.Flow;
import flow.plumber.Source;
import flow.plumber.implementations.sinks.ReturnSink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Freifeld Royi
 * @since 03-Oct-15.
 */
public class AsyncPipeTest
{
	private final static int MAX_VALUE = 30;
	private final static int THRESHOLD = 20;

	private Flow<Integer, Long> syncFlow;
	private Flow<Integer, Long> asyncFlow;
	private ReturnSink<Long> syncFlowReturnSink;
	private ReturnSink<Long> asyncFlowReturnSink;

	@BeforeClass
	public void setup()
	{
		this.syncFlowReturnSink = new ReturnSink<>();
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

		this.asyncFlowReturnSink = new ReturnSink<>();
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
		long start = System.currentTimeMillis();
		this.syncFlow.start();
		long end = System.currentTimeMillis();
		List<Long> syncStuff = this.syncFlowReturnSink.getStuff();
		System.out.println(syncStuff);
		System.out.println((end - start) / 1000.0);

		this.asyncFlow.start();
		System.out.println("\n\n\n\n\n\n\n");
		System.out.println(this.asyncFlowReturnSink.getStuff());
		this.asyncFlow.close();
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

	private static class FibonacciAsyncPipe extends AsyncPipe<Integer, Long>
	{
		public FibonacciAsyncPipe()
		{
			super();
		}

		@Override
		protected BiFunction<String, Collection<Integer>, List<RecursiveTask<Long>>> createTaskProducer()
		{
			return (s, intList) -> {
				System.out.println(intList);

				return intList.stream().map(n -> new FibonacciTask(n, s, (s1, longs) -> {
					System.out.println(longs);
					nextFlow(s1, longs);
				})).collect(Collectors.toList());
			};
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
			this.init(starterFibNumber, name, (s1, longs1) -> {});
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
			this.onCompleteFunction.accept(this.name, Arrays.asList(result));
		}
	}
}
