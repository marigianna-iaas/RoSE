package de.unistuttgart.iaas.bpmn.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.unistuttgart.iaas.bpmn.compare.algorithm.MatchedBranchesMap;
import de.unistuttgart.iaas.bpmn.model.Checkpoint;

/**
 * This class has methods necessary for comparison
 * @author skourama
 *
 */
public class CompareUtils {
	public static TreeIterator<EObject> getAllContentsWithSpecificTypes(
			EObject root, Collection<EClass> filterTypes, boolean resolve) {
		return new TypeSpecificTreeIterator<EObject>(root, filterTypes, resolve);
	}

	/***
	 * Checks if the sources of two sequence flows are of the same type
	 * 
	 * @param sequenceFlow1
	 * @param sequenceFlow2
	 * @return
	 */
	public static boolean checkTypeMatch(EClass type1, EClass type2) {
		
		return 	type1.equals(Bpmn2Package.Literals.CALL_ACTIVITY) && type2.equals(Bpmn2Package.Literals.TASK) ||
				type2.equals(Bpmn2Package.Literals.CALL_ACTIVITY) && type1.equals(Bpmn2Package.Literals.TASK) ||
				type1.equals(type2);
		
		
	}
	
	


}
