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

	public Reservoir(FlowObject next)
	{
		super(next);
	}
}
