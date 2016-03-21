package de.unistuttgart.iaas.bpmn.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;

/**
 * This class contains the Metadata of the Fragment
 * @author skourama
 */
public class FragmentDiscoveryMetadata {
	
	private Set<FlowNode> startingPoints;
	private int occurCnt; //Calculates how many times a Fragment occurred in a Model

	/**
	 * Constructor. It creates and initializes the Metadata
	 * When its created it will have for sure one starting point. 
	 * Then it will have to extend*/
	public FragmentDiscoveryMetadata(FlowNode startingPoint){
		startingPoints = new HashSet<FlowNode>();
		startingPoints.add(startingPoint);
		occurCnt = 1;
	}
	
	/**
	 * Extend starting points with these of a newly matched Fragment
	 * @param newStartingPoints
	 */
	public void extendStartingPoints(Set<FlowNode> newStartingPoints)
	{
		
		for(FlowNode newStartingPoint : newStartingPoints)
		{
			if(!this.startingPoints.contains(newStartingPoint))
			{
				this.occurCnt++;
				this.startingPoints.add(newStartingPoint);	
			}
		}
	}
	
	/**
	 * Returns the starting points of this {@link FragmentDiscoveryMetadata} instance
	 * @return - a Set of startingPoints
	 */
	public Set<FlowNode> getStartingPoints()
	{
		return this.startingPoints;
	}
	
	/**
	 * Returns the occurences of the Fragment in the Model
	 * @return the occurences
	 */
	public int getOccurCnt ()
	{
		return this.occurCnt;
	}
	
}
