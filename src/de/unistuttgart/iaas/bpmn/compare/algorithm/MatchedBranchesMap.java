package de.unistuttgart.iaas.bpmn.compare.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.SequenceFlow;

public class MatchedBranchesMap {

	private int matchedBranchesMapArr [][]= null;
	private int  rowsSize;

	private int columnsSize;
	
	private List<SequenceFlow> baseModelInstanceSequenceFlows;

	private List<SequenceFlow> toCompareModelInstanceSequenceFlows;
 	
	public MatchedBranchesMap(int rowsSize, int columnsSize) {
		this.rowsSize = rowsSize;
		this.columnsSize = columnsSize;
		matchedBranchesMapArr = new int[rowsSize][columnsSize];
		init(rowsSize, columnsSize);
	}
	
	private void init(int rowsSize, int columnsSize)
	{
		for(int i=0; i< rowsSize; i++)
		{
			for(int j =0 ; j<columnsSize; j++)
			{
				matchedBranchesMapArr[i][j] =0;
			}
		}
	}

	public int[][] getMatchedBranchesMapArr() {
		return matchedBranchesMapArr;
	}
	
	
	//TODO: check table limits for row and column
	public void setMappingMatchedBranches(int row, int column) {
		this.matchedBranchesMapArr[row][column] =1;
	}
	
	public int getRowsSize() {
		return rowsSize;
	}

	public int getColumnsSize() {
		return columnsSize;
	}

	private int getIndexOf(List<SequenceFlow> list, SequenceFlow sf)
	{
		int cnt =0;
		for(SequenceFlow curr : list)
		{
			if (curr.getId().equals(sf.getId()))
				return cnt;
			cnt++;
		}
		return -1;
	}
	
	
	public void fixSourcePointBaseModelInstance(SequenceFlow sequenceFlow) {

		int index = getIndexOf(baseModelInstanceSequenceFlows, sequenceFlow);
		Collections.swap(baseModelInstanceSequenceFlows, 0, index);
	}
	
	public void fixSourcePointToCompareModelInstance(SequenceFlow sequenceFlow) {
		int index = getIndexOf(toCompareModelInstanceSequenceFlows, sequenceFlow);

		Collections.swap(toCompareModelInstanceSequenceFlows, 0, index);
	}
	
	public List<SequenceFlow> getBaseModelInstanceSequenceFlows() {
		return baseModelInstanceSequenceFlows;
	}

	@SuppressWarnings("unchecked")
	public void setBaseModelInstanceSequenceFlows(
			ArrayList<SequenceFlow> baseModelInstanceSequenceFlows) {
		this.baseModelInstanceSequenceFlows =  (List<SequenceFlow>) baseModelInstanceSequenceFlows.clone();
	
	}

	public List<SequenceFlow> getToCompareModelInstanceSequenceFlows() {
		return toCompareModelInstanceSequenceFlows;
	}

	@SuppressWarnings("unchecked")
	public void setToCompareModelInstanceSequenceFlows(
			ArrayList<SequenceFlow> toCompareModelInstanceSequenceFlows) {
		this.toCompareModelInstanceSequenceFlows = (List<SequenceFlow>) toCompareModelInstanceSequenceFlows.clone();
		
	}

}
