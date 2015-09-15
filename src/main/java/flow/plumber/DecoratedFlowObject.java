package flow.plumber;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public abstract class DecoratedFlowObject implements FlowObject
{
	protected FlowObject next;

	public DecoratedFlowObject()
	{
		this(null);
	}

	public DecoratedFlowObject(FlowObject next)
	{
		this.init(next);
	}

	public void setNext(FlowObject next)
	{
		this.next = next;
	}

	private void init(FlowObject next)
	{
		this.next = next;
	}
}
