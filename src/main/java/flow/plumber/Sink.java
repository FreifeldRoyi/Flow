package flow.plumber;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public abstract class Sink implements FlowObject
{
	private final String name;

	protected Sink(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
