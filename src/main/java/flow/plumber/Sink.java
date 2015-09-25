package flow.plumber;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public abstract class Sink<T> implements FlowObject<T>
{
	private final String name;

	public Sink()
	{
		this("*");
	}

	public Sink(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
