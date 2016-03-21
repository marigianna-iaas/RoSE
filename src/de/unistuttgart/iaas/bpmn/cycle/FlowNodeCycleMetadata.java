package de.unistuttgart.iaas.bpmn.cycle;

import org.eclipse.bpmn2.FlowNode;

public class FlowNodeCycleMetadata {


	int index;
	int lowlink;
	FlowNode node;
	boolean onStack;
	
	public FlowNodeCycleMetadata(int index, int lowlink)
	{
		this.index = index;
		this.lowlink = lowlink;
		this.onStack = false;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLowlink() {
		return lowlink;
	}

	public void setLowlink(int lowlink) {
		this.lowlink = lowlink;
	}

	public FlowNode getNode() {
		return node;
	}

	public void setNode(FlowNode node) {
		this.node = node;
	}
	
	public boolean isOnStack() {
		return onStack;
	}

	public void setOnStack(boolean onStack) {
		this.onStack = onStack;
	}


}
