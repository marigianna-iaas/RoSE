package de.unistuttgart.iaas.bpmn.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import de.unistuttgart.iaas.bpmn.util.CompareUtils;
import de.unistuttgart.iaas.bpmn.util.Constants;


/**
 * This class represents the BPMN 2 model as a Java Object with the usage of
 * EMF BPMN 2 Metamodel {@link https://eclipse.org/modeling/mdt/?project=bpmn2}
 * @author skourama
 *
 */
public class ModelInstance {

	//this list defines the types of the checkpoints
	
	private ResourceSet resourceSet = null;
	private Resource resource = null;
	private Definitions definitions;
	protected Process process;
	private String filePath;
	
	List<Checkpoint> checkPointsList = new ArrayList<Checkpoint>();
	List<SequenceFlow> sequenceFlowsList = new ArrayList<SequenceFlow>();
	private boolean hasCycle = false;
	private int flowNodesCnt = 0;
	
/**
 * Constructor - Creates and initilizes the ModelInstance
 * @param modelFileURI
 */
	public ModelInstance(String modelFileURI) {
		resourceSet = new ResourceSetImpl();
		resource = resourceSet.getResource(URI.createFileURI(modelFileURI),
				true);

		definitions = ((DocumentRoot) resource.getContents().get(0))
				.getDefinitions();
		
		filePath = new String(modelFileURI);
		List<Process> processPart = discoverProcesses();
		if(!processPart.isEmpty())
		{
			//it only gets the first process elements
			//so in the case we need to get all processes of a choreography another handling is necessary
			this.process = processPart.get(0);
			initCheckPointsList();
			initSequenceFlowsList();
			initFlowNodesCnt();		
		}
	}

	


	private void initFlowNodesCnt() {	
		for (TreeIterator<EObject> iterator = CompareUtils
				.getAllContentsWithSpecificTypes(process, Constants.flowNodesType, true); iterator
				.hasNext();) {
			EObject current = iterator.next();
			if (current instanceof FlowNode) {
					flowNodesCnt ++;
				}
			}
	}




	private void initSequenceFlowsList() {
		for (TreeIterator<EObject> iterator = CompareUtils
				.getAllContentsWithSpecificTypes(process, Constants.sequenceFlow, true); iterator
				.hasNext();) {
	
			EObject current = iterator.next();
			if (current instanceof SequenceFlow) {
				SequenceFlow node = (SequenceFlow) current;
					this.sequenceFlowsList.add(node);
				}
		}			
	}

	private void initCheckPointsList() {
		if (checkPointsList.size() == 0)
		{
			//initialize Checkpoints 
			for (TreeIterator<EObject> iterator = CompareUtils
					.getAllContentsWithSpecificTypes(process, Constants.checkPointTypes, true); iterator
					.hasNext();) {

				EObject current = iterator.next();
				if (current instanceof FlowNode) {
					FlowNode node = (FlowNode) current;
					for (SequenceFlow sequenceFlow : node.getOutgoing()) {
						// Create a checkpoint for the identified EObject and add it
						// to the
						// list of checkpoints
						this.checkPointsList.add(new Checkpoint(this, sequenceFlow));

					}
				}
			}

		}
	}

	/**
	 * Returns the list of Checkpoints for this object
	 * For {@link de.unistuttgart.iaas.bpmn.model.Checkpoint} definition
	 * @see {@link http://www.iaas.uni-stuttgart.de/institut/mitarbeiter/skouradaki/res/INPROC-2015-04%20%20-%20Application%20of%20Sub-Graph%20Isomorphism%20to%20Extract%20Reoccurring%20Structures%20from.pdf}
	 * @return a List of @see {@link de.unistuttgart.iaas.bpmn.model.Checkpoint}
	 */
	public List<Checkpoint> getCheckpoints() {
		return checkPointsList;
	}
	
	/**
	 * Get the definitions part of the ModelInstance
	 * @return the definitions part
	 */
	public Definitions getDefinitions()
	{
		return this.definitions;
	}
	
	/**
	 * Returns the path of the file that corresponds to this process model
	 * @return the file path
	 */
	public String getFilePath()
	{
		return filePath;
		
	}
	
	/**
	 * Returns the list of all contained process elements contained in the BPMN process model
	 * @return the list of processes
	 */
	public List<Process> discoverProcesses() {
		List<Process> results = new ArrayList<Process>();

		if (this.definitions != null) {
			for (RootElement element : definitions.getRootElements()) {
				if (element instanceof Process) {
					results.add((Process) element);
				}
			}
		}
		
		return results;
	}	
	
	public org.eclipse.bpmn2.Process getProcess()
	{
		return this.process;
	}

	public void setHasCycle(boolean b) {
		this.hasCycle = b;
	}

	public boolean getHasCycle() {
		return hasCycle;
	}

	public void resetCheckpoints() {
		for(Checkpoint ch : checkPointsList)
		{
			ch.setMatched(false);
		}
	}
	
	public List<SequenceFlow> getSequenceFlows()
	{
		return sequenceFlowsList;
	}

	
	public SequenceFlow getNSequenceFlow(int i)
	{
		return sequenceFlowsList.get(i);
	}
	
	public int getIndexOfSequenceFlow(SequenceFlow seq)
	{
		return sequenceFlowsList.indexOf(seq);
	}
	
	 public int getFlowNodesCnt()
	 {
		 return this.flowNodesCnt;
	 }
	
	
}
