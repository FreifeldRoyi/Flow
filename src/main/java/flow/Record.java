package flow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A processed data. Default data structure that is being extracted from input.
 * Most common use for this structure is the pre-process operation
 *
 * @author Freifeld Royi
 * @since 12-Sep-15.
 */
public class Record<T, K, V>
{
	private T id;
	private Map<K, V> mapper;

	public Record()
	{
		this(null);
	}

	public Record(T id)
	{
		this.init(id);
	}

	public void put(K key, V value)
	{
		this.mapper.put(key, value);
	}

	public V get(K key)
	{
		return this.mapper.get(key);
	}

	public T getId()
	{
		return this.id;
	}

	private void init(T id)
	{
		this.id = id;
		this.mapper = new ConcurrentHashMap<>();
	}
}
