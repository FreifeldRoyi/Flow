package framework;

/**
 * Interface defining the <code>transform</code> operation.
 * It's purpose is to transform from an input of some type <code>T</code> to an output of type
 * <code>R</code>
 *
 * @param <T> input type parameter
 * @param <R> output type parameter
 * @author Freifeld Royi
 * @since 12-Sep-15.
 */
@FunctionalInterface
public interface Transformer<T,R>
{
	R transform(T input);
}
