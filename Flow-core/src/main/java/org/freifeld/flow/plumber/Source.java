package org.freifeld.flow.plumber;

import java.util.Collection;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
public class Source<T> extends DecoratedFlowObject<T>
{
	public Source()
	{
		super();
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		this.nextFlow(name, data);
	}
}
