package org.freifeld.flow.plumber.implementations.pipes;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;

/**
 * @author Freifeld Royi
 * @since 15-Sep-15.
 */
public class FilteringPipeTest
{
	private static final int MAX_NUM = 1000000;

	private int dataCount;

	private FilteringPipe<Integer> pipeUnderTest;
	private List<Integer> data;

	private List<Integer> testedData;

	private Predicate<Integer> dividedByTwo = i -> i % 2 == 0;
	private Predicate<Integer> dividedByThree = i -> i % 3 == 0;

	@BeforeMethod
	public void beforeMethod()
	{
		this.dataCount = new Random().nextInt(MAX_NUM);
		this.testedData = IntStream.range(0, dataCount).boxed().collect(Collectors.toList());
		this.data = new ArrayList<>();
		this.pipeUnderTest = new FilteringPipe<>();
		this.pipeUnderTest.setNext((name, data) -> data.forEach(o -> this.data.add((Integer) o)));
	}

	@Test(invocationCount = 10)
	public void testPumpOneFilter() throws Exception
	{
		this.pipeUnderTest.addFilter(this.dividedByTwo);
		this.pipeUnderTest.pump("*", this.testedData);

		assertEquals(this.data.size(), (this.testedData.size() / 2) + (this.testedData.size() % 2));
	}

	@Test(invocationCount = 10)
	public void testPumpTwoFilters() throws Exception
	{
		this.pipeUnderTest.addFilter(this.dividedByTwo);
		this.pipeUnderTest.addFilter(this.dividedByThree);
		this.pipeUnderTest.pump("*", this.testedData);

		assertEquals(this.data.size(), (this.testedData.size() / 6) + (this.testedData.size() % 6 == 0 ? 0 : 1));
	}
}