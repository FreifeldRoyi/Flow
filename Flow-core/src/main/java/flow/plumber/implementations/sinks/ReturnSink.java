package flow.plumber.implementations.sinks;

import flow.plumber.Sink;

import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Freifeld Royi
 * @since 25-Sep-15.
 */
public class ReturnSink<T> extends Sink<T>
{
	protected List<T> stuff;
	protected Comparator<T> comparator;
	protected boolean accumulating;
	private boolean async;

	public ReturnSink()
	{
		this("*");
	}

	public ReturnSink(String name)
	{
		super(name);
		this.stuff = null;
		this.comparator = null;
		this.accumulating = false;
		this.async = false;
	}

	public ReturnSink(String name, Comparator<T> comparator, boolean accumulating, boolean async)
	{
		super(name);
		this.comparator = comparator;
		this.accumulating = accumulating;
		this.async = async;
		if (this.accumulating)
		{
			setStuff(null);
		}
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
			System.out.println(toReturn);
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
}
