package flow.plumber.implementations;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Freifeld Royi
 * @since 15-Sep-15.
 */
public class PrintOutSinkTest
{
	private PrintOutSink printOutSink;

	@BeforeMethod
	public void setUp() throws Exception
	{
		this.printOutSink = new PrintOutSink("testing");
	}

	@Test
	public void testPump() throws Exception
	{
		String[] words = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		this.printOutSink.pump(words);
	}
}