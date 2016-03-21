package de.unistuttgart.iaas.bpmn.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.impl.CallActivityImpl;
import org.eclipse.bpmn2.impl.TaskImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.unistuttgart.iaas.bpmn.compare.algorithm.BranchesSynthesizer;
import de.unistuttgart.iaas.bpmn.compare.algorithm.Comparison;
import de.unistuttgart.iaas.bpmn.compare.algorithm.FragmentsFinder;
import de.unistuttgart.iaas.bpmn.compare.algorithm.MatchedBranchesMap;
import de.unistuttgart.iaas.bpmn.util.CompareUtils;

/**
 * The class models the Fragment (RPF)
 * 
 * @author skourama
 *
 */
public class Fragment extends ModelInstance{

	private final static Logger LOGGER = Logger
			.getLogger(FragmentsFinder.class.getName());
	static {
		LOGGER.setLevel(Level.ALL);
	}

	private UUID id; // the ID of the Fragment
	// the appearance occurrences of the fragment
	//in how many comparisons was the fragment detected
	private int appearanceCnt;
	// at the end the fragment is an incomplete process. Serialized and hold in
	// this variable
	private Process process = null;
	// a fragment has to be found in at least one comparison to be valid.
	// this variable can change depending on our configuration needs
	// TODO: maybe I can move it to the configuration file...
	private final int MIN_APPEARANCE = 1;
	// During the discovery the fragment is a set of SequenceFlows (i.e.
	// duplicates are not allowed)
	// this is stored in the fragmentSequenceFlows variable
	private List<SequenceFlow> fragmentSequenceFlows = null;
	// stores the starting points of the discovered fragments and the models
	// where it was discovered
	// implemented as a Set (i.e. does not allow duplicates)
	private Map<String, FragmentDiscoveryMetadata> metaData;

	private FlowNode startingPoint = null;
	// for getting easily variables from the configuration file


	public Fragment(List<SequenceFlow> fragmentSequenceFlows,
			final String modelAName, final String modelBName, FlowNode startingPointA, FlowNode startingPointB){
		super(modelBName);
		this.id = UUID.randomUUID();
		this.appearanceCnt = MIN_APPEARANCE; // will appear at least 2 times to
												// be a RPF
		this.fragmentSequenceFlows = new ArrayList<SequenceFlow>();
		this.fragmentSequenceFlows.addAll(fragmentSequenceFlows);
		this.process = Bpmn2Factory.eINSTANCE.createProcess();
		metaData = new HashMap<String, FragmentDiscoveryMetadata>();
		this.startingPoint = startingPointB;
		FragmentDiscoveryMetadata md = new FragmentDiscoveryMetadata(startingPointA);
		metaData.put(modelAName, md);	
		FragmentDiscoveryMetadata md2 = new FragmentDiscoveryMetadata(startingPointB);
		metaData.put(modelBName, md2);	
		createFragment(); 
	}

	/**
	 * A new fragment is matched in the comparison. If we check the
	 * RPFCollection @see
	 * {@link de.unistuttgart.iaas.bpmn.collection.RPFCollectionSingleton} and a similar
	 * structure (Fragment) already exists then we need add the Metadata of the
	 * new Fragment to the old one
	 * 
	 * @param match
	 */
	//TODO: check this
	public void ExtendMetaData(final Fragment match) {

		for (Map.Entry<String, FragmentDiscoveryMetadata> newMetadata : match
				.getMetaData().entrySet()) {
			String modelName = newMetadata.getKey();
			// if this fragment was already found in the same model
			if (this.metaData.containsKey(modelName)) {
				// this fragment was found again on the same model
				// was it due to the same starting point? -> it will do nothing
				// was it due to another starting point? -> it will add it and
				// increase counter

				this.metaData.get(modelName).extendStartingPoints(
						match.getMetaData().get(modelName).getStartingPoints());
			} else {

				this.metaData.put(modelName, new FragmentDiscoveryMetadata(
						match.getMetaData().get(modelName).getStartingPoints()
								.iterator().next()));
			}
		}
	}

	
	/**
	 * Returns the appearanceCnt of the Fragment Currently it is used to count
	 * the occurences of the Fragment in the different comparisons
	 * 
	 * @return - the appearanceCnt
	 */
	public int getAppearanceCnt() {
		return appearanceCnt;
	}

	/**
	 * Sets the appearanceCnt of the Fragment Currently it is used to count the
	 * occurences of the Fragment in the different comparisons
	 * 
	 * @param appearanceCnt
	 */
	public void setAppearanceCnt(int appearanceCnt) {
		this.appearanceCnt = appearanceCnt;
	}

	/**
	 * @return a List of SequenceFlow s that constitute the Fragment
	 */
	@Override
	public List<SequenceFlow> getSequenceFlows() {
		return fragmentSequenceFlows;
	}

	/**
	 * Serializes the Fragment to a Process Basically traverses all the fragment
	 * and connects the nodes This way we can visualize the Fragment TODO: check
	 * if with minor fixes it can visualize the Fragment s better
	 */
	public void createFragment() {
		Set<FlowNode> clonedOriginalNodes = new HashSet<FlowNode>();
		HashMap<FlowNode, FlowNode> nodeMap = new HashMap<FlowNode, FlowNode>();
		for (SequenceFlow flow : fragmentSequenceFlows) {
			FlowNode start = flow.getSourceRef();
	
			FlowNode clonedStart = null;
			if (!clonedOriginalNodes.contains(start)) {
				clonedStart = EcoreUtil.copy(start);
				clonedStart.setId(UUID.randomUUID().toString());
				this.getProcess().getFlowElements().add(clonedStart);
	
				nodeMap.put(start, clonedStart);
				clonedOriginalNodes.add(start);
			} else {
				clonedStart = nodeMap.get(start);
			}
			FlowNode end = flow.getTargetRef();
			FlowNode clonedEnd = null;
			if (!clonedOriginalNodes.contains(end)) {
				clonedEnd = EcoreUtil.copy(end);
				if (clonedEnd != null) {
					clonedEnd.setId(UUID.randomUUID().toString());
					this.getProcess().getFlowElements().add(clonedEnd);
	
					nodeMap.put(end, clonedEnd);
					clonedOriginalNodes.add(end);
				}
			} else {
				clonedEnd = nodeMap.get(end);
			}
		  connectFlowNodes(this.process, clonedStart, clonedEnd);
		}
	}

	/**
	 * It is used from @see
	 * de.unistuttgart.iaas.bpmn.model.Fragment.createFragment the Fragment to a
	 * Process. It connects the one node with another TODO: check if with minor
	 * fixes it can visualize the Fragment s better
	 * 
	 * @param process
	 * @param startNode
	 * @param endNode
	 * @return
	 */
	private static SequenceFlow connectFlowNodes(Process process,
			FlowNode startNode, FlowNode endNode) {
		
		// Create a new sequenceFlow object
		SequenceFlow flow = Bpmn2Factory.eINSTANCE.createSequenceFlow();
		// Add the new sequence flow to the process
		process.getFlowElements().add(flow);

		// Use the sequence flow to link the previous and the current flow
		// node
		flow.setSourceRef(startNode);
		flow.setTargetRef(endNode);
		
		startNode.getOutgoing().add(flow);
		endNode.getIncoming().add(flow);

		return flow;
	}

	/**
	 * Returns the extended (incomplete) process of this Fragment
	 * 
	 * @return - the process
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * The UUID of this Fragment. It is basically used to analysis data, and to
	 * create the File Name that will hold the Fragment
	 * 
	 * @return
	 */
	public UUID getId() {
		return this.id;
	}

	/**
	 * Creates the general structure of the process for this fragment as a BPMN
	 * model and saves to file
	 * 
	 * @param targetPath
	 *            - the path where the Fragments will be saved
	 */

	public void serializeFragment(String targetPath) {

		ResourceSetImpl resourceSet = new ResourceSetImpl();

		Resource resource = resourceSet.createResource(URI
				.createFileURI(targetPath + File.separator + "fragment"
						+ this.getId() + ".bpmn2"));

		DocumentRoot root = Bpmn2Factory.eINSTANCE.createDocumentRoot();
		Definitions definitions = Bpmn2Factory.eINSTANCE.createDefinitions();

		Process process = this.getProcess();

		// Add the process to the definitions
		definitions.getRootElements().add(process);
		// Add the definitions to the document root
		root.setDefinitions(definitions);
		// Add the document root to the resource
		resource.getContents().add(root);
		// Try to save the resource
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * This method overrides the equals of the Object Two fragments are equal if
	 * all of their participating SequenceFlow s are connected with FlowNodes
	 * of the same type
	 */
	@Override
	//FIXME: this needs to be fixed then it will work 
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Fragment))
			;// throw exception or ,,,

		if (this == (Fragment) obj) // in case we pass ourself
			return true;

		Fragment fragmentToCompare = (Fragment) obj;
		ArrayList<SequenceFlow> thisSequenceFlows = (ArrayList<SequenceFlow>) this.getSequenceFlows();
		ArrayList<SequenceFlow> toCompareSequenceFlows = (ArrayList<SequenceFlow>) fragmentToCompare
				.getSequenceFlows();
		if (thisSequenceFlows.size() == toCompareSequenceFlows.size()) { //are of the same size

			if(this.getStartingPoint().getClass().getSimpleName().equals(fragmentToCompare.getStartingPoint().getClass().getSimpleName()))
			{

				ArrayList<SequenceFlow> tmpList = new ArrayList<SequenceFlow>();
				
				
				for(SequenceFlow sf1 : thisSequenceFlows)
				{
					for(SequenceFlow sf2: toCompareSequenceFlows)
					{
						if (CompareUtils.checkTypeMatch(sf1.getSourceRef().eClass(), sf2.getSourceRef().eClass())
								&& CompareUtils.checkTypeMatch(sf1.getTargetRef().eClass(), sf2.getTargetRef().eClass())) 
						{
							tmpList.remove(sf2);
						}
					}
					toCompareSequenceFlows = (ArrayList<SequenceFlow>) tmpList.clone();
				}
				if(tmpList.isEmpty())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * For implementing the Map<Fragment,Fragment> we needed to override this
	 */
	@Override
	public int hashCode() {
		return fragmentSequenceFlows.size();
	}

	/**
	 * A Fragment is valid if it contains at least MinEdgesSizeTasks (or Call Activities
	 * for IBM Models) and contains at least minEdgesSize SequenceFlow (and
	 * starts with Checkpoint (this is default from our comparison algorithm and
	 * for this not checked)) (FIXME)
	 * 
	 * @return true if valid - false otherwise
	 */
	public boolean isValid(int minEdgesSize) {
		// check for size
		if (this.getSequenceFlows().size() >= minEdgesSize) {
			// check for existence of activity

			for (SequenceFlow sF : this.getSequenceFlows()) {
				//because of IBM models we need to consider Task EQUALS Call activity
				if (sF.getTargetRef().getClass().getCanonicalName().toString()
						.equals(TaskImpl.class.getCanonicalName().toString())
						||
					sF.getTargetRef().getClass().getCanonicalName().toString()
						.equals(CallActivityImpl.class.getCanonicalName().toString())
							||
					sF.getTargetRef().getClass().getCanonicalName().toString()
						.equals("org.eclipse.bpmn2.impl.SubProcessImpl")) 		
				{
				
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the Metadata of a fragment. It is a Map with pairs of <Fragment
	 * UUID, FragmentDiscoveryMetadata>
	 * 
	 * @return FragmentDiscoveryMetadata
	 */
	public Map<String, FragmentDiscoveryMetadata> getMetaData() {
		return this.metaData;
	}

	public void extendSequenceFlows(List<SequenceFlow> newSequenceFlows) {
		this.fragmentSequenceFlows.addAll(newSequenceFlows);
	}

	
	public boolean isReachable()
	{
		int cnt =0;
		for (FlowElement node : this.getProcess().getFlowElements()) {		
			if(node instanceof FlowNode)
			{
				FlowNode curr = (FlowNode) node;
				if(curr.getIncoming().size() == 0)
				{
					 cnt++;
				}
			}
		}
		return  cnt  == 1 ;
	}
	
	public int size()
	{
		return this.getSequenceFlows().size();
	}
	
	public FlowNode getStartingPoint() {
		return startingPoint;
	}


	public void setStartingPoint(FlowNode startingPoint) {
		this.startingPoint = startingPoint;
	}
}
