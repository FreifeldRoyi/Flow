package flow.plumber;

import flow.plumber.implementations.sinks.ReturnSink;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Freifeld Royi
 * @since 14-Sep-15.
 */
public class FlowTest
{
	private static final List<String> SIMPLE_DATA =
			Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
						  "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
	private static final Source<String> SIMPLE_SOURCE = new Source<String>()
	{
		@Override
		public void pump(String name, List<String> data)
		{
			this.next.pump(name, data);
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
		public void pump(String name, List<String> data)
		{
			this.next.pump(name, data);
		}

		@Override
		public void start()
		{
			this.pump("*", new ArrayList<>());
		}
	};

	@Test(groups = { "plumber" })
	public void plumber_buildTestNoPipes()
	{
		ReturnSink<String> retSink = new ReturnSink<>();

		Flow<String, String> flow =
				new Flow.Plumber<String, String>().from(SIMPLE_SOURCE).to(retSink).build();
		flow.start();

		// Test that all data passed
		List<String> stuff = retSink.getStuff();
		Assert.assertTrue(stuff.size() == SIMPLE_DATA.size());

		// Test that the data is in order
		for (int i = 0; i < stuff.size(); ++i)
		{
			Assert.assertTrue(stuff.get(i).equals(SIMPLE_DATA.get(i)));
		}

		//		Assert.assertTrue(stuff.containsAll(Arrays.asList(SIMPLE_DATA)));
		//		Assert.assertTrue(Arrays.asList(SIMPLE_DATA).containsAll(stuff));
		flow.close();
	}

	@Test(groups = { "plumber" })
	public void plumber_buildTestWithPipes()
	{
		ReturnSink<String> retSink = new ReturnSink<>();
		Flow.Plumber<String, String> plumber = new Flow.Plumber<>(SIMPLE_EMPTY_SOURCE, retSink);

		for (String str : SIMPLE_DATA)
		{
			plumber.pipe(new DecoratedFlowObject<String>()
			{
				@Override
				public void pump(String name, List<String> data)
				{
					data.add(str);
					this.next.pump(name, data);
				}
			});
		}

		Flow<String, String> flow = plumber.build();
		flow.start();

		// Test order of pipes
		String concatString = String.join("", retSink.getStuff());
		String concatSimpleData = String.join("", SIMPLE_DATA);
		Assert.assertEquals(concatString, concatSimpleData);
		flow.close();
	}

	@Test(groups = { "plumber" })
	public void plumber_buildTestMultipleSinks()
	{
		ReturnSink<String> returnSink1 = new ReturnSink<>();
		ReturnSink<String> returnSink2 = new ReturnSink<>();

		Flow<String, String> flow =
				new Flow.Plumber<>(SIMPLE_SOURCE, Arrays.asList(returnSink1, returnSink2)).build();
		flow.start();

		List<String> returnSink1Stuff = returnSink1.getStuff();
		List<String> returnSink2Stuff = returnSink2.getStuff();

		String joinedSink1Stuff = String.join("", returnSink1Stuff);
		String joinedSink2Stuff = String.join("", returnSink2Stuff);
		Assert.assertEquals(joinedSink1Stuff, joinedSink2Stuff);
		flow.close();
	}

	@Test(groups = { "plumber" }, expectedExceptions = { IllegalStateException.class })
	public void plumber_noSource()
	{
		/*
		 * 1. Note that the test needs to make sure that it is the absence of the source that causes
		 * 	the exception to be thrown
		 * 2. Note that the type of the Sink isn't relevant
		 */
		new Flow.Plumber<String, String>().to(new Sink<String>()
		{
			@Override
			public void pump(String name, List<String> data)
			{
				// do nothing
			}
		}).build();
	}

	@Test(groups = { "plumber" }, expectedExceptions = { IllegalStateException.class })
	public void plumber_noSink()
	{
		/*
		 * 1. Note that the test needs to make sure that it is the absence of the Sink that causes
		 * 	the exception to be thrown
		 * 2. Note that the type of the Source isn't relevant
		 */
		new Flow.Plumber<String, String>().from(new Source<String>()
		{
			@Override
			public void pump(String name, List<String> data)
			{
				// do nothing
			}
		}).build();
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