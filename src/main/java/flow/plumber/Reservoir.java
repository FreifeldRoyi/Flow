package flow.plumber;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public abstract class Reservoir extends DecoratedFlowObject
{
	public Reservoir()
	{
		super();
	}

	public Reservoir(String name, FlowObject next)
	{
		super(next);
	}
}
