package flow.plumber.injections;

/**
 * This interface is used to describe the functionality to provide a name for an element
 *
 * @author royif
 * @since 10/06/16
 */
@FunctionalInterface
public interface ElementNameProvider
{
	/**
	 * Generate a name for an object according to its class, and manually provided prefix and suffix
	 * Note that the provided prefix and suffix are not mandatory.
	 *
	 * @param cls - the class of the object
	 * @param prefix - the provided prefix
	 * @param suffix - the provided suffix
	 * @return a non null name
	 */
	String generate(Class<?> cls, String prefix, String suffix);
}
