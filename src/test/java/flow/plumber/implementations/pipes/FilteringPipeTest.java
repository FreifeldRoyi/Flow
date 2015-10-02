package flow.plumber.implementations.pipes;

import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Freifeld Royi
 * @since 15-Sep-15.
 */
public class FilteringPipeTest
{
	private static final int MAX_NUMBER_TO_GENERATE = 10000;

	private FilteringPipe<String> filterStringReservoir;
	private List<String> stringData;

	private List<String> testedStringData;

	private Predicate<String> dividedByTwo = s -> Integer.parseInt(s) % 2 == 0;
	private Predicate<String> dividedByThree = s -> Integer.parseInt(s) % 3 == 0;

	@Test
	public void testPumpOneFilter() throws Exception
	{
		this.filterStringReservoir.addFilter(this.dividedByTwo);
		this.filterStringReservoir.pump("*", testedStringData);

		Assert.assertTrue(this.stringData.size() < MAX_NUMBER_TO_GENERATE);
	}

	@Test
	public void testPumpTwoFilters() throws Exception
	{
		this.filterStringReservoir.addFilter(this.dividedByTwo);
		this.filterStringReservoir.addFilter(this.dividedByThree);
		this.filterStringReservoir.pump("*", testedStringData);

		Assert.assertTrue(this.stringData.size() < MAX_NUMBER_TO_GENERATE / 2);
	}

	@BeforeClass
	private void setup()
	{
		this.testedStringData =
				IntStream.iterate(0, x -> x + 1).limit(MAX_NUMBER_TO_GENERATE).boxed()
						 .map(n -> n + "").collect(Collectors.toList());
	}

	@BeforeMethod
	private void beforeMethod()
	{
		this.stringData = new ArrayList<>();
		this.filterStringReservoir = new FilteringPipe<>();
		this.filterStringReservoir.setNext((name, data) -> {
			for (Object o : data)
			{
				this.stringData.add((String) o);
			}
		});
	}
}