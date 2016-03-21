package de.unistuttgart.iaas.bpmn.cycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import de.unistuttgart.iaas.bpmn.model.*;
import de.unistuttgart.iaas.bpmn.util.CompareUtils;
import de.unistuttgart.iaas.bpmn.collection.*;
import de.unistuttgart.iaas.bpmn.compare.algorithm.FragmentsFinder;

import org.eclipse.bpmn2.Process;
public class Cycle
{
	private final static Logger LOGGER = Logger.getLogger(FragmentsFinder.class.getName());
	static{
		LOGGER.setLevel(Level.ALL);
	}
	Set<String> cyclicModels = new HashSet<String>();
	List<String> cyclicModels2 = new ArrayList<String>();

	private Collection<EClass> types = Arrays.asList(
			Bpmn2Package.Literals.FLOW_NODE) ;
	
	int index ;
//	Stack<FlowNode> stack = new Stack<FlowNode>();
//	List<FlowNode> scc = new ArrayList<FlowNode>();
	Stack<FlowNode> stack;
	List<FlowNode> scc;

	List<ModelInstance> models = BpmnCollectionSingleton.getInstance().getAllModels();

	
	public void findCycles()
	{
		for(ModelInstance model : models)
		{
			Process process = model.getProcess();
			 
			//for all Flowodes
			Map<FlowNode, FlowNodeCycleMetadata> allFlowNodeMetadataMap = getModelsCycleFlowNodes(process);
			Iterator it = allFlowNodeMetadataMap.entrySet().iterator();
			stack =new Stack<FlowNode>();
			scc = new ArrayList<FlowNode>(); 
			index = 1;

	    	while (it.hasNext()) {
			        Map.Entry pair = (Map.Entry)it.next();
			        FlowNode node = (FlowNode) pair.getKey();
			        FlowNodeCycleMetadata metadata = (FlowNodeCycleMetadata) pair.getValue();
					
				    if (metadata.getIndex() == -1)
				    {
				    		strongconnect(node, metadata, allFlowNodeMetadataMap,model);
				    }

			}
		}
	}
	
	public void strongconnect(FlowNode node, FlowNodeCycleMetadata metadata, Map<FlowNode, FlowNodeCycleMetadata> modelsFlowNodes, ModelInstance model) {
		// Set the depth index for v to the smallest unused index
	    metadata.setIndex(index);
	    metadata.setLowlink(index);
	    index++;
	    stack.push(node);
	    metadata.setOnStack(true);

	    // Consider successors of v
	    List<SequenceFlow> outgoings = node.getOutgoing();
	    FlowNode successor;
		for(SequenceFlow outgoing : outgoings)
		{
			successor = outgoing.getTargetRef();
			FlowNodeCycleMetadata succMetadata = modelsFlowNodes.get(successor);
			if(succMetadata.getIndex() == -1)
			{
				// Successor w has not yet been visited; recurse on it
				strongconnect(successor,succMetadata, modelsFlowNodes, model);
				metadata.setLowlink(Math.min(metadata.getLowlink(), succMetadata.getLowlink()));
				
			}
			else if (succMetadata.isOnStack())
			{
				// Successor w is in stack S and hence in the current SCC
				metadata.setLowlink(Math.min(metadata.getLowlink(), succMetadata.getIndex()));
			}
				
		}
		// If v is a root node, pop the stack and generate an SCC
		if(metadata.getLowlink() == metadata.getIndex())
		{
			//start a new strongly connected component
			do
			{
				successor = null;
				if(!stack.isEmpty())
				{
					successor = stack.pop();
					FlowNodeCycleMetadata succMetadata = modelsFlowNodes.get(successor);
					succMetadata.onStack = false;
					//add w to current strongly connected component
					scc.add(successor);
				}
				if(successor != null || !stack.isEmpty())
					break;
			}while(node.equals(successor));
		}
		if(metadata.getLowlink() < metadata.getIndex())
		{

			cyclicModels.add(model.getFilePath());				    	
			//model.setHasCycle(true);
		} 
	}

	/**
	 * It will return a list of all FlowNodes of a model that are extended to type CycleFlowNode
	 * In this way we can access the index, and lowlink attributes
	 * @param process
	 * @return
	 */
	public Map<FlowNode,FlowNodeCycleMetadata> getModelsCycleFlowNodes(Process process)
	{
		Map<FlowNode, FlowNodeCycleMetadata>  modelsFlowNodes  = new HashMap<FlowNode, FlowNodeCycleMetadata>();
		for (TreeIterator<EObject> iterator = CompareUtils
				.getAllContentsWithSpecificTypes(process, types, true); 
				iterator.hasNext();) {
	
			EObject node = iterator.next();
			if (node instanceof FlowNode) {
				modelsFlowNodes.put((FlowNode)node, new FlowNodeCycleMetadata( -1 , -1));
				
			}
		}
		return modelsFlowNodes;
	}


	
	public void printCyclicModels()
	{
		LOGGER.info("--------------------------------Models With Cycles--------------------------------");
		
		for(String modelName : cyclicModels)	
			LOGGER.info(modelName);
        LOGGER.info("----------------------------------------------------------------------------------");
	}
	

	
}

