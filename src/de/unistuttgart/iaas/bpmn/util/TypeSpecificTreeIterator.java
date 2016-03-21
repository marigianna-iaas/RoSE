package de.unistuttgart.iaas.bpmn.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.InternalEList;

public class TypeSpecificTreeIterator<E> extends AbstractTreeIterator<E> {

	private static final long serialVersionUID = 6877367927555171449L;

	private Collection<EClass> types = Arrays.asList(
			Bpmn2Package.Literals.GATEWAY, Bpmn2Package.Literals.EVENT,
			Bpmn2Package.Literals.SUB_PROCESS);

	protected boolean isResolveProxies;

	public TypeSpecificTreeIterator(E object, Collection<EClass> filterTypes,
			boolean isResolveProxies) {
		super(object, false);
		
		if (filterTypes != null && !filterTypes.isEmpty()) {
			this.types = filterTypes;
		}

		this.isResolveProxies = isResolveProxies;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<E> getChildren(Object object) {
		if (object instanceof EObject) {
			return (Iterator<E>) getTypedChildren((EObject) object);
		} else if (object == this.object) {
			return ((Collection<E>) object).iterator();
		} else {
			return getObjectChildren(object);
		}
	}

	private Iterator<? extends EObject> getTypedChildren(EObject eObject) {
		Iterator<? extends EObject> children = null;
		if (isResolveProxies()) {
			children = getInstancesOfSpecifiedTypes(
					eObject.eContents().iterator()).iterator();
		} else {
			children = getInstancesOfSpecifiedTypes(
					((InternalEList<EObject>) eObject.eContents())
							.basicIterator()).iterator();
		}

		return children;
	}

	protected Iterator<E> getObjectChildren(Object object) {
		return ECollections.<E> emptyEList().iterator();
	}

	protected boolean isResolveProxies() {
		return isResolveProxies;
	}

	private List<EObject> getInstancesOfSpecifiedTypes(
			Iterator<? extends EObject> children) {
		List<EObject> result = new ArrayList<EObject>();

		for (Iterator<? extends EObject> iter = children; iter.hasNext();) {
			EObject eObject = iter.next();

			if (isInstanceOfSpecifiedClass(eObject)) {
				result.add(eObject);
			}
		}

		return result;
	}

	private boolean isInstanceOfSpecifiedClass(EObject eObject) {
		for (EClass clazz : types) {
			if (clazz.isInstance(eObject)) {
				return true;
			}
		}

		return false;
	}
}
