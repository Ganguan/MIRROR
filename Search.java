package searches;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MIRROR.Configuration;
import MIRROR.FitnessFunction;
import MIRROR.Metrics;
import recoder.CrossReferenceServiceConfiguration;
import recoder.io.PropertyNames;
import recoder.io.SourceFileRepository;
import recoder.java.CompilationUnit;
import refactorings.Refactoring;

public abstract  class Search {
	protected CrossReferenceServiceConfiguration sc;
	protected Configuration c;
	protected Metrics m;
	protected String resultsPath;
	protected ArrayList<String> refactoringInfo;
	
	public Search(CrossReferenceServiceConfiguration sc, Configuration c) 
	{
		this.sc = sc;
		this.c = c;
		String output = sc.getProjectSettings().getProperty(PropertyNames.OUTPUT_PATH);
//		./results/
//		this.resultsPath = "C:/Users/jmh/Desktop/results/" + output.substring(output.lastIndexOf("/") + 1) + "/";
		this.resultsPath = "./results/" + output.substring(output.lastIndexOf("/") + 1) + "/";
	}
	
	public Search(CrossReferenceServiceConfiguration sc) 
	{
		this.sc = sc;
		String output = sc.getProjectSettings().getProperty(PropertyNames.OUTPUT_PATH);
		this.resultsPath = "./results/" + output.substring(output.lastIndexOf("/") + 1) + "/";
	}
	
	public abstract void run();

//	protected void print(SourceFileRepository sfr)
//	{
//		List<CompilationUnit> list = sfr.getKnownCompilationUnits();
//
//		for (CompilationUnit cu : list)
//		{	
//			try 
//			{
//				sfr.print(cu);
//			} 
//			catch (IOException e) 
//			{
//				System.out.println("ggggggggggggggggggg");
//				System.out.println("\r\nEXCEPTION: Cannot print changes to output.");
//				System.exit(1);
//			}
//		}
//	}
	
	// Finds a random available refactoring in the specified model and passes back the
	// refactoring used and the position of the applicable program element in the model.
	//��ָ��ģ���в���������õ��ع�����������ʹ�õ��ع��Լ�ģ�������ó���Ԫ�ص�λ�á�
	public int[] randomRefactoring(ArrayList<Refactoring> refactorings)
	{
		int[] position = new int[2];
		int r = -1;

		// Find element to refactor.
		if (refactorings.size() > 0)
		{
			r = (int)(Math.random() * refactorings.size());
			position = randomElement(refactorings.get(r));
		}
		else
		{
			position[0] = -1;
			position[1] = -1;
		}

		// Checks in case no elements are returned for the refactoring.
		// Will check again for each available refactoring in the search and 
		// if there are still no applicable elements returned the search will terminate.
		//����Ƿ�û��Ϊ�ع�����Ԫ�ء����ٴμ�������е�ÿ�������ع��������Ȼû�з������õ�Ԫ�أ���������ֹ��
		if ((position[0] == -1) && (position[1] == -1))
		{
			int exclude = r;
			for (r = 0; r < refactorings.size(); r++)
			{
				// Stops the loop from repeating the check for the previous refactoring.ֹͣѭ������һ���ع��ظ���顣
				if ((r == exclude) && ((r + 1) < refactorings.size()))
					r++;
				else if (r == exclude)
					break;

				position = randomElement(refactorings.get(r));

				if ((position[0] != -1) && (position[1] != -1))
					break;
			}

			if ((position[0] == -1) && (position[1] == -1))
				r = -1;
		}

		return new int[]{r, position[0], position[1]};
	}
	
	public int[] randomElement(Refactoring r)
	{
		int[] position = new int[2];
		position[0] = (int) (Math.random() * r.getServiceConfiguration().getSourceFileRepository().getKnownCompilationUnits().size());
	
		// Only counts the relevant program elements.ֻ������صĳ���Ԫ�ء�
		int amount = r.getAmount(position[0]);
		
		// Count the amount of available elements in the chosen class for refactoring.
		// If none are available find the next element up that is available.
		// If there are still no elements found look in the other direction and if there 
		// are no elements available at all return -1 so it can be handled in the search.
		//������ѡ���������ع��Ŀ���Ԫ�ص����������û�п���Ԫ�أ��������һ������Ԫ�ء�
		//�����Ȼû���ҵ�Ԫ�أ��볯��һ�����򿴣��������û�п��õ�Ԫ�أ��뷵��-1���Ա��������д���
		if (amount == 0)
		{
			int[] temp = new int[2];
			
			if (Math.random() >= 0.5)
			{
				temp = nextElementUp(position[0], r);

				if ((temp[0] == -1) && (temp[1] == -1))
					temp = nextElementDown(position[0], r);
			}
			else
			{
				temp = nextElementDown(position[0], r);
				
				if ((temp[0] == -1) && (temp[1] == -1))
					temp = nextElementUp(position[0], r);	
			}
				
			position = temp;
		}
		else
			// Random element chosen from the available amount.�ӿ���������ѡ������Ԫ�ء�
			position[1] = (int) (Math.random() * (amount-1)) + 1;
		
		// Finds the element position as an absolute 
		// position among elements of that type in the unit.��Ԫ��λ����Ϊ��Ԫ�и�����Ԫ�صľ���λ�ò��ҡ�
		if ((position[0] != -1) && (position[1] != -1))
			position[1] = r.getAbsolutePosition(position[0],  position[1]);
		
		return position;
	}
	
	private int[] nextElementUp(int currentUnit, Refactoring r) 
	{
		int[] position = new int[2];
		
		if (currentUnit == (r.getServiceConfiguration().getSourceFileRepository().getKnownCompilationUnits().size() - 1))
		{
			position[0] = -1;
			position[1] = -1;
		}
		else
		{				
			currentUnit++;
			int amount = 0;

			while (amount == 0)
			{
				if (currentUnit == r.getServiceConfiguration().getSourceFileRepository().getKnownCompilationUnits().size())
				{
					position[0] = -1;
					position[1] = -1;
					amount = -1;
				}
				// Only counts the relevant program element.
				else
					amount = r.getAmount(currentUnit);

				if (amount == 0)
					currentUnit++;
			}

			if (amount != -1)
			{
				position[0] = currentUnit;
				position[1] = 1;
			}
		}

		return position;
	}
	
	private int[] nextElementDown(int currentUnit, Refactoring r) 
	{
		int[] position = new int[2];
		
		if (currentUnit == 0)
		{
			position[0] = -1;
			position[1] = -1;
		}
		else
		{
			currentUnit--;
			int amount = 0;

			while (amount == 0)
			{
				if (currentUnit < 0)
				{
					position[0] = -1;
					position[1] = -1;
					amount = -1;
				}
				// Only counts the relevant program element.
				else
					amount = r.getAmount(currentUnit);

				if (amount == 0)
					currentUnit--;
			}

			if (amount != -1)
			{
				position[0] = currentUnit;
				position[1] = amount;
			}
		}

		return position;
	}
	
	// Output search information to results file.��������Ϣ���������ļ���
	protected void outputSearchInfo(String pathName, String runInfo)
	{
		String runName = String.format("%sresults.txt", pathName);
		File dir = new File(pathName);
		if (!dir.exists()) 
			dir.mkdirs();

		try 
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(runName, false));
			bw.write(String.format("======== Search Information ========"));
			bw.write(String.format("\r\n%s", runInfo));
			bw.close();
		}
		catch (IOException e) 
		{
			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa");
			System.out.println("\r\nEXCEPTION: Cannot export results to text file.");
			System.exit(1);
		}
	}
			
	// Output refactoring information to results file.���ع���Ϣ���������ļ���
	protected void outputRefactoringInfo(String pathName, double time, double qualityGain)
	{
		String runName = String.format("%sresults.txt", pathName);
		File dir = new File(pathName);
		if (!dir.exists()) 
			dir.mkdirs();

		try 
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(runName, true));
			bw.append("\r\n\r\n>>Refactorings Information:");
//			System.out.println("\r\n\r\n======== Applied Refactorings ========");

			for (int i = 0; i < this.refactoringInfo.size(); i++) {
				bw.append(String.format("\r\n%s", this.refactoringInfo.get(i)));
//				System.out.println(String.format("\r\n%s", this.refactoringInfo.get(i)));
			}

//			bw.append(String.format("\r\n\r\nScore has improved overall by %f", qualityGain));
			bw.append(String.format("\r\nTime taken to refactor: %.2fs", time));
//			System.out.println(String.format("\r\nTime taken to refactor: %.2fs", time));

			bw.close();
		}
		catch (IOException e) 
		{
			System.out.println("bbbbbbbbbbbbbbbbbbbbbbb");
			System.out.println("\r\nEXCEPTION: Cannot export results to text file.");
			System.exit(1);
		}
	}
	
	// Outputs the metric values for the project.�����Ŀ�Ķ���ֵ��
	protected void outputMetrics(float score, boolean initial, boolean log, String pathName)
	{
		FitnessFunction ff = new FitnessFunction(this.c.getConfiguration());
		GeneticAlgorithmSearch.setAdditionalInfo(ff, this.c);		
		String[] outputs = ff.createOutput(this.m);
		
		// Create a location for the results output.Ϊ����������һ��λ�á�
		String runName = String.format("%sresults.txt", pathName);
		File dir = new File(pathName);
		if (!dir.exists()) 
			dir.mkdirs();

		try 
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(runName, true));
			
			if (initial)
				bw.append(String.format("\r\n\r\n======== Initial Metric Info ========"));
			else
				bw.append(String.format("\r\n\r\n======== Final Metric Info ========"));
			
			// Outputs the metric values for the project to a text file.����Ŀ�Ķ���ֵ������ı��ļ���
			for (int i = 0; i < outputs.length; i++)
				bw.append("\r\n" + outputs[i]);
			
			bw.append(String.format("\r\nOverall score: %f", score));
			System.out.println("Overall score"+score);
			bw.close();
		}
		catch (IOException e) 
		{
			System.out.println("ccccccccccccccccccccccccccccccc");
			System.out.println("\r\nEXCEPTION: Cannot export results to text file.");
			System.exit(1);
		}

		if (log)
		{
			// Outputs the metric values for the project to the console for immediate feedback.����Ŀ�Ķ���ֵ���������̨���Ա�����������
			System.out.printf("\r\n");
			
			for (int i = 0; i < outputs.length; i++)
			{
				if(outputs[i].charAt(outputs[i].length() - 7) == '.')
					outputs[i] = outputs[i].substring(0, outputs[i].length() - 4);
				
				System.out.printf("\r\n%s", outputs[i]);
			}
			
			System.out.printf("\r\nOverall score: %.2f", score);
		}
	}
		
	public String getResultsPath()
	{
		return this.resultsPath;
	}
	
	public void setResultsPath(String resultsPath)
	{
		this.resultsPath = resultsPath;
	}
		
	public void setServiceConfiguration(CrossReferenceServiceConfiguration sc)
	{
		this.sc = sc;
	}
	
	public void setConfiguration(Configuration c)
	{
		this.c = c;
	}

}
