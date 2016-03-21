package de.unistuttgart.iaas.bpmn.model;

import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.impl.FlowNodeImpl;

import de.unistuttgart.iaas.bpmn.util.CompareUtils;

public class Checkpoint extends FlowNodeImpl{

	private ModelInstance model = null;
	private SequenceFlow checkpointSequenceFlow = null;
	boolean matched = false;
	

	public Checkpoint(ModelInstance model) {
		this.model = model;
	}

	public Checkpoint(ModelInstance model, SequenceFlow sequenceFlow) {
		super();
		this.model = model;
		this.checkpointSequenceFlow = sequenceFlow;
	}

	public SequenceFlow getCheckPointSequenceFlow() {
		return checkpointSequenceFlow;
	}

	public void setCheckPointSequenceFlow(SequenceFlow sequenceFlow) {
		this.checkpointSequenceFlow = sequenceFlow;
	}

	@Override
	public boolean equals(Object obj) {
		// Check if the checkpoint objects are equal
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof Checkpoint) {
			// Check if the referenced model elements are of the same type
			if (CompareUtils.checkTypeMatch(this.checkpointSequenceFlow.getSourceRef().eClass(),
					((Checkpoint) obj).getCheckPointSequenceFlow().getSourceRef().eClass())
					&& CompareUtils.checkTypeMatch(this.checkpointSequenceFlow.getTargetRef().eClass(),
							((Checkpoint) obj).getCheckPointSequenceFlow().getTargetRef().eClass())) {
				return true;
			}
		}
		return false;
	}

	public ModelInstance getModel() {
		return model;
	}
	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

}
