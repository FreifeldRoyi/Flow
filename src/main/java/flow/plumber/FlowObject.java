package flow.plumber;

/**
 * @author Freifeld Royi
 * @since 13-Sep-15.
 */
@FunctionalInterface
public interface FlowObject
{
	void pump(Object... data);
}
