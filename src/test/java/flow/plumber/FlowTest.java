package flow.plumber;

import flow.plumber.implementations.sinks.ReturnSink;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Freifeld Royi
 * @since 14-Sep-15.
 */
public class FlowTest
{
	private static final List<String> SIMPLE_DATA =
			Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
						  "z");
	private static final Source<String> SIMPLE_SOURCE = new Source<String>()
	{
		@Override
		public void pump(String name, Collection<String> data)
		{
			this.nextFlow(name, data);
		}

		@Override
		public void start()
		{
			this.pump("*", SIMPLE_DATA);
		}
	};

	private static final Source<String> SIMPLE_EMPTY_SOURCE = new Source<String>()
	{
		@Override
		public void pump(String name, Collection<String> data)
		{
			this.nextFlow(name, data);
		}

		@Override
		public void start()
		{
			this.pump("*", new ArrayList<>());
		}
	};

	@Test(groups = { "plumber" })
	public void plumberBuild_noPipes()
	{
		ReturnSink<String> retSink = new ReturnSink<>();

		try (Flow<String, String> flow = new Flow.Plumber<String, String>().from(SIMPLE_SOURCE).to(retSink).build())
		{
			flow.start();

			// Test that all data passed
			List<String> stuff = retSink.getStuff();
			Assert.assertTrue(stuff.size() == SIMPLE_DATA.size());

			// Test that the data is in order
			for (int i = 0; i < stuff.size(); ++i)
			{
				Assert.assertTrue(stuff.get(i).equals(SIMPLE_DATA.get(i)));
			}
		}
	}

	@Test(groups = { "plumber" })
	public void plumberBuild_sameOutputPipes()
	{
		ReturnSink<String> retSink = new ReturnSink<>();
		Flow.Plumber<String, String> plumber = new Flow.Plumber<>(SIMPLE_EMPTY_SOURCE, retSink);

		for (String str : SIMPLE_DATA)
		{
			plumber.pipe(new DecoratedFlowObject<String>()
			{
				@Override
				public void pump(String name, Collection<String> data)
				{
					data.add(str);
					this.nextFlow(name, data);
				}
			});
		}

		try (Flow<String, String> flow = plumber.build())
		{
			flow.start();
			// Test order of pipes
			Assert.assertTrue(this.stringListEquator(retSink.getStuff(), SIMPLE_DATA));
		}
	}

	@Test(groups = { "plumber" })
	public void plumberBuild_differentMatchingOutputPipes()
	{
		ReturnSink<String> retSink = new ReturnSink<>();
		Flow.Plumber<String, String> plumber = new Flow.Plumber<>(SIMPLE_SOURCE, retSink).pipe(new DecoratedFlowObject<String>()
		{
			@Override
			public void pump(String name, Collection<String> data)
			{
				this.nextFlow(name, data.stream().map(s -> (int) s.charAt(0)).collect(Collectors.toList()));
			}
		}).pipe(new DecoratedFlowObject<Integer>()
		{
			@Override
			public void pump(String name, Collection<Integer> data)
			{
				this.nextFlow(name, data.stream().map(integer -> "" + ((char) integer.intValue())).collect(Collectors.toList()));
			}
		});
		try (Flow<String, String> flow = plumber.build())
		{
			flow.start();
			Assert.assertTrue(this.stringListEquator(retSink.getStuff(), SIMPLE_DATA));
		}
	}

	@Test(groups = { "plumber" }, expectedExceptions = { ClassCastException.class })
	public void plumberBuild_differentNoMatchOutputPipes()
	{
		ReturnSink<String> retSink = new ReturnSink<>();
		Flow.Plumber<String, String> plumber = new Flow.Plumber<>(SIMPLE_SOURCE, retSink).pipe(new DecoratedFlowObject<String>()
		{
			@Override
			public void pump(String name, Collection<String> data)
			{
				this.nextFlow(name, data.stream().map(s -> (int) s.charAt(0)).collect(Collectors.toList()));
			}
		}).pipe(new DecoratedFlowObject<String>() // Note: Should be Integer
		{
			@Override
			public void pump(String name, Collection<String> data)
			{
				this.nextFlow(name, String.join("", data));
			}
		});
		try (Flow<String, String> flow = plumber.build())
		{
			flow.start(); // Will throw ClassCastException
		}
	}

	@Test(groups = { "plumber" })
	public void plumberBuild_multipleSinks()
	{
		ReturnSink<String> returnSink1 = new ReturnSink<>();
		ReturnSink<String> returnSink2 = new ReturnSink<>();

		try (Flow<String, String> flow = new Flow.Plumber<>(SIMPLE_SOURCE, Arrays.asList(returnSink1, returnSink2)).build())
		{
			flow.start();
			Assert.assertTrue(this.stringListEquator(returnSink1.getStuff(), returnSink2.getStuff()));
		}
	}

	@Test(groups = { "plumber" }, expectedExceptions = { IllegalStateException.class })
	public void plumberBuild_noSource()
	{
		/*
		 * 1. Note that the test needs to make sure that it is the absence of the source that causes
		 * 	the exception to be thrown
		 * 2. Note that the type of the Sink isn't relevant
		 */
		try (Flow<String, String> flow = new Flow.Plumber<String, String>().to(new Sink<String>()
		{
			@Override
			public void pump(String name, Collection<String> data)
			{
				// do nothing
			}
		}).build())
		{
			flow.start(); // is never called
		}
	}

	@Test(groups = { "plumber" }, expectedExceptions = { IllegalStateException.class })
	public void plumberBuild_noSink()
	{
		/*
		 * 1. Note that the test needs to make sure that it is the absence of the Sink that causes
		 * 	the exception to be thrown
		 * 2. Note that the type of the Source isn't relevant
		 */
		try (Flow<String, String> flow = new Flow.Plumber<String, String>().from(new Source<String>()
		{
			@Override
			public void pump(String name, Collection<String> data)
			{
				// do nothing
			}
		}).build())
		{
			flow.start(); // is never called
		}
	}

	private boolean stringListEquator(List<String> list1, List<String> list2)
	{
		String concatList1 = String.join("", list1);
		String concatList2 = String.join("", list2);
		return concatList1.equals(concatList2);
	}

	//	@Test
	//	public void transformTest()
	//	{
	//		String toTransform = "1989-06-02T12:35:22.555Z";
	//		Transformer<String, Long> transformer =
	//				(x) -> LocalDateTime.parse(x, DateTimeFormatter.ISO_DATE_TIME)
	//									.atZone(ZoneId.of("Asia/Jerusalem")).
	//											toInstant().toEpochMilli();
	//
	//		Long res = transformer.transform(toTransform);
	//		Assert.assertNotNull(res);
	//
	//		Date date = new Date(res);
	//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	//		String formattedString = format.format(date);
	//		Assert.assertTrue(formattedString.equals(toTransform));
	//	}
}