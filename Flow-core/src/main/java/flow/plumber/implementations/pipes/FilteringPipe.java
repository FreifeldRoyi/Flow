package flow.plumber.implementations.pipes;

import flow.plumber.DecoratedFlowObject;

import java.util.ArrayList;
import java.util.Collection;
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
	public void pump(String name, Collection<T> data)
	{
		List<T> collected = data.parallelStream().filter(this.accumulatedFilter).collect(Collectors.toList());
		this.nextFlow(name, collected);
	}

	private void init(List<Predicate<T>> filters)
	{
		this.accumulatedFilter = filters.stream().reduce((p1, p2) -> p1.and(p2)).orElse(x -> true);
		this.filters = filters;
	}
}
