package flow.plumber;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
@FunctionalInterface
public interface FlowObject extends AutoCloseable
{
	void pump(Object... data);

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
