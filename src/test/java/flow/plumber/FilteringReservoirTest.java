package flow.plumber;

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
public class FilteringReservoirTest
{
	private static final int MAX_NUMBER_TO_GENERATE = 10000;

	private FilteringReservoir<String> filterStringReservoir;
	private List<String> stringData;

	private String[] testedStringData;

	private Predicate<String> dividedByTwo = s -> Integer.parseInt(s) % 2 == 0;
	private Predicate<String> dividedByThree = s -> Integer.parseInt(s) % 3 == 0;

	@Test
	public void testPumpOneFilter() throws Exception
	{
		this.filterStringReservoir.addFilter(this.dividedByTwo);
		this.filterStringReservoir.pump(testedStringData);

		Assert.assertTrue(this.stringData.size() < MAX_NUMBER_TO_GENERATE);
	}

	@Test
	public void testPumpTwoFilters() throws Exception
	{
		this.filterStringReservoir.addFilter(this.dividedByTwo);
		this.filterStringReservoir.addFilter(this.dividedByThree);
		this.filterStringReservoir.pump(testedStringData);

		System.out.println(this.stringData.size());

		Assert.assertTrue(this.stringData.size() < MAX_NUMBER_TO_GENERATE / 2);
	}

	@BeforeClass
	private void setup()
	{
		this.testedStringData = new String[MAX_NUMBER_TO_GENERATE];
		List<Integer> intList =
				IntStream.iterate(0, x -> x + 1).limit(MAX_NUMBER_TO_GENERATE).boxed()
						 .collect(Collectors.toList());
		intList.stream().forEach(n -> testedStringData[n] = n + "");
	}

	@BeforeMethod
	private void beforeMethod()
	{
		this.stringData = new ArrayList<>();
		this.filterStringReservoir = new FilteringReservoir<>();
		this.filterStringReservoir.setNext(data -> {
			for (Object o : data)
			{
				this.stringData.add((String) o);
			}
		});
	}
}