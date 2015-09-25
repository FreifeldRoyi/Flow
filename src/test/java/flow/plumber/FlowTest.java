package flow.plumber;

import flow.plumber.implementations.sinks.ReturnSink;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author Freifeld Royi
 * @since 14-Sep-15.
 */
public class FlowTest
{
	private static final String[] SIMPLE_DATA =
			{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
					"r", "s", "t", "u", "v", "w", "x", "y", "z" };
	private static final Source SIMPLE_SOURCE = new Source()
	{
		@Override
		public void pump(Object... data)
		{
			this.next.pump(data);
		}

		@Override
		public void start()
		{
			this.pump(SIMPLE_DATA);
		}
	};

	private static final Source SIMPLE_EMPTY_SOURCE = new Source()
	{
		@Override
		public void pump(Object... data)
		{
			this.next.pump(data);
		}

		@Override
		public void start()
		{
			this.pump(new String[0]);
		}
	};

	@Test(groups = { "plumber" })
	public void plumber_buildTestNoPipes()
	{
		ReturnSink<String> retSink = new ReturnSink<>();

		Flow<String> flow = new Flow.Plumber<String>().from(SIMPLE_SOURCE).to(retSink).build();
		flow.start();

		// Test that all data passed
		String[] stuff = retSink.getStuff();
		Assert.assertTrue(stuff.length == SIMPLE_DATA.length);

		// Test that the data is in order
		for (int i = 0; i < stuff.length; ++i)
		{
			Assert.assertTrue(stuff[i].equals(SIMPLE_DATA[i]));
		}

		//		Assert.assertTrue(stuff.containsAll(Arrays.asList(SIMPLE_DATA)));
		//		Assert.assertTrue(Arrays.asList(SIMPLE_DATA).containsAll(stuff));
		flow.close();
	}

	@Test(groups = { "plumber" })
	public void plumber_buildTestWithPipes()
	{
		ReturnSink<String> retSink = new ReturnSink<>();
		Flow.Plumber<String> plumber = new Flow.Plumber<>(SIMPLE_EMPTY_SOURCE, retSink);

		for (String str : SIMPLE_DATA)
		{
			plumber.pipe(new DecoratedFlowObject()
			{
				@Override
				public void pump(Object... data)
				{
					String[] nextArray = Arrays.copyOf(((String[]) data), data.length + 1);
					nextArray[data.length] = str;
					this.next.pump(nextArray);
				}
			});
		}

		Flow<String> flow = plumber.build();
		flow.start();

		// Test order of pipes
		String concatString = String.join("", retSink.getStuff());
		String concatSimpleData = String.join("", SIMPLE_DATA);
		Assert.assertEquals(concatString, concatSimpleData);
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
		new Flow.Plumber<String>().to(new Sink()
		{
			@Override
			public void pump(Object... data)
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
		new Flow.Plumber<String>().from(new Source()
		{
			@Override
			public void pump(Object... data)
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