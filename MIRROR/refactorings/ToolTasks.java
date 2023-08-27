package refactorings;

import java.util.ArrayList;

import MIRROR.Configuration;
import recoder.CrossReferenceServiceConfiguration;
import recoder.ParserException;
import recoder.io.PropertyNames;
import refactorings.Refactoring;
import refactorings.field.*;
import refactorings.method.*;
import refactorings.type.*;
import searches.MultiObjectiveSearch;
import searches.Search;
import refactorings.Tasks;

public class ToolTasks extends Tasks
{
	// No attributes - empty constructor.
	public ToolTasks()
	{
		super();
	}
	
	public ToolTasks(String pathway)
	{
		super(pathway);		
	}
	
	public void run()
	{	
		System.out.println("kaishi :");
		String[] input = new String[]{
//		"C:\\Users\\hp\\Desktop\\测试程序-版本\\测试程序-版本\\GanttProject\\ganttproject-1.10.2-master"};
//		"C:\\Users\\gk152\\Desktop\\test\\ant-1.8.2"};
//		"C:\\Users\\gk152\\Desktop\\testprogram\\ganttproject-1.10.2-master"};
//		"C:\\Users\\gk152\\Desktop\\testprogram\\jfreechart-1.5.2"};
//		"C:\\Users\\gk152\\Desktop\\testprogram\\jhotdraw60b1-master"};
//		"C:\\Users\\gk152\\Desktop\\testprogram\\junit4-r4.10"};
		"C:\\Users\\gk152\\Desktop\\testprogram\\mango-mango-1.4.0"};
//		"C:\\Users\\gk152\\Desktop\\testprogram\\mybatis-mybatis-3.0.1"};
//		"C:\\Users\\gk152\\Desktop\\testprogram\\rhino-Rhino1_7R1_BRANCH"};
//		"C:\\Users\\gk152\\Desktop\\testprogram\\xerces2-j-Xerces-J_2_7_0"};
		
//				"./data/original/mango",
//				"./data/original/beaver/beaver-0.9.11",
//				"./data/original/apachexmlrpc/apachexmlrpc-2.0",
//				"./data/original/jhotdraw/jhotdraw-5.3"};
		
		// Create an initial service configuration to be overwritten.
		// Reads the source code from the specified directory.
		CrossReferenceServiceConfiguration sc = new CrossReferenceServiceConfiguration();
		String[][] sourceFiles = new String[][]{
			super.read(input[0])};
//			super.read(input[1]),
//			super.read(input[2]),
//			super.read(input[3]),
//			super.read(input[4])};
		System.out.println("creat empty list of refactoring:");
		// Create empty list of refactorings.
		// Reads the metric configuration in from a specified text file.
		ArrayList<Refactoring> refactorings = new ArrayList<Refactoring>();
//		Configuration c1 = new Configuration("./configurations/qualityfunction-objective1.txt", refactorings);
//		Configuration c2 = new Configuration("./configurations/qualityfunction-objective2.txt", refactorings);
//		Configuration c3 = new Configuration("./configurations/qualityfunction-objective3.txt", refactorings);
//		Configuration[] cMO = {new Configuration("./configurations/qualityfunction-objective1.txt"), new Configuration("./configurations/qualityfunction-objective2.txt"), 
//				               new Configuration("./configurations/qualityfunction-objective3.txt")};
		Configuration c1 = new Configuration("./configurations/quality-objective1.txt", refactorings);
		Configuration c2 = new Configuration("./configurations/codesmells-objective2.txt.txt", refactorings);
		Configuration c3 = new Configuration("./configurations/history-objective3.txt", refactorings);
		Configuration[] cMO = {new Configuration("./configurations/qualityobjective1.txt"), new Configuration("./configurations/codesmells-objective2.txt"), 
				               new Configuration("./configurations/history-objective3.txt")};
		// Initialise search tasks.
		ArrayList<Search> searches = new ArrayList<Search>();
		searches.add(new MultiObjectiveSearch(sc, cMO, refactorings, sourceFiles[0], 100, 50, 0.2f, 0.8f));
		searches.add(new MultiObjectiveSearch(sc, cMO, refactorings, sourceFiles[0], 100, 50, 0.2f, 0.8f));
		searches.add(new MultiObjectiveSearch(sc, cMO, refactorings, sourceFiles[0], 100, 50, 0.2f, 0.8f));
		searches.add(new MultiObjectiveSearch(sc, cMO, refactorings, sourceFiles[0], 100, 50, 0.2f, 0.8f));
		searches.add(new MultiObjectiveSearch(sc, cMO, refactorings, sourceFiles[0], 100, 50, 0.2f, 0.8f));
		searches.add(new MultiObjectiveSearch(sc, cMO, refactorings, sourceFiles[0], 100, 50, 0.2f, 0.8f));
//		System.out.println("search end");
//		long end = System.currentTimeMillis(); 
//		System.out.println("绋嬪簭鎼滅储杩愯鏃堕棿锛�" + (end - start)/1000 + "s");
		
		// Create list of output directories for
		// each refactored project to be written to.
		String[] outputDir = new String[]{
				"./data/refactored/ToolExperiment/Part4/Multi-Objective/"};
//				"./data/refactored/ToolExperiment/Part4/Mono-Objective/Objective1/",
//				"./data/refactored/ToolExperiment/Part4/Mono-Objective/Objective2/",
//				"./data/refactored/ToolExperiment/Part4/Mono-Objective/Objective3/"};
		
		System.out.println("Refactoring result:");
		// Create list of output directories for
		// each result data output to be written to.
		String[] resultsDir = new String[]{
//				"C:/Users/hp/Desktop/测试程序-版本/测试程序-版本/GanttProject/ganttproject-1.10.2-master/Multi-Objective"};
//				"C:/Users/gk152/Desktop/test/ant-1.8.2/Multi-Objective"};
//				"C:/Users/gk152/Desktop/testprogram/ganttproject-1.10.2-master/Multi-Objective"};
//				"C:/Users/gk152/Desktop/testprogram/jfreechart-1.5.2/Multi-Objective"};
//				"C:/Users/gk152/Desktop/testprogram/jhotdraw60b1-master/Multi-Objective"};
//				"C:/Users/gk152/Desktop/testprogram/junit4-r4.10/Multi-Objective"};
				"C:/Users/gk152/Desktop/testprogram/mango-mango-1.4.0/Multi-Objective"};
//				"C:/Users/gk152/Desktop/testprogram/mybatis-mybatis-3.0.1/Multi-Objective"};
//				"C:/Users/gk152/Desktop/testprogram/rhino-Rhino1_7R1_BRANCH/Multi-Objective"};
//				"C:/Users/gk152/Desktop/testprogram/xerces2-j-Xerces-J_2_7_0/Multi-Objective"};

//				"./results/ToolExperiment/Part4/Multi-Objective/"};
//				"./results/ToolExperiment/Part4/Mono-Objective/Objective1/",
//				"./results/ToolExperiment/Part4/Mono-Objective/Objective2/",
//				"./results/ToolExperiment/Part4/Mono-Objective/Objective3/"};
		
		// Create a list of configurations for the genetic algorithm to set.
//		String[] configurations = new String[]{
//				"./configurations/qualityfunction-objective1.txt",
//				"./configurations/qualityfunction-objective2.txt",
//				"./configurations/qualityfunction-objective3.txt"};
		//mybatis-mybatis-3.0.1
//		String[] configurations = new String[]{
//				"C:/Users/gk152/Desktop/testprogram/mybatis-mybatis-3.0.1/configurations/quality-objective1.txt",
//				"C:/Users/gk152/Desktop/testprogram/mybatis-mybatis-3.0.1/configurations/codesmells-objective2.txt",
//				"C:/Users/gk152/Desktop/testprogram/mybatis-mybatis-3.0.1/configurations/history-objective3.txt"};
		
		//GanttProject
//		String[] configurations = new String[]{
//				"C:/Users/gk152/Desktop/testprogram/ganttproject-1.10.2-master/configurations/quality-objective1.txt",
//				"C:/Users/gk152/Desktop/testprogram/ganttproject-1.10.2-master/configurations/codesmells-objective2.txt",
//				"C:/Users/gk152/Desktop/testprogram/ganttproject-1.10.2-master/configurations/history-objective3.txt"};
//		String[] configurations = new String[]{
//				"C:/Users/hp/Desktop/娴嬭瘯绋嬪簭-鐗堟湰/娴嬭瘯绋嬪簭-鐗堟湰/GanttProject/ganttproject-1.10.2-master/configurations/quality-objective1.txt",
//				"C:/Users/hp/Desktop/娴嬭瘯绋嬪簭-鐗堟湰/娴嬭瘯绋嬪簭-鐗堟湰/GanttProject/ganttproject-1.10.2-master/configurations/codesmells-objective2.txt",
//				"C:/Users/hp/Desktop/娴嬭瘯绋嬪簭-鐗堟湰/娴嬭瘯绋嬪簭-鐗堟湰/GanttProject/ganttproject-1.10.2-master/configurations/history-objective3.txt"};
		
		//junit
//		String[] configurations = new String[]{
//		"C:/Users/gk152/Desktop/testprogram/junit4-r4.10/configurations/quality-objective1.txt",
//		"C:/Users/gk152/Desktop/testprogram/junit4-r4.10/configurations/codesmells-objective2.txt",
//		"C:/Users/gk152/Desktop/testprogram/junit4-r4.10/configurations/history-objective3.txt"};
		
		//mango
		String[] configurations = new String[]{
		"C:/Users/gk152/Desktop/testprogram/mango-mango-1.4.0/configurations/quality-objective1.txt",
		"C:/Users/gk152/Desktop/testprogram/mango-mango-1.4.0/configurations/codesmells-objective2.txt",
		"C:/Users/gk152/Desktop/testprogram/mango-mango-1.4.0/configurations/history-objective3.txt"};
		
		//Rhino
//		String[] configurations = new String[]{
//		"C:/Users/gk152/Desktop/testprogram/rhino-Rhino1_7R1_BRANCH/configurations/quality-objective1.txt",
//		"C:/Users/gk152/Desktop/testprogram/rhino-Rhino1_7R1_BRANCH/configurations/codesmells-objective2.txt",
//		"C:/Users/gk152/Desktop/testprogram/rhino-Rhino1_7R1_BRANCH/configurations/history-objective3.txt"};

		//JhotDraw
//		String[] configurations = new String[]{
//		"C:/Users/gk152/Desktop/testprogram/jhotdraw-jhotdraw60b1-release/jhotdraw6/configurations/quality-objective1.txt",
//		"C:/Users/gk152/Desktop/testprogram/jhotdraw-jhotdraw60b1-release/jhotdraw6/configurations/codesmells-objective2.txt",
//		"C:/Users/gk152/Desktop/testprogram/jhotdraw-jhotdraw60b1-release/jhotdraw6/configurations/history-objective3.txt"};

		//Apache Ant
//		String[] configurations = new String[]{
//		"C:/Users/gk152/Desktop/test/ant-1.8.2/configurations/quality-objective1.txt",
//		"C:/Users/gk152/Desktop/test/ant-1.8.2/configurations/codesmells-objective2.txt",
//		"C:/Users/gk152/Desktop/test/ant-1.8.2/configurations/history-objective3.txt"};

		//JFreechart
//		String[] configurations = new String[]{
//		"C:/Users/gk152/Desktop/testprogram/jfreechart-1.5.2/configurations/quality-objective1.txt",
//		"C:/Users/gk152/Desktop/testprogram/jfreechart-1.5.2/configurations/codesmells-objective2.txt",
//		"C:/Users/gk152/Desktop/testprogram/jfreechart-1.5.2/configurations/history-objective3.txt"};

		//Xerces-J
//		String[] configurations = new String[]{
//		"C:/Users/gk152/Desktop/testprogram/xerces2-j-Xerces-J_2_7_0/configurations/quality-objective1.txt",
//		"C:/Users/gk152/Desktop/testprogram/xerces2-j-Xerces-J_2_7_0/configurations/codesmells-objective2.txt",
//		"C:/Users/gk152/Desktop/testprogram/xerces2-j-Xerces-J_2_7_0/configurations/history-objective3.txt"};
		
		long timeTaken, startTime = System.currentTimeMillis();
		double time;

		for (int i = 0; i < searches.size(); i++)
		{
			// Creates new service configuration to start from scratch.
			sc = new CrossReferenceServiceConfiguration();
			int search = (int) Math.floor(i / 30);
			int path = (int) Math.floor(i / 6);
			int run = (i % 6) + 1;
			
			while (path > 4)
				path -= 5;
			
			String outputPath = outputDir[search] + input[path].substring(input[path].lastIndexOf("/") + 1) + "/" + run + "/";
			String resultsPath = resultsDir[search] + input[path].substring(input[path].lastIndexOf("/") + 1) + "/" + run + "/";

			// Initialise available refactorings. Needs to be done each 
			// time as the service configuration won't be updated otherwise.
			refactorings = new ArrayList<Refactoring>();
			DecreaseMethodVisibility dmv = new DecreaseMethodVisibility(sc);
			refactorings.add(dmv);
			DecreaseFieldVisibility dfv = new DecreaseFieldVisibility(sc);
			refactorings.add(dfv);
			IncreaseMethodVisibility imv = new IncreaseMethodVisibility(sc);
			refactorings.add(imv);
			IncreaseFieldVisibility ifv = new IncreaseFieldVisibility(sc);
			refactorings.add(ifv);
			MakeClassAbstract mca = new MakeClassAbstract(sc);
			refactorings.add(mca);
			MakeClassConcrete mcc = new MakeClassConcrete(sc);
			refactorings.add(mcc);
			MakeClassFinal mcf = new MakeClassFinal(sc);
			refactorings.add(mcf);
			MakeMethodFinal mmf = new MakeMethodFinal(sc);
			refactorings.add(mmf);
			MakeFieldFinal mff = new MakeFieldFinal(sc);
			refactorings.add(mff);
			MakeClassNonFinal mcnf = new MakeClassNonFinal(sc);
			refactorings.add(mcnf);
			MakeMethodNonFinal mmnf = new MakeMethodNonFinal(sc);
			refactorings.add(mmnf);
			MakeFieldNonFinal mfnf = new MakeFieldNonFinal(sc);
			refactorings.add(mfnf);
			MakeMethodStatic mms = new MakeMethodStatic(sc);
			refactorings.add(mms);
			MakeFieldStatic mfs = new MakeFieldStatic(sc);
			refactorings.add(mfs);
			MakeMethodNonStatic mmns = new MakeMethodNonStatic(sc);
			refactorings.add(mmns);
			MakeFieldNonStatic mfns = new MakeFieldNonStatic(sc);
			refactorings.add(mfns);
			MoveMethodUp mmu = new MoveMethodUp(sc);
			refactorings.add(mmu);
			MoveFieldUp mfu = new MoveFieldUp(sc);
			refactorings.add(mfu);
			MoveMethodDown mmd = new MoveMethodDown(sc);
			refactorings.add(mmd);	
			MoveFieldDown mfd = new MoveFieldDown(sc);
			refactorings.add(mfd);
			RemoveInterface ri = new RemoveInterface(sc);
			refactorings.add(ri);
			RemoveClass rc = new RemoveClass(sc);
			refactorings.add(rc);
			RemoveMethod rm = new RemoveMethod(sc);
			refactorings.add(rm);
			RemoveField rf = new RemoveField(sc);
			refactorings.add(rf);
			CollapseHierarchy ch = new CollapseHierarchy(sc);
			refactorings.add(ch);

			try 
			{
				// Read the original input.
				sc.getSourceFileRepository().getCompilationUnitsFromFiles(sourceFiles[path]);
			}
			catch (ParserException e) 
			{
				System.out.println("\r\nEXCEPTION: Cannot read input.");
				System.exit(1);
			}

			// Set up initial properties of service configuration.
			sc.getProjectSettings().setProperty(PropertyNames.INPUT_PATH, input[path] + super.readLibs(input[path]));
			sc.getProjectSettings().setProperty(PropertyNames.OUTPUT_PATH, outputPath);
			sc.getProjectSettings().ensureSystemClassesAreInPath();

			// initialise search task.			
			if (searches.get(i).getClass().getName().contains("MultiObjectiveSearch"))
				((MultiObjectiveSearch) searches.get(i)).setRefactorings(refactorings);
			else
				searches.get(i).setConfiguration(new Configuration(configurations[(int) Math.floor((i - 30)/30)], refactorings));

			searches.get(i).setServiceConfiguration(sc);
			searches.get(i).setResultsPath(resultsPath);
			searches.get(i).run();
			searches.set(i, null);

			// Output overall time taken to console.
			timeTaken = System.currentTimeMillis() - startTime;
			time = timeTaken / 1000.0;
			System.out.printf("\r\n\r\nTime taken to run part 4 so far: %.2fs", time);
			System.out.printf("\n\r-----------------------------------------------");
			System.out.printf("\n\r-----------------------------------------------");
		}	
	}
}
