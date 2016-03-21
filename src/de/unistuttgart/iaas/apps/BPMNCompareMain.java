package de.unistuttgart.iaas.apps;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import de.unistuttgart.iaas.bpmn.collection.BpmnCollectionSingleton;
import de.unistuttgart.iaas.bpmn.compare.algorithm.FragmentsFinder;
import de.unistuttgart.iaas.bpmn.util.LoggerConfig;

/**
 * @author skourama
 */
public class BPMNCompareMain {

	public static void main(String[] args) throws Throwable {

		//Initialization is needed before I do any other action on BPMN2 files
		LoggerConfig.init();

		Bpmn2Package bpmn2Package = Bpmn2Package.eINSTANCE;
		// Register the package
		EPackage.Registry.INSTANCE.put(Bpmn2Package.eNS_URI, bpmn2Package);

		// Register the BPMN2 resource factory
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("bpmn2", new Bpmn2ResourceFactoryImpl());

		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("bpmn", new Bpmn2ResourceFactoryImpl());

		// before load
		BpmnCollectionSingleton bpmnCollection =  BpmnCollectionSingleton.getInstance();
		bpmnCollection.loadCollectionFromFiles();	
		// Creates the BpmnCollection
		// Initialize the BPMN2 EMF package			
		// compares and returns the RPFs
		FragmentsFinder.execute(bpmnCollection.getAllModels());
		
	
	}		
}
