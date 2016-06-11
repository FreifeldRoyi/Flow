package flow.plumber.injections;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * @author royif
 * @since 07/05/16
 */
public class NamedFlowElementImpl implements NamedFlowElement, Serializable
{
	private static final long serialVersionUID = 57516901687402127L;

	private final String elementName;
	private final boolean indexed;

	public NamedFlowElementImpl(String elementName, boolean indexed)
	{
		this.elementName = elementName;
		this.indexed = indexed;
	}

	@Override
	public String elementName()
	{
		return this.elementName;
	}

	@Override
	public boolean indexed()
	{
		return this.indexed;
	}

	@Override
	public Class<? extends Annotation> annotationType()
	{
		return NamedFlowElement.class;
	}

	@Override
	public int hashCode()
	{
		return 	(127 * "elementName".hashCode()) ^ this.elementName.hashCode() +
				(127 * "indexed".hashCode()) ^ Boolean.hashCode(this.indexed);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof NamedFlowElement))
		{
			return false;
		}

		NamedFlowElement other = (NamedFlowElement) obj;
		return this.indexed == other.indexed() && this.elementName.equals(other.elementName());
	}

	@Override
	public String toString()
	{
		return "@" + NamedFlowElement.class.getName() + "(elementName=" + elementName + ", indexed=" + indexed + ")";
	}
}
