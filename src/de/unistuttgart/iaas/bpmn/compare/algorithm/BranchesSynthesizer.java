package de.unistuttgart.iaas.bpmn.compare.algorithm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.bpmn2.SequenceFlow;

import com.googlecode.jctree.ArrayListTree;
import com.googlecode.jctree.NodeNotFoundException;
import com.googlecode.jctree.Tree;

import de.unistuttgart.iaas.bpmn.model.Fragment;
import de.unistuttgart.iaas.bpmn.model.ModelInstance;

public class BranchesSynthesizer {
	//this method logs the info..
	private final static Logger LOGGER = Logger.getLogger(FragmentsFinder.class.getName());
	static{
		LOGGER.setLevel(Level.ALL);
	}
	private ModelInstance baseModelInstance;
	private ModelInstance toCompareModelInstance;

	int [][] matchedBranchesMapArr;
	private Tree<DecisionTreeNodeData> decisionTree  = new ArrayListTree<DecisionTreeNodeData>();;
	
	private int rowsSize =  0;
	private int columnsSize = 0;
	
    Queue<DecisionTreeNodeData> childrenQueue  = new LinkedList<DecisionTreeNodeData>();
    
    List<List<DecisionTreeNodeData>> decisionTreePaths = new ArrayList<List<DecisionTreeNodeData>>();;
	
	public BranchesSynthesizer(ModelInstance baseModelInstance, ModelInstance toCompareModelInstance, MatchedBranchesMap matchedBranchesMap)
	{
		
		this.baseModelInstance = baseModelInstance;
		this.toCompareModelInstance = toCompareModelInstance;
		rowsSize =  baseModelInstance.getSequenceFlows().size();
		columnsSize = toCompareModelInstance.getSequenceFlows().size();
		matchedBranchesMapArr = matchedBranchesMap.getMatchedBranchesMapArr();
		initDecisionTree();
		
	}

	private void initDecisionTree() {
		if(matchedBranchesMapArr[0][0] == 1)
		{
			decisionTree.add(new DecisionTreeNodeData(0, 0, matchedBranchesMapArr, columnsSize,rowsSize)); //add root
			fixDecisionTree2(decisionTree.root());
			fixPathsOfDecisionTree();		
			sortDecisionTreePaths();
		}
	}

	@SuppressWarnings("deprecation")	
	private void addChildren(DecisionTreeNodeData parent){
		List<DecisionTreeNodeData> children = parent.getImediateChildrenFromArray();
		if(!children.isEmpty()){
			DecisionTreeNodeData child = children.get(0);
			try {
				decisionTree.add(parent, child);	
			} catch (NodeNotFoundException e) {
				e.printStackTrace();
			}
			childrenQueue.add(child);	
		}
	}
	
	@SuppressWarnings("deprecation")	
	private void addChildren2(DecisionTreeNodeData parent){
		List<DecisionTreeNodeData> children = parent.getImediateChildrenFromArray();
		for(DecisionTreeNodeData child : children){
			try {
				decisionTree.add(parent, child);	
			} catch (NodeNotFoundException e) {
				e.printStackTrace();
			}
			childrenQueue.add(child);	
		}
	}

	private void fixDecisionTree2(DecisionTreeNodeData root) {
		addChildren(root);
		try{	
			while(!childrenQueue.isEmpty()){
				DecisionTreeNodeData child = (DecisionTreeNodeData) childrenQueue.poll();
				if(child != null){
					addChildren(child);
				}
			}
		}
		catch(Throwable e)
		{
			//TODO: Create a utility function to convert an exception to string ...
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LOGGER.severe(sw.toString());
			LOGGER.severe("Exception thrown - check for cycle");
			LOGGER.severe("Model A: "+ baseModelInstance.getFilePath() + "- Model B:"+ toCompareModelInstance.getFilePath());
		}		
	}
	
	@SuppressWarnings("deprecation")
	private void fixDecisionTree(DecisionTreeNodeData parent) {
		System.out.println(childrenQueue.size());
		List<DecisionTreeNodeData> children = new ArrayList<DecisionTreeNodeData>();
		children.addAll(parent.getImediateChildrenFromArray() );
		for(DecisionTreeNodeData child : children)
		{
			try {
				decisionTree.add(parent, child);	
			} catch (NodeNotFoundException e) {
				e.printStackTrace();
			}
			childrenQueue.add(child);		
		}
		try{		
			DecisionTreeNodeData child = (DecisionTreeNodeData) childrenQueue.poll();
			if(child != null){
				fixDecisionTree(child);
			}
		}
		catch(Throwable e)
		{
			//TODO: Create a utility function to convert an exception to string ...
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LOGGER.severe(sw.toString());
			LOGGER.severe("Exception thrown - check for cycle");
			LOGGER.severe("Model A: "+ baseModelInstance.getFilePath() + "- Model B:"+ toCompareModelInstance.getFilePath());
		}		
	}
	
	public void printTree(DecisionTreeNodeData parent)
	{
		try {
			for(DecisionTreeNodeData child: decisionTree.children(parent))
			{
				System.out.println(parent.toString() + "-->"+ child.toString());
				printTree(child);
			}
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void fixPathsOfDecisionTree()
	{
		for(DecisionTreeNodeData node: decisionTree.leaves())
		{
			List<DecisionTreeNodeData> path = new ArrayList<DecisionTreeNodeData>();
			decisionTreePaths.add(path);
			path.add(node);
			path = getPathsOfDecisionTree( node, path);
		}
		sortDecisionTreePaths();
	}
	
	@SuppressWarnings("unused")
	private void printPaths()
	{
		for(List<DecisionTreeNodeData> path : decisionTreePaths )
		{
			for(DecisionTreeNodeData nodeInPath : path)
			{
				System.out.print(nodeInPath.toString(toCompareModelInstance.getSequenceFlows()) + "--" );
			}
			System.out.println(path.size());
		}
		System.out.println(decisionTreePaths.size());
	}
	
	private List<DecisionTreeNodeData> getPathsOfDecisionTree(DecisionTreeNodeData node, List<DecisionTreeNodeData> path)
	{
		try {
			DecisionTreeNodeData parent = decisionTree.parent(node);
			if(parent!= null)
			{
				path.add(parent);
				path = getPathsOfDecisionTree(parent, path);
			}
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		return path;
	}
	
	/**
	 * @param matchedBranchesMap
	 * @return the fragment that originates from the matched branches map. Returns <code>null</code> if fragment not created
	 */
	public Fragment matchedBranchesMapToFragment(MatchedBranchesMap matchedBranchesMap)
	{	
		Fragment newFragment = null;

		for(List<DecisionTreeNodeData> path : decisionTreePaths )
		{
			List<SequenceFlow> matchedSequenceFlows = new ArrayList<SequenceFlow>();
			for(DecisionTreeNodeData nodeInPath : path)
			{
				matchedSequenceFlows.add(
						matchedBranchesMap.getToCompareModelInstanceSequenceFlows().get(nodeInPath.getColumn()));
			}
			newFragment = new Fragment(matchedSequenceFlows,baseModelInstance.getFilePath(), toCompareModelInstance.getFilePath(), matchedBranchesMap.getBaseModelInstanceSequenceFlows().get(0).getSourceRef(), matchedBranchesMap.getToCompareModelInstanceSequenceFlows().get(0).getSourceRef());
			if(newFragment.isReachable() )
			{
				return newFragment;
			}
		}
		return null;		
	}
	
	/**
	 * Sorts the paths of the decision tree with the order of length
	 */
	private void sortDecisionTreePaths(){
		Collections.sort(decisionTreePaths, new Comparator<List<DecisionTreeNodeData>>(){

			@Override
			public int compare(List<DecisionTreeNodeData> o1, List<DecisionTreeNodeData> o2) {
				return o2.size() - o1.size(); //reverse order the list
			}}
		);
	}

	
}

