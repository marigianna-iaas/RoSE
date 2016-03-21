package de.unistuttgart.iaas.bpmn.compare.algorithm;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.unistuttgart.iaas.bpmn.collection.RPFCollectionSingleton;
import de.unistuttgart.iaas.bpmn.model.Fragment;
import de.unistuttgart.iaas.bpmn.model.ModelInstance;
import de.unistuttgart.iaas.bpmn.util.AnalyticsUtils;
import de.unistuttgart.iaas.bpmn.util.FilesManagerSingleton;

public final class FragmentsFinder {
	//this method logs the info..
	private final static Logger LOGGER = Logger.getLogger(FragmentsFinder.class.getName());
	static{
		LOGGER.setLevel(Level.ALL);
	}
	
	
	/**
	 * Executes comparisons and returns statistics
	 * @return RPF collection of the calculated RPFs
	 * @throws IOException
	 */
	public static RPFCollectionSingleton execute(final List<ModelInstance> bpmnCollection) throws IOException
	{
	
		//the types that will be counted as checkpoints
		FilesManagerSingleton filesManager =  FilesManagerSingleton.getInstance();
		String rpfAnalysisStatisticsFileName = filesManager.getRpfAnalysisStatisticsPath()+ System.currentTimeMillis() ;

		LOGGER.info("I will compare: " +  filesManager.getBpmnFilePaths().size()+ " models");
		RPFCollectionSingleton rpfCollection = RPFCollectionSingleton.getInstance();

		Set<String> visitedFiles = new LinkedHashSet<String>();
		//each model with each model
		String modelAFileName;
		String modelBFileName;
		int comparisonsCnt = 0 ;
		Long start = System.currentTimeMillis();	

		for(ModelInstance modelA : bpmnCollection)
		{
			modelAFileName = modelA.getFilePath();
			visitedFiles.add(modelAFileName);
			for(ModelInstance modelB : bpmnCollection)
			{
				modelBFileName = modelB.getFilePath();
				try{
					if( (!modelAFileName.equals(modelBFileName)) && (!visitedFiles.contains(modelBFileName)) &&  modelAFileName.contains(".bpmn") && modelBFileName.contains(".bpmn") )
					{	
						LOGGER.info("Model A: "+ modelAFileName);
						LOGGER.info("Model B: "+ modelBFileName);
						
						TwoModelComparator compare = new TwoModelComparator(modelA, modelB);
						List<Fragment> discoveredFragments = compare.discoverFragments();
						
						int oldCntRPF = rpfCollection.getAllFragments().size();			
						rpfCollection.addMatchesToCollection(discoveredFragments, filesManager.getMinEdgesSize(),filesManager.getRpfPath());
						
						int currCntRPF = rpfCollection.getAllFragments().size() - oldCntRPF;
						LOGGER.info("#RPF found: " + currCntRPF);
						
						comparisonsCnt ++;
					}
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					LOGGER.severe("Exception thrown");
					LOGGER.severe("Model A: "+ modelAFileName + "- Model B:"+ modelBFileName);
				}
			}
		}
		
		Long end = System.currentTimeMillis();	

		AnalyticsUtils.writeFragmentDiscoveryAnalysisCsv(rpfAnalysisStatisticsFileName, rpfCollection.getAllFragments());
		LOGGER.info(comparisonsCnt + "model comparisons");

		LOGGER.info("Comparison ended successfully");
		LOGGER.info("Total Time in ms: " + (end - start)   + "ms");

		LOGGER.info("Total Time in min: " + ((end - start) / 60000)  + "min");

		LOGGER.info("Total Time in hours: " + ((end - start) / 60000) / 60 + "hours");
		return rpfCollection;
	}
}
