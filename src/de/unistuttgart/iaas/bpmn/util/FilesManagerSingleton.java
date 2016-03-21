package de.unistuttgart.iaas.bpmn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.unistuttgart.iaas.bpmn.compare.algorithm.FragmentsFinder;

public final class FilesManagerSingleton {
	//this method logs the info..
		private final static Logger LOGGER = Logger.getLogger(FragmentsFinder.class.getName());
		static{
			LOGGER.setLevel(Level.ALL);
		}
	private static final String CONFIG_FILE = "config.properties";
	private static final String MODELS_PATH = "models_path";
	private static final String RPF_PATH = "rpf_path";
	private static final String STATISTICS_ANALYSIS_PATH = "statistics_analysis_path";
	private static final String MIN_EDGES_SIZE = "MIN_EDGES_SIZE";
	
	Properties prop ;
	private String rpfPath;
	private String rpfAnalysisStatisticsPath;

	private String modelsCollectionPath;
	private int minEdgesSize;
	private List<File> bpmnFilePaths;

	private static FilesManagerSingleton filesManagerInstance = new FilesManagerSingleton();	//for initiating the singleton

	/**
	 * Constructor: Initializes 
	 * @throws IOException
	 */
	//exists only to defeat construction
		protected FilesManagerSingleton(){
		this.prop = new Properties();
		String propFileName = new String(System.getProperty("user.dir")
				+ File.separator + CONFIG_FILE);
		try {
			InputStream inputStream = new FileInputStream(propFileName);
			prop.load(inputStream);
			inputStream.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "IOException: "+ e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IOException: "+ e.getMessage(), e);
		}
	}

	//we call this method to get the singleton class instance
	public static FilesManagerSingleton getInstance() {
		return filesManagerInstance;
	}
	
	public int getMinEdgesSize() {
		minEdgesSize  = Integer.parseInt(prop.getProperty(MIN_EDGES_SIZE));
		return minEdgesSize;
	}
	public String getRpfPath() {
		rpfPath = prop.getProperty(RPF_PATH);
		return rpfPath;
	}

	public String getRpfAnalysisStatisticsPath() {
		rpfAnalysisStatisticsPath = prop.getProperty(STATISTICS_ANALYSIS_PATH);
		return rpfAnalysisStatisticsPath;
	}

	public String getModelsCollectionPath() {
		modelsCollectionPath = prop.getProperty(MODELS_PATH);
		return modelsCollectionPath;
	}

	public void setModelFileNames(String modelsCollectionPath) {
		// get all BPMN files in a folder

		File collectionFolder = new File(modelsCollectionPath);
		String[] listOfBpmnFiles = collectionFolder.list();

		for (String bpmnFileName : listOfBpmnFiles) {
			File bpmnFile = new File(collectionFolder.getAbsolutePath()
					.toString() + File.separator+ bpmnFileName);
			if (bpmnFile.isFile()) {
				this.bpmnFilePaths.add(bpmnFile);
			} else if (bpmnFile.isDirectory()) {
				setModelFileNames(bpmnFile.getAbsolutePath());

			}
		}
	}

	public List<File> getBpmnFilePaths() {
		
		bpmnFilePaths = new ArrayList<File>();
		modelsCollectionPath = getModelsCollectionPath();
		if(! modelsCollectionPath.isEmpty())
		{
			setModelFileNames(modelsCollectionPath);
			return bpmnFilePaths;
		}
		else
			return null;
	}
	
	
}
