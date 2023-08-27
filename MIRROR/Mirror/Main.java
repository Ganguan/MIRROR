package MIRROR;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import refactorings.Tasks;
import refactorings.ToolTasks;

public class Main {
	public static void main(String[] args)
	{
		System.out.printf("Automated Refactoring Tool Running");

		if (args.length > 0) 
		{
			if (args[0].equals("-f"))
			{
//				Tasks run = new Tasks(args[1]);
//				run.run();
				long start = System.currentTimeMillis(); 
				ToolTasks run=new ToolTasks();
				run.run();
				long end = System.currentTimeMillis(); 
		        System.out.println("程序总共运行时间：" + (end - start) + "ms");
			}
			else if (args[0].equals("-r"))
			{
				try 
				{
					BufferedReader br = new BufferedReader(new FileReader(args[1]));
//					Tasks run = new Tasks(br.readLine());
//					run.run();
					ToolTasks run=new ToolTasks(br.readLine());
					run.run();
					br.close();
				} 
				catch (IOException e) 
				{
					System.out.println("\r\nEXCEPTION: Cannot read source path from file.");
					System.exit(1);
				}
			}
			else
			{
				System.out.print("\r\n\r\nArgument not applicable. Input arguments must consist of one of the following:\r\n"
								  + " -f to pass in a directory containing the input\r\n"
								  + " -r to read in a file containing the input directory");
			}
		} 
		else 
		{
//			Tasks run = new Tasks();
//			run.run();
			ToolTasks run=new ToolTasks();
			run.run();
		}
		
		System.out.printf("\r\n\r\nFinished!");
	}

}
