package org.freifeld.flow.plumber;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public abstract class DecoratedFlowObject<T> implements FlowObject<T>
{
	@SuppressWarnings("unchecked")
	private FlowObject next;

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

	/**
	 * This method should be called whenever the data manipulation is done and should be passed
	 * on to the <code>next</code> {@link FlowObject}.
	 * Note that this method has a {@link SuppressWarnings}:
	 * The idea behind it is that the next form of data in the pipe chain is unknown. If one
	 * would try to use generics to somehow "know" the types of the entire flow, one would have
	 * to use as much generics as the length of the flow. And it is not possible in Java
	 *
	 * @param name - the name of the output sink
	 * @param data - the data to pass to the next Flow
	 */
	@SuppressWarnings("unchecked")
	protected void nextFlow(String name, Collection<?> data)
	{
		this.next.pump(name, data);
	}

	protected void nextFlow(String name, Object... data)
	{
		this.nextFlow(name, Arrays.asList(data));
	}

	private void init(FlowObject next)
	{
		setNext(next);
	}
}
