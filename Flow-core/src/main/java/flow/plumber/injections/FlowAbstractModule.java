package flow.plumber.injections;

import com.google.inject.AbstractModule;

import javax.inject.Singleton;

/**
 * @author royif
 * @since 10/06/16
 */
public class FlowAbstractModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		bind(ElementNamingFactory.class).in(Singleton.class);
	}
}
