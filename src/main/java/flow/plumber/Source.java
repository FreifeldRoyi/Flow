package flow.plumber;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public abstract class Source extends DecoratedFlowObject
{
	public Source()
	{
		super();
	}

	public Source(String name, FlowObject next)
	{
		super(next);
	}
}
