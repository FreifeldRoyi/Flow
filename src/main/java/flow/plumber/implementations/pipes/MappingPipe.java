package flow.plumber.implementations.pipes;

import com.google.gson.JsonElement;
import flow.transformers.TypeTransformer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * @author Freifeld Royi
 * @since 03-Nov-15.
 */
public class MappingPipe extends AsyncPipe<JsonElement, Map<String, Collection<String>>, CompletableFuture<Map<String, Collection<String>>>>
{
	public TypeTransformer typeConverter;

	public MappingPipe()
	{

	}

	@Override
	protected BiFunction<String, Collection<JsonElement>, List<CompletableFuture<Map<String, Collection<String>>>>> createTaskProducer()
	{
		return (s, jsonElements) -> {
			jsonElements.parallelStream().map(jsonElement ->)
		};
	}

}
