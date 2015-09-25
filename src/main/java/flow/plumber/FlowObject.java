package flow.plumber;

import java.util.List;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
@FunctionalInterface
public interface FlowObject<T> extends AutoCloseable
{
	void pump(String name, List<T> data);

	default void start()
	{
		// do nothing
	}

	@Override
	default void close()
	{
		// do nothing
	}
}
