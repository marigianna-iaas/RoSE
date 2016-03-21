package de.unistuttgart.iaas.bpmn.compare.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.SequenceFlow;

public class DecisionTreeNodeData {
	private final int ROWSUM = 1;
	private final int COLUMNSUM = 2;
	
	int row = 0;
	int column = 0;
	int [][] arrayState = null;
	int rowsSize =0;
	int columnsSize =0;
	//int rowOfChildren = -1;
	
	public DecisionTreeNodeData(int row, int column, int [][] arrayState, int columnsSize, int rowsSize) {
		this.row = row;
		this.column = column;
		this.arrayState = deepCopyIntMatrix(arrayState);
		this.rowsSize =rowsSize;
		this.columnsSize = columnsSize;
		fixArrayState(row, column, columnsSize, rowsSize);
	}
	
	private int[][] deepCopyIntMatrix(int[][] input) {
	    if (input == null)
	        return null;
	    int[][] result = new int[input.length][];
	    for (int r = 0; r < input.length; r++) {
	        result[r] = input[r].clone();
	    }
	    return result;
	}
	
	public int getRow() {
		return row;
	}


	public void setRow(int row) {
		this.row = row;
	}


	public int getColumn() {
		return column;
	}


	public void setColumn(int column) {
		this.column = column;
	}


	public int[][] getArrayState() {
		return arrayState;
	}


	public void setArrayState(int [][] arrayState) {
		this.arrayState = arrayState;
	}

	public int getArrayStateElement(int i, int j) {
		
		return this.arrayState[i][j];
	}
	
	public void fixArrayState(int row, int column,int columnsSize, int rowsSize)
	{
		//left
		makePosZero(row, 0, column, COLUMNSUM);
		//right
		makePosZero(row, column+1, columnsSize, COLUMNSUM);
		//up
		makePosZero(column, 0, row, ROWSUM);
		//down
		makePosZero(column, row+1, rowsSize, ROWSUM);
	}
	
	private void makePosZero(int stablePos, int iterationStart, int iterationEnd, int columnsOrRowsIndicator) {
		
		for(int j= iterationStart; j<iterationEnd; j++)
		{
			if(columnsOrRowsIndicator == COLUMNSUM)
			{
				this.arrayState[stablePos][j] = 0;
			}
			else if(columnsOrRowsIndicator == ROWSUM)
			{
				this.arrayState[j][stablePos] = 0;
			}
		}
	}
	
	
	public List<DecisionTreeNodeData> getImediateChildrenFromArray()
	{
		List<DecisionTreeNodeData> immediateChildren = new ArrayList<DecisionTreeNodeData>();
		//Start searching from the first row
		for(int i= row+1; i< rowsSize; i++ )
		{
			for(int j=0; j< columnsSize; j++)
			{
				if(arrayState[i][j] == 1)
				{
					immediateChildren.add(new DecisionTreeNodeData(i, j, arrayState, columnsSize, rowsSize));
				}
			}
			 //before going to the next row if found children break
			if(!immediateChildren.isEmpty())
				break;
		}
		return immediateChildren;
	}

	public String toString(List<SequenceFlow> toCompareModelInstanceSequenceFlows)
	{
		return "[" + 
				toCompareModelInstanceSequenceFlows.get(column).getSourceRef().getName() + "-" + toCompareModelInstanceSequenceFlows.get(column).getId() +	
				"-" + toCompareModelInstanceSequenceFlows.get(column).getTargetRef().getName()+  "]";
		
	}
	
}
