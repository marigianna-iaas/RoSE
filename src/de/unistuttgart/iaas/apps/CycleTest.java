package de.unistuttgart.iaas.apps;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.common.util.Logger;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import de.unistuttgart.iaas.bpmn.collection.BpmnCollectionSingleton;
import de.unistuttgart.iaas.bpmn.cycle.Cycle;
import de.unistuttgart.iaas.bpmn.util.LoggerConfig;

public class CycleTest {

	public static void main(String[] args) {

		try {
			LoggerConfig.initCycleLogger();

			Bpmn2Package bpmn2Package = Bpmn2Package.eINSTANCE;
			// Register the package
			EPackage.Registry.INSTANCE.put(Bpmn2Package.eNS_URI, bpmn2Package);

			// Register the BPMN2 resource factory
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("bpmn2", new Bpmn2ResourceFactoryImpl());

			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("bpmn", new Bpmn2ResourceFactoryImpl());

			// TODO: change to Singleton? - the files will be transformed
			// before load
			BpmnCollectionSingleton bpmnCollection =  BpmnCollectionSingleton.getInstance();
			bpmnCollection.loadCollectionFromFiles();
			Cycle cycle = new Cycle();
			cycle.findCycles();
			cycle.printCyclicModels();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
