package flow.plumber.injections;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import javax.inject.Inject;

/**
 * @author royif
 * @since 29/05/16
 */
public class ElementNamingFactory
{
	public static final ElementNameProvider DEFAULT_PROVIDER = (cls, prefix, suffix) -> (prefix != null && !prefix.isEmpty() ? prefix + "_" : "") + cls.getSimpleName() + (
			suffix != null && !suffix.isEmpty() ? "_" + suffix : "");

	private Table<Class<?>, ElementNameProvider, Integer> clsAndProviderToIndex;

	@Inject
	public ElementNamingFactory()
	{
		this.clsAndProviderToIndex = HashBasedTable.create();
	}

	public void addProvider(Class<?> cls, ElementNameProvider provider)
	{
		addProvider(cls, provider, 0);
	}

	public void addProvider(Class<?> cls, ElementNameProvider provider, int initialValue)
	{
		this.addProviderIfNotExists(cls, provider, initialValue);
	}

	public String getName(Class<?> cls)
	{
		return getName(cls, DEFAULT_PROVIDER);
	}

	public String getName(Class<?> cls, ElementNameProvider provider)
	{
		this.addProviderIfNotExists(cls, provider, 0);
		int count = this.clsAndProviderToIndex.get(cls, provider) + 1;
		String toReturn = provider.generate(cls, "", "" + count);
		incrementUsage(cls, provider);
		return toReturn;
	}

	private void addProviderIfNotExists(Class<?> cls, ElementNameProvider provider, int initialValue)
	{
		if (!this.clsAndProviderToIndex.contains(cls, provider))
		{
			this.clsAndProviderToIndex.put(cls, provider, initialValue);
		}
	}

	private void incrementUsage(Class<?> cls, ElementNameProvider provider)
	{
		int count = this.clsAndProviderToIndex.get(cls, provider) + 1;
		this.clsAndProviderToIndex.put(cls, provider, count);
	}
}
