package flow.plumber.implementations.pipes;

import flow.plumber.DecoratedFlowObject;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Freifeld Royi
 * @since 25-Sep-15.
 */
public abstract class AsyncPipe<T, R> extends DecoratedFlowObject<T>
{
	public AsyncPipe()
	{
		super();
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		List<CompletableFuture<R>> collect = data.stream().map(t -> CompletableFuture.supplyAsync(() -> apply(t))).collect(Collectors.toList());

		// Thanks http://www.nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html?q=completablefuture
		CompletableFuture<Void> future = CompletableFuture.allOf(collect.toArray(new CompletableFuture[collect.size()]));
		future.thenAcceptAsync(v -> this.nextFlow(name, collect.stream().map(CompletableFuture::join).collect(Collectors.toList())));
	}

	protected abstract R apply(T data);
}
