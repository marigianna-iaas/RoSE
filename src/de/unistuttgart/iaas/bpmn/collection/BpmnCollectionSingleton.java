package de.unistuttgart.iaas.bpmn.collection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.unistuttgart.iaas.bpmn.compare.algorithm.FragmentsFinder;
import de.unistuttgart.iaas.bpmn.model.ModelInstance;
import de.unistuttgart.iaas.bpmn.util.FilesManagerSingleton;

/**
 * @author skourama
 * This class is a singleton class. This mean only one instance of this class is allowed per execution.
 * It is a Holder of all the BPMN models as java objects (i.e. ModelInstances)
 * 
 */
public final class BpmnCollectionSingleton {
	
	//this method logs the info..
	private final static Logger LOGGER = Logger.getLogger(FragmentsFinder.class.getName());
	static{
		LOGGER.setLevel(Level.ALL);
	}
	
	private static BpmnCollectionSingleton bpmnCollectionInstance = new BpmnCollectionSingleton();	//for initiating the singleton
	private List<ModelInstance> allModels = new ArrayList<ModelInstance>();	//the arraylist/holder of all the ModelInstances
	

    	
	protected BpmnCollectionSingleton() {
		// Exists only to defeat instantiation.
	}

	//we call this method to get the singleton class instance
	public static BpmnCollectionSingleton getInstance() {
		
		return bpmnCollectionInstance;
	}

	/**
	 * This class is responsible for loading all models from the "file paths" as read from @see {@link de.unistuttgart.iaas.bpmn.util.FilesManagerSingleton}
	 * For all the already loaded filename paths of BPMN 2.0 models it creates the collection of BPMN 2.0 Java instances
	 */
	public void loadCollectionFromFiles() {
		
		//file manager instance for retrieving information regarding the participating files (i.e. configuration file, bpmn model file paths)
		FilesManagerSingleton filesManager;	
		LOGGER.info("Load new collection");
			filesManager = FilesManagerSingleton.getInstance();
			
			List<File> collectionFileNames = filesManager.getBpmnFilePaths();

			for (File modelFilename : collectionFileNames) {
				try{
					ModelInstance model = new ModelInstance(modelFilename.getAbsolutePath());
					this.allModels.add(model);
				}catch(Exception e)
				{
					LOGGER.log(Level.SEVERE, "There was a problem with file: "+ modelFilename + e.getMessage(), e);
					//System.out.println("There was a problem with file: "+ e.getMessage());
				}
			}

	
	}

	/**
	 * It returns a List of all the participating BPMN 2.0 files as Java Objects 
	 * @return List<ModelInstance> allModels
	 */
	public List<ModelInstance> getAllModels() {
		return allModels;
	}

	
}
