package de.unistuttgart.iaas.bpmn.compare.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

import de.unistuttgart.iaas.bpmn.model.Checkpoint;
import de.unistuttgart.iaas.bpmn.model.Fragment;
import de.unistuttgart.iaas.bpmn.model.ModelInstance;

/**
 * @author skourama
 * @see baseModelInstance: This model is used as a basis for the comparison, so
 *      it restricts the results based on its contents! For example, if this
 *      model contains gateways with only two outgoing edges, all identified
 *      fragments can also have only gateways with a maximum of two outgoing
 *      edges.
 */
public class TwoModelComparator {


	private ModelInstance baseModelInstance;
	private ModelInstance toCompareModelInstance;
	
	public TwoModelComparator(ModelInstance baseModelInstance,
			ModelInstance toCompareModelInstance) {

		if(baseModelInstance.getProcess().getFlowElements().size() >= toCompareModelInstance.getProcess().getFlowElements().size())
		{
				this.baseModelInstance = baseModelInstance;
				this.toCompareModelInstance = toCompareModelInstance;
		}
		else
		{
			this.baseModelInstance = toCompareModelInstance;
			this.toCompareModelInstance = baseModelInstance;
		}

	}
	
	public ModelInstance getBaseModelInstance() {
		return baseModelInstance;
	}

	public ModelInstance getCompareModelInstance() {
		return toCompareModelInstance;
	}

	
	/***
	 * Runs a DFS algorithm to discover the mapping between the SequenceFlows
	 * @return Returns the discovered @see {@link Fragment}
	 */
	public ArrayList<Fragment>  discoverFragments() {
		Comparison comparison = new Comparison();
		MatchedBranchesMap comparisonMatchedBranchesMap = null;
		List<Checkpoint> checkpoitntsModelA = baseModelInstance.getCheckpoints();
		List<Checkpoint> checkpointsModelB = toCompareModelInstance.getCheckpoints();
		int cnt =0;
		List<MatchedBranchesMap> comparisonMatchedBranchesMaps = new ArrayList<MatchedBranchesMap> ();
		
		Set<FlowNode> checkedNodesA = new HashSet<FlowNode>();
		Set<FlowNode> checkedNodesB = new HashSet<FlowNode>();
	
		Map<FlowNode, Fragment> discoveredFragmentsMap = new HashMap<FlowNode, Fragment>();
		ArrayList<Fragment> fragmentCollection = new ArrayList<Fragment>();
		for (Checkpoint checkPointModelA : checkpoitntsModelA) {
			FlowNode startingNodeA  = checkPointModelA.getCheckPointSequenceFlow().getSourceRef();
			if(!checkedNodesA.contains(startingNodeA))
			{
				checkedNodesA.add((FlowNode) startingNodeA);
					for (Checkpoint checkPointModelB : checkpointsModelB) {
						FlowNode startingNodeB  = checkPointModelB.getCheckPointSequenceFlow().getSourceRef();
						if(!checkedNodesB.contains(startingNodeB))
						{
							checkedNodesB.add((FlowNode) startingNodeB);
							comparisonMatchedBranchesMap  = comparison.initComparisonMatchedBranchesMap(baseModelInstance.getSequenceFlows(), toCompareModelInstance.getSequenceFlows(), checkPointModelA.getCheckPointSequenceFlow(), checkPointModelB.getCheckPointSequenceFlow());
									
							comparisonMatchedBranchesMap = comparison.createMatchedBranchesMap(
									checkPointModelA.getCheckPointSequenceFlow(),
									checkPointModelB.getCheckPointSequenceFlow(),
													comparisonMatchedBranchesMap, new HashSet<FlowNode>());
							
							comparisonMatchedBranchesMaps.add(comparisonMatchedBranchesMap);
						}
					}
					//comparison of this checkpoint finished
					//find max fragment and return only this map
					List<Fragment> fragments = synthesizeFragments(comparisonMatchedBranchesMaps);
					discoveredFragmentsMap = filterDiscoveredFragments(fragments, discoveredFragmentsMap);
					fragmentCollection.addAll(discoveredFragmentsMap.values());
					cnt++;
					//BUG-cause? supposes that checkpoints are given in the parsing sequence (99% they are)
					if(cnt >= checkPointModelA.getCheckPointSequenceFlow().getSourceRef().getOutgoing().size())
					{
						comparison.setVisitedModelA(new LinkedHashSet<SequenceFlow>());
						comparison.setVisitedModelB(new LinkedHashSet<SequenceFlow>());
						cnt=0;
					}
					checkedNodesB = new HashSet<FlowNode>();
					discoveredFragmentsMap  = new HashMap<FlowNode, Fragment>();
			}
			comparisonMatchedBranchesMaps = new ArrayList<MatchedBranchesMap>();
		}
		//return new ArrayList<Fragment>(discoveredFragmentsMap.values());
		return fragmentCollection;
	}

	/**
	 * The edges of a checkpoint might return many subgraphs that were isomorphic
	 * We only need to keep the larger graph starting from two specific checkpoints
	 * @param newFragment
	 * @param discoveredFragmentsMap
	 * @return
	 */
	private Map<FlowNode, Fragment> filterDiscoveredFragments(List<Fragment> fragments,	Map<FlowNode, Fragment> discoveredFragmentsMap) {
		
		for(Fragment newFragment : fragments)
		{
			//if it contains the key then replace if bigger
			if(discoveredFragmentsMap.containsKey(newFragment.getStartingPoint()))
			{
				if(newFragment.size() > discoveredFragmentsMap.get(newFragment.getStartingPoint()).size())
				{
					discoveredFragmentsMap.put(newFragment.getStartingPoint(), newFragment);
				}
			}
			else //if it does not contain the key then put anyway 
			{
				discoveredFragmentsMap.put(newFragment.getStartingPoint(), newFragment);
			}
		}
		return  discoveredFragmentsMap;
	}
	
	private List<Fragment> synthesizeFragments(
			List<MatchedBranchesMap> comparisonMatchedBranchesMaps) {
		List<Fragment> fragments  = new ArrayList<Fragment>();
		Fragment tmpFragment = null;
		for(MatchedBranchesMap matchedBranchesMap : comparisonMatchedBranchesMaps)
		{
			BranchesSynthesizer synthesizer = new BranchesSynthesizer(baseModelInstance, toCompareModelInstance, matchedBranchesMap);	
			tmpFragment = synthesizer.matchedBranchesMapToFragment(matchedBranchesMap);
			
			if(tmpFragment != null)
			{	
				fragments.add(tmpFragment);
			}
		}
		return fragments;
	}	
}
