package org.freifeld.flow.plumber.implementations.pipes;

import org.freifeld.flow.plumber.DecoratedFlowObject;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Freifeld Royi
 * @since 25-Sep-15.
 */
public abstract class AsyncPipe<T, R> extends DecoratedFlowObject<T>
{
	private ExecutorService executorService;

	public AsyncPipe()
	{
		super();
		this.executorService = Executors.newCachedThreadPool();
	}

	@Override
	public void pump(String name, Collection<T> data)
	{
		List<CompletableFuture<R>> collect = data.stream().map(t -> CompletableFuture.supplyAsync(() -> apply(t),this.executorService)).collect(Collectors.toList());
		CompletableFuture<Void> future = CompletableFuture.allOf(collect.toArray(new CompletableFuture[collect.size()]));
		CompletableFuture<List<R>> f = future.thenApply(v -> collect.stream().map(CompletableFuture::join).collect(Collectors.toList()));
		this.nextFlow(name, f.join());
	}

	protected abstract R apply(T data);
}
