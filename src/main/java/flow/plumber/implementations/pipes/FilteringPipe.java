package flow.plumber.implementations.pipes;

import flow.plumber.DecoratedFlowObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Freifeld Royi
 * @since 15-Sep-15.
 */
public final class FilteringPipe<T> extends DecoratedFlowObject<T>
{
	private Predicate<T> accumulatedFilter;
	private List<Predicate<T>> filters;

	public FilteringPipe(List<Predicate<T>> filters)
	{
		super();
		this.init(filters);
	}

	public FilteringPipe()
	{
		super();
		this.init(new ArrayList<>());
	}

	public void addFilter(Predicate<T> filter)
	{
		this.filters.add(filter);
		this.init(this.filters);
	}

	@Override
	public void pump(String name, List<T> data)
	{
		List<T> collected =
				data.parallelStream().filter(this.accumulatedFilter).collect(Collectors.toList());
		this.next.pump(name, collected);
	}

	private void init(List<Predicate<T>> filters)
	{
		this.accumulatedFilter = filters.stream().reduce(Predicate::and).orElse(x -> true);
		this.filters = filters;
	}
}
