package flow.plumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Freifeld Royi
 * @since 15-Sep-15.
 */
public final class FilteringReservoir<T> extends Reservoir
{
	private Predicate<T> accumulatedFilter;
	private List<Predicate<T>> filters;

	public FilteringReservoir(List<Predicate<T>> filters)
	{
		super();
		this.init(filters);
	}

	public FilteringReservoir()
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
	public void pump(Object... data)
	{
		List<T> list = Arrays.asList((T[]) data);
		List<T> collected =
				list.parallelStream().filter(this.accumulatedFilter).collect(Collectors.toList());
		Object[] params = new ArrayList<>(collected).toArray();
		this.next.pump(params);
	}

	private void init(List<Predicate<T>> filters)
	{
		this.accumulatedFilter = filters.stream().reduce(Predicate::and).orElse(x -> true);
		this.filters = filters;
	}
}
