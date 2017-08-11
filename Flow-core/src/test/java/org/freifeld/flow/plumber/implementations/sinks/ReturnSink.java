package org.freifeld.flow.plumber.implementations.sinks;

import org.freifeld.flow.plumber.Sink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * I do not see a use to this Sink. It was created for testing purposes and is probably buggy (multithreading problems)
 *
 * @author Freifeld Royi
 * @since 25-Sep-15.
 */
public class ReturnSink<T> extends Sink<T>
{
	protected List<T> stuff;
	protected Comparator<T> comparator;
	protected boolean accumulating;
	private boolean async;

	public ReturnSink(Comparator<T> comparator, boolean accumulating, boolean async)
	{
		super();
		init(comparator, accumulating, async);
	}

	public ReturnSink(String name, Comparator<T> comparator, boolean accumulating, boolean async)
	{
		super(name);
		init(comparator, accumulating, async);
	}

	public ReturnSink(String name)
	{
		this(name, null, false, false);
	}

	public ReturnSink()
	{
		this("*");
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		if (this.accumulating)
		{
			this.stuff.addAll(data);
		}
		else
		{
			setStuff(data);
		}
	}

	public List<T> getStuff()
	{
		List<T> toReturn = new ArrayList<>(this.stuff);
		if (this.comparator != null)
		{
			toReturn.sort(this.comparator);
		}

		return toReturn;
	}

	private void setStuff(Collection<T> data)
	{
		if (this.async)
		{
			this.stuff = data != null ? new CopyOnWriteArrayList<>(data) : new CopyOnWriteArrayList<>();
		}
		else
		{
			this.stuff = data != null ? new ArrayList<>(data) : new ArrayList<>();
		}
	}

	private void init(Comparator<T> comparator, boolean accumulating, boolean async)
	{
		this.comparator = comparator;
		this.accumulating = accumulating;
		this.async = async;
		if (this.accumulating)
		{
			setStuff(null);
		}
	}
}
