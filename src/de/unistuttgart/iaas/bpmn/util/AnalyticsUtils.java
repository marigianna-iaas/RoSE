package de.unistuttgart.iaas.bpmn.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.unistuttgart.iaas.bpmn.model.Fragment;
import de.unistuttgart.iaas.bpmn.model.FragmentDiscoveryMetadata;


/**
 * 
 * @author skourama
 * This class is responsible for writing the statistical results of RPF discovery to csv Files
 */
public class AnalyticsUtils {
	private static final int HEADER_WRITING = 1;

	public static void writeFragmentDiscoveryAnalysisCsv(
			String statisticsFileName, Set<Fragment> allFragments) {
		int recordsCnt =0;
		int fileCnt = 1;
		try {
			
			FileWriter writer =null;
			
			if (!allFragments.isEmpty()) {
				for (Fragment fragment : allFragments) {
					for (Map.Entry<String, FragmentDiscoveryMetadata> newMetadataSumPair : fragment.getMetaData().entrySet()) {
							if(recordsCnt % 10000 == 0)
							{
								if(writer != null)
								{
									writer.flush();
									writer.close();
								}
								writer = new FileWriter(statisticsFileName+"_"+(fileCnt++)+".csv", true);
								writer.append("Fragment ID");
								writer.append(',');
								writer.append("Comparisons Appearance");
								writer.append(',');
								writer.append("Model Name");
								writer.append(',');
								writer.append("#Appearance in Model");
								writer.append('\n');
							}
							writer.append(fragment.getId().toString());
							writer.append(',');

							writer.append(Integer.toString(fragment.getAppearanceCnt()));
							writer.append(',');
							writer.append(newMetadataSumPair.getKey());	//modelName
							writer.append(',');
							
							writer.append(Integer.toString(newMetadataSumPair.getValue().getOccurCnt())); //appearance in Model
							writer.append('\n');
							recordsCnt ++;
					}

				}
			}
			if(writer != null)
			{
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}

	public static void writeComparisonDetailsCsv(
			String comparisonAnalysisFileName, String fileName1,
			String fileName2, String comparisonComplexity, String CFCModelA,
			String CFCModelB, String size, String time, int depth) {

		try {

			if (depth == HEADER_WRITING) {
				File comparisonAnalysisFile = new File(
						comparisonAnalysisFileName);

				if (!comparisonAnalysisFile.exists()) {
					comparisonAnalysisFile.createNewFile();
				}
			}

			FileWriter writer = new FileWriter(comparisonAnalysisFileName, true);

			writer.append(fileName1);
			writer.append(',');

			writer.append(fileName2);
			writer.append(',');

			writer.append(comparisonComplexity);
			writer.append(',');

			writer.append(CFCModelA);
			writer.append(',');

			writer.append(CFCModelB);
			writer.append(',');

			writer.append(size);
			writer.append(',');

			writer.append(time);
			writer.append('\n');

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeFlowNodesCnt(String rpfAnalysisStatisticsFileName,
			Map<Integer, Integer> frequenciesCntSorted) {
		try {

			
			File cntAnalysisFile = new File(
						rpfAnalysisStatisticsFileName);

			if (!cntAnalysisFile.exists()) {
				cntAnalysisFile.createNewFile();
			}

			FileWriter writer = new FileWriter(rpfAnalysisStatisticsFileName, true);

			writer.append("#Frequency");
			writer.append(',');

			writer.append("Size");
			writer.append('\n');
			
			for(Map.Entry<Integer,Integer> entry : frequenciesCntSorted.entrySet())
			{
				writer.append(entry.getValue().toString());
				writer.append(',');
				
				writer.append(entry.getKey().toString());
				writer.append('\n');
			}
			

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeModelSize(String file, String processFileName, int flowNodesCnt) {
		File modelSizeFile = new File(file);
		try {
			if (!modelSizeFile.exists()) {
				
					modelSizeFile.createNewFile();
			
			}
		
			FileWriter writer = new FileWriter(file, true);
			writer.append(processFileName);
			writer.append(',');
			
			writer.append(Integer.toString(flowNodesCnt));
			writer.append('\n');
			writer.flush();
			writer.close();

		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
