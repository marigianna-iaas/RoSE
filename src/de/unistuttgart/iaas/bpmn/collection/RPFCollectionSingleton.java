package de.unistuttgart.iaas.bpmn.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unistuttgart.iaas.bpmn.model.Fragment;
import de.unistuttgart.iaas.bpmn.util.FilesManagerSingleton;


/**
 * This class is a Holder of all the discovered Fragments
 * It is implemented with Map to reduce duplicates
 * I.e. each (@see Fragment) entry has a unique structure
 * @author skourama
 *
 */
public class RPFCollectionSingleton {
	//The collection fragment holder
	private final Map<Fragment, Fragment> fragments = new HashMap<Fragment, Fragment>();
	private static RPFCollectionSingleton rpfCollectionSingleton = new RPFCollectionSingleton();

	/**
	 * Constructor
	 */
	protected RPFCollectionSingleton() {
		// Exists only to defeat instantiation.
	}
	
	//we call this method to get the singleton class instance
	public static RPFCollectionSingleton getInstance() {
			
		return rpfCollectionSingleton;
	}
		
	/**
	 * This method will do the appropriate checks to see if an structure already exists in the collection
	 * If not it will add it
	 * @param matches - the matched sequence flows returned from the discovery
	 * @param minEdgesSize 
	 */
	public void addMatchesToCollection(List<Fragment> matchedFragments, int minEdgesSize, String rpfPath) {
		
		for (Fragment fragment : matchedFragments) {
			if(fragment.isValid(minEdgesSize))
			{
				if (fragments.isEmpty())
				{
					fragments.put(fragment, fragment);
					fragment.serializeFragment(rpfPath);
				}
				else 
				if (fragments.containsKey(fragment)){	//there is a similar fragment
					Fragment tmpFragment = fragments.get(fragment);
					//FIXME: the setAppearanceCnt counts how many times a Fragment appeared in comparisons
					//it is faulty multiplied by the comparisons and needs to be fixed otherwise
					tmpFragment.setAppearanceCnt(tmpFragment.getAppearanceCnt() + 1); 
					//here it adds the metadata of the found fragment to its duplicate
					//i.e. in which model this one was found etc.
					tmpFragment.ExtendMetaData(fragment); //FIXME: check this works nicely
				}
				else {	//new fragment;
					fragments.put(fragment, fragment);
					fragment.serializeFragment(rpfPath);
				}
			}
		}
	}
	
	
	/**
	 * Returns the RPF Collection as Set of RPFs
	 * @return a set of RPFs which is the RPFCollection of the execution
	 */
	public Set<Fragment> getAllFragments() {
		return fragments.keySet();
	}
	
	
	/**
	 * Currently the Fragments  are only Lists of 
	 * It serializes the RPFs in a format to look more like a process
	 * @param FragmentPath - the path of the file that will be used to store this Fragment
	 */
	public void createRPFFiles(String rpfPath) {
		System.out.println("Exported fragments saved at:" +rpfPath);
		//at the end all discovered fragments are serialized
		for(Fragment fragment : fragments.keySet())
		{
			fragment.serializeFragment(rpfPath);
		}

	}

	public void createFragments() {
		for(Fragment fragment : fragments.values())
		{
			fragment.createFragment();
		}
	}
	
}
