package flow.plumber.config;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author royif
 * @since 06/05/16
 */
public class ConfigureTest
{
	private Configure configure;

	@BeforeClass
	public void setup()
	{
		this.configure = new Configure("configTest.properties", "configDefaultTest.properties");
	}

	@Test(groups = "docs",testName = "Null File Names", description = "Checks whether the constructor adheres to its doc of non-null params")
	public void nullFileNamesTest()
	{
		Assert.expectThrows(NullPointerException.class,() -> new Configure(null, "configDefaultTest.properties"));
		Assert.expectThrows(NullPointerException.class,() -> new Configure("configTest.properties", null));
		Assert.expectThrows(NullPointerException.class,() -> new Configure(null, null));
	}

	@Test
	public void onlyDefaultValueGetPropertyTest()
	{
		Assert.assertEquals(this.configure.getProperty("flow.test.property1"),"flow.test.defaults.value1");
	}

	@Test
	public void onlyProvidedValueGetPropertyTest()
	{
		Assert.assertEquals(this.configure.getProperty("flow.test.property2"),"flow.test.value2");
	}

	@Test
	public void providedAndDefaultValuesGetPropertyTest()
	{
		Assert.assertEquals(this.configure.getProperty("flow.test.property3"),"flow.test.value3");
		Assert.assertNotEquals(this.configure.getProperty("flow.test.property3"),"flow.test.defaults.value3");
	}
}