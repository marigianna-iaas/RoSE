package de.unistuttgart.iaas.bpmn.compare.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import de.unistuttgart.iaas.bpmn.model.Checkpoint;
import de.unistuttgart.iaas.bpmn.util.CompareUtils;

/**
 * This method does the traversal necessary for the process models comparison
 * 
 * @author skourama
 *
 */

public class Comparison {
	private Set<SequenceFlow> visitedModelA = new LinkedHashSet<SequenceFlow>();
	private Set<SequenceFlow> visitedModelB = new LinkedHashSet<SequenceFlow>();

	/**
	 * Is used for the first creation of the fragment. I.e., it origins from the process model comparison
	 * it basically creates the mappings between two models. 
	 * @param sequenceFlowModelA
	 * @param sequenceFlowModelB
	 * @param matchedBranchesMap
	 * @param depth
	 * @param checkedCheckPointFlows
	 * @return
	 */
	public MatchedBranchesMap createMatchedBranchesMap(
			SequenceFlow sequenceFlowModelA, SequenceFlow sequenceFlowModelB, MatchedBranchesMap matchedBranchesMap, HashSet<FlowNode> checkedCheckPointFlows) {
				// if sources & targets of the corresponding sequence flows match
				if (CompareUtils.checkTypeMatch(sequenceFlowModelA.getSourceRef().eClass(), sequenceFlowModelB.getSourceRef().eClass())
					&& CompareUtils.checkTypeMatch(sequenceFlowModelA.getTargetRef().eClass(), sequenceFlowModelB.getTargetRef().eClass())) 
				{					
					//first take the correct outgoing
					List<SequenceFlow> outgoingEdgesFromSourceModelA = getSequenceFlows(sequenceFlowModelA, checkedCheckPointFlows);
					List<SequenceFlow> outgoingEdgesFromSourceModelB = getSequenceFlows(sequenceFlowModelB, checkedCheckPointFlows);

					//then mark for future calls
					checkedCheckPointFlows = markCheckedCheckPointFlows(sequenceFlowModelA, checkedCheckPointFlows);
					checkedCheckPointFlows = markCheckedCheckPointFlows(sequenceFlowModelB, checkedCheckPointFlows);
					int row = matchedBranchesMap.getBaseModelInstanceSequenceFlows().indexOf(sequenceFlowModelA);
					int column = matchedBranchesMap.getToCompareModelInstanceSequenceFlows().indexOf(sequenceFlowModelB);
					if(row == -1 || column == -1) return matchedBranchesMap; 
					matchedBranchesMap.setMappingMatchedBranches(row, column); 
					
					for (SequenceFlow outgoingA : outgoingEdgesFromSourceModelA) 
					{
						for (SequenceFlow outgoingB : outgoingEdgesFromSourceModelB) 
						{
							matchedBranchesMap = createMatchedBranchesMap(outgoingA, outgoingB,matchedBranchesMap, checkedCheckPointFlows);	
						}
					}		
				}
		return matchedBranchesMap;
	}
	

	
	private List<SequenceFlow> getSequenceFlows(SequenceFlow sequenceFlowCheckPoint,HashSet<FlowNode> checkedCheckPointFlows) {
		
		//if it does not contain the source, i.e. the source has not al  ready been checked
		if(checkedCheckPointFlows.contains(sequenceFlowCheckPoint.getSourceRef()))
		{
			return sequenceFlowCheckPoint.getTargetRef().getOutgoing();
		}
		else
		{
			return sequenceFlowCheckPoint.getSourceRef().getOutgoing();
		}
	}

	private HashSet<FlowNode> markCheckedCheckPointFlows(SequenceFlow sequenceFlowCheckPoint, HashSet<FlowNode> checkedCheckPointFlows)
	{
		if(!checkedCheckPointFlows.contains(sequenceFlowCheckPoint.getSourceRef()))
		{
			checkedCheckPointFlows.add(sequenceFlowCheckPoint.getSourceRef());
		}
		return checkedCheckPointFlows;
	}
	
	public Set<SequenceFlow> getVisitedModelA() {
		return visitedModelA;
	}

	public void setVisitedModelA(Set<SequenceFlow> visitedModelA) {
		this.visitedModelA = visitedModelA;
	}

	public Set<SequenceFlow> getVisitedModelB() {
		return visitedModelB;
	}

	public void setVisitedModelB(Set<SequenceFlow> visitedModelB) {
		this.visitedModelB = visitedModelB;
	}
	
	public MatchedBranchesMap initComparisonMatchedBranchesMap(List<SequenceFlow> baseModelInstanceSF, List<SequenceFlow> toCompareModelInstanceSF, SequenceFlow baseSF, SequenceFlow toCompareSF) {
		MatchedBranchesMap comparisonMatchedBranchesMap = new MatchedBranchesMap(baseModelInstanceSF.size(), toCompareModelInstanceSF.size());
		//when we are here the tmpFragment will already contain the matched fragments
		//at this point I should be getting a correct result
		comparisonMatchedBranchesMap.setBaseModelInstanceSequenceFlows((ArrayList<SequenceFlow>) baseModelInstanceSF);
		comparisonMatchedBranchesMap.setToCompareModelInstanceSequenceFlows((ArrayList<SequenceFlow>) toCompareModelInstanceSF);
		comparisonMatchedBranchesMap.fixSourcePointBaseModelInstance(baseSF);
		comparisonMatchedBranchesMap.fixSourcePointToCompareModelInstance(toCompareSF);		
		return comparisonMatchedBranchesMap;
	}

}
