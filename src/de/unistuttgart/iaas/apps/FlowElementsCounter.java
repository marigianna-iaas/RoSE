package de.unistuttgart.iaas.apps;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import de.unistuttgart.iaas.bpmn.collection.BpmnCollectionSingleton;
import de.unistuttgart.iaas.bpmn.model.ModelInstance;
import de.unistuttgart.iaas.bpmn.util.AnalyticsUtils;
import de.unistuttgart.iaas.bpmn.util.FilesManagerSingleton;

public class FlowElementsCounter {

	public static void main(String[] args) throws IOException {
		
		//has entries of type <NodesCnt, Frequency>
		Map<Integer, Integer> frequenciesFNCnt = new HashMap<Integer,Integer>();
		Map<Integer, Integer> frequenciesSFCnt = new HashMap<Integer,Integer>();

		Map<Integer, Integer> frequenciesFNCntSorted;
		Map<Integer, Integer> frequenciesSFCntSorted;

		Integer freq =0;
		Integer key;
		
		Integer freq2 =0;
		Integer key2;
		FilesManagerSingleton filesManager = FilesManagerSingleton.getInstance();
	
		long ID = System.currentTimeMillis();
		//TODO: change name of this and create new path
		String rpfAnalysisStatisticsFileName = filesManager.getRpfAnalysisStatisticsPath()+ "_FlowNodesCnt_" + ID +".csv" ;
		String rpfAnalysisStatisticsFileName2 = filesManager.getRpfAnalysisStatisticsPath()+ "_SequenceFlowCnt_" +ID+ ".csv" ;

		
		//TODO: Move the following an initializer untility method?
		Bpmn2Package bpmn2Package = Bpmn2Package.eINSTANCE;
		// Register the package
		EPackage.Registry.INSTANCE.put(Bpmn2Package.eNS_URI, bpmn2Package);
	
		// Register the BPMN2 resource factory
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("bpmn2", new Bpmn2ResourceFactoryImpl());
	
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("bpmn", new Bpmn2ResourceFactoryImpl());
	
		// before load
		BpmnCollectionSingleton bpmnCollection =  BpmnCollectionSingleton.getInstance();
		bpmnCollection.loadCollectionFromFiles();	
		
		
		for(ModelInstance modelA : bpmnCollection.getAllModels())
		{	
			
			AnalyticsUtils.writeModelSize(filesManager.getRpfAnalysisStatisticsPath()+ "modelsFlowNodesSize.csv", modelA.getFilePath(), modelA.getFlowNodesCnt());
			AnalyticsUtils.writeModelSize(filesManager.getRpfAnalysisStatisticsPath()+ "modelsSequenceFlowsSize.csv",modelA.getFilePath(), modelA.getSequenceFlows().size());

			key = Integer.valueOf(modelA.getFlowNodesCnt());	
			
			if(frequenciesFNCnt.containsKey(key))
			{
				freq = frequenciesFNCnt.get(key);
				frequenciesFNCnt.put(key, Integer.valueOf(freq + 1));
			}
			else
			{
				frequenciesFNCnt.put(key, Integer.valueOf(1));
			}
			
			key2 = Integer.valueOf(modelA.getSequenceFlows().size());
			if(frequenciesSFCnt.containsKey(key2))
			{
				freq2 = frequenciesSFCnt.get(key2);
				frequenciesSFCnt.put(key2, Integer.valueOf(freq2 + 1));
			}
			else
			{
				frequenciesSFCnt.put(key2, Integer.valueOf(1));
			}
		}
		frequenciesFNCntSorted = new TreeMap<Integer, Integer>(frequenciesFNCnt);
		AnalyticsUtils.writeFlowNodesCnt(rpfAnalysisStatisticsFileName, frequenciesFNCntSorted);
		
		
		frequenciesSFCntSorted = new TreeMap<Integer, Integer>(frequenciesSFCnt);
		AnalyticsUtils.writeFlowNodesCnt(rpfAnalysisStatisticsFileName2, frequenciesSFCntSorted);
		
		System.out.println("Completed");
	}

}
