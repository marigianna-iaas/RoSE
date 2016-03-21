package de.unistuttgart.iaas.bpmn.util;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.emf.ecore.EClass;

public final class Constants {

    public static final Collection<EClass> checkPointTypes = Arrays.asList(
			Bpmn2Package.Literals.PARALLEL_GATEWAY, Bpmn2Package.Literals.EVENT, Bpmn2Package.Literals.EXCLUSIVE_GATEWAY
			); //Bpmn2Package.Literals.EVENT, Bpmn2Package.Literals.SUB_PROCESS,
    public static final Collection<EClass>  sequenceFlow = Arrays.asList(Bpmn2Package.Literals.SEQUENCE_FLOW) ;
    public static final Collection<EClass>  flowNodesType = Arrays.asList(Bpmn2Package.Literals.FLOW_NODE) ;

    
    private Constants() {
        // restrict instantiation
}

}
