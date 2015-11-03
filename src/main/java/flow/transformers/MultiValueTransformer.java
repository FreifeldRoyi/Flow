package flow.transformers;

import java.util.Map;

/**
 * @author Freifeld Royi
 * @since 03-Nov-15.
 */
@FunctionalInterface
public interface MultiValueTransformer<T> extends Transformer<Map<String, String>, T>
{}
