package flow.plumber;

import flow.plumber.injections.ElementNamingFactory;

import javax.inject.Inject;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public abstract class Sink<T> implements FlowObject<T>
{
	private final String name;

	@Inject
	public Sink(ElementNamingFactory factory)
	{
		super();
		this.name = factory.getName(getClass());
	}

	public Sink(String name)
	{
		super();
		this.name = name;
	}

	public Sink()
	{
		this("*");
	}

	public String getName()
	{
		return this.name;
	}
}
