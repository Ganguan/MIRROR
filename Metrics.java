package MIRROR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import recoder.abstraction.ClassType;
import recoder.abstraction.Method;
import recoder.abstraction.Type;
import recoder.convenience.AbstractTreeWalker;
import recoder.convenience.ForestWalker;
import recoder.convenience.TreeWalker;
import recoder.java.CompilationUnit;
import recoder.java.ProgramElement;
import recoder.java.declaration.ClassDeclaration;
import recoder.java.declaration.FieldDeclaration;
import recoder.java.declaration.FieldSpecification;
import recoder.java.declaration.InterfaceDeclaration;
import recoder.java.declaration.MemberDeclaration;
import recoder.java.declaration.MethodDeclaration;
import recoder.java.declaration.TypeDeclaration;
import recoder.java.declaration.VariableDeclaration;
import recoder.java.declaration.modifier.Private;
import recoder.java.declaration.modifier.Protected;
import recoder.java.declaration.modifier.Public;
import recoder.java.declaration.modifier.VisibilityModifier;
import recoder.java.reference.MethodReference;
import recoder.java.reference.TypeReference;
import recoder.kit.MethodKit;
import recoder.kit.MiscKit;
import recoder.service.CrossReferenceSourceInfo;
import recoder.service.SourceInfo;
import refactorings.Refactoring;
import searches.Search;
public class Metrics {
	private List<CompilationUnit> units;
	private ArrayList<String> affectedClasses;
	private HashMap<String, Integer> elementDiversity;
	private HashMap<String, Integer> elementScores;
	private ArrayList<Refactoring> refactorings;
	private Refactoring r;
	
	public Metrics(List<CompilationUnit> units)
	{
		this.units = units;
		this.elementScores = new HashMap<String, Integer>();
	}
	
	// Amount of classes in the project.
	// Includes both ordinary classes and interfaces.项目中的类数量包括普通类和接口。
	public int classDesignSize()
	{
		int classCounter = 0; 
		ForestWalker tw = new ForestWalker(this.units);

		while (tw.next(TypeDeclaration.class))
		{
			TypeDeclaration td = (TypeDeclaration) tw.getProgramElement();
			if ((td.getName() != null) && ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration)))
				classCounter++;
		}
		
		return classCounter;
	}
	
	// Amount of distinct class hierarchies in the project.
	// Excludes classes from external libraries.项目中不同类层次结构的数量。
	public int numberOfHierarchies()
	{
		SourceInfo si = this.units.get(0).getFactory().getServiceConfiguration().getSourceInfo();
		Set<String> baseTypes = new HashSet<String>();

		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if (td.isOrdinaryClass())
				{
					// Prevents "Zero Service" outputs logged to the console.
					if (td.getProgramModelInfo() == null)
						td.getFactory().getServiceConfiguration().getChangeHistory().updateModel();
					
					if (!(td.getSupertypes().get(0) instanceof TypeDeclaration) && (si.getSubtypes(td).size() > 0))
						baseTypes.add(td.getFullName());
				}
			}
		}
		
		return baseTypes.size();
	}
	
	// Average amount of classes away from the root per class.
	// Excludes classes from external libraries.每个类离开根的平均类数。
	public float averageNumberOfAncestors()
	{
		int classCounter = 0;
		int superTypeCounter = 0;

		for (int i = 0; i < this.units.size(); i++)
		{
			for (ClassType ct : getAllTypes(this.units.get(i)))
			{
				if (ct.isOrdinaryClass())
				{				
					// Prevents "Zero Service" outputs logged to the console.
					if (ct.getProgramModelInfo() == null)
						((ClassDeclaration) ct).getFactory().getServiceConfiguration().getChangeHistory().updateModel();

					classCounter++;
					while (ct.getSupertypes().get(0) instanceof TypeDeclaration)
					{
						superTypeCounter++;
						ct = ct.getSupertypes().get(0);
					}
				}
			}
		}

		return (float) superTypeCounter / (float) classCounter;
	}
	
	// Average ratio of the amount of private, package or 
	// protected attributes in a class to the overall amount per class.
	public float dataAccessMetric()
	{
		int counter, nonPublicCounter;
		int classCounter = 0;
		float dataAccessMetric = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					counter = 0;
					nonPublicCounter = 0;
					classCounter++;
					
					for (MemberDeclaration md : td.getMembers())
					{
						if (md instanceof FieldDeclaration)
						{	
							counter++;
							if (!(((FieldDeclaration) md).getVisibilityModifier() instanceof Public))
								nonPublicCounter++;
						}
					}
					
					if (counter > 0)
						dataAccessMetric += (float) nonPublicCounter / (float) counter;
				}
			}
		}
		
		return dataAccessMetric / (float) classCounter;
	}
	
	// Average number of other distinct classes each class depends on per class.
	// Only includes user defined classes from the project.
	public float directClassCoupling()
	{
		int couplingCounter = 0;
		int classCounter = 0;
		Set<String> distinctTypes;
		SourceInfo si = this.units.get(0).getFactory().getServiceConfiguration().getSourceInfo();

		for (int i = 0; i < this.units.size(); i++)
		{			
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					distinctTypes = new HashSet<String>();
					
					// Prevents "Zero Service" outputs logged to the console.
					if (td.getProgramModelInfo() == null)
						td.getFactory().getServiceConfiguration().getChangeHistory().updateModel();

					for (MemberDeclaration md : td.getMembers())
						if (md instanceof MethodDeclaration)
							for (Type t : ((MethodDeclaration) md).getSignature())
								if ((t != null) && ((t instanceof ClassDeclaration) || (t instanceof InterfaceDeclaration)))
									distinctTypes.add(t.getFullName());

					for (FieldSpecification fs : td.getFieldsInScope())
					{
						TreeWalker tw = new TreeWalker(fs);
						while (tw.next(TypeReference.class)) 
						{
							Type t = si.getType(tw.getProgramElement());

							if ((t != null) && ((t instanceof ClassDeclaration) || (t instanceof InterfaceDeclaration)))
								distinctTypes.add(t.getFullName()); 
						}
					}

					couplingCounter += distinctTypes.size();	
				}
			}
		}

		return (float) couplingCounter / (float) classCounter;
	}

	// Average cohesion among methods ratio per class.
	// Ratio gets the accumulation of the amount of distinct parameter types for each method
	// over the maximum possible amount of distinct parameter types across all the methods.
	// Denominator is calculated by multiplying the amount of methods by the amount of
	// distinct parameter types in all of the methods of the class.
	public float cohesionAmongMethods()
	{
		int methodCounter, cohesionCounter;
		int classCounter = 0;
		float cohesionAmongMethods = 0;

		ArrayList<String> types;
		ArrayList<String> allTypes;
		Set<String> distinctTypes;

		for (int i = 0; i < this.units.size(); i++)
		{		
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					methodCounter = 0;
					cohesionCounter = 0;
					allTypes = new ArrayList<String>();
				
					// Prevents "Zero Service" outputs logged to the console.
					if (td.getProgramModelInfo() == null)
						td.getFactory().getServiceConfiguration().getChangeHistory().updateModel();
					
					for (MemberDeclaration md : td.getMembers())
					{
						if (md instanceof MethodDeclaration)
						{
							methodCounter++;
							types = new ArrayList<String>();
							
							for (Type t : ((MethodDeclaration) md).getSignature())
							{
								types.add(t.getFullName());
								allTypes.add(t.getFullName());
							}

							distinctTypes = new HashSet<String>(types);
							cohesionCounter += distinctTypes.size();
						}
					}

					distinctTypes = new HashSet<String>(allTypes);

					if ((methodCounter * distinctTypes.size()) > 0)
						cohesionAmongMethods += (float) cohesionCounter / (float) (methodCounter * distinctTypes.size());
				}
			}
		}

		return cohesionAmongMethods / (float) classCounter;
	}

	// Average amount of user defined attributes declared per class.
	// Only counts classes defined in the project.
	public float aggregation()
	{
		int counter = 0;
		int classCounter = 0;
		SourceInfo si = this.units.get(0).getFactory().getServiceConfiguration().getSourceInfo();
		
		for (int i = 0; i < this.units.size(); i++)
		{			
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					for (FieldSpecification f : td.getFieldsInScope())
					{
						Type t = si.getType(f);	
						if ((t != null) && ((t instanceof ClassDeclaration) || (t instanceof InterfaceDeclaration)))
							counter++;
					}
				}
			}
		}

		return (float) counter / (float) classCounter;
	}
	
	// Average functional abstraction ratio per class.
	// Ratio gets the amount of inherited methods accessible within a class
	// (methods declared in a super class of the current class that are public
	// or protected, or are package and contain the same package) over the overall
	// amount of methods accessible (inherited and declared within the class) to the class.
	// Excludes methods inherited from external library classes.
	public float functionalAbstraction()
	{
		int counter, inheritedCounter;
		int classCounter = 0;
		float functionalAbstraction = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					inheritedCounter = 0;
					counter = 0;
					TypeDeclaration superType = td;
					
					for (MemberDeclaration md : td.getMembers())
						if (md instanceof MethodDeclaration)
							counter++;
					
					// Prevents "Zero Service" outputs logged to the console.
					if (td.getProgramModelInfo() == null)
						td.getFactory().getServiceConfiguration().getChangeHistory().updateModel();
					
					while (superType.getSupertypes().get(0) instanceof TypeDeclaration)
					{
						superType = (TypeDeclaration) superType.getSupertypes().get(0);
						
						for (MemberDeclaration md : td.getMembers())
							if (md instanceof MethodDeclaration)
								if ((md.isPublic()) || (md.isProtected()) || (!(md.isPrivate()) && (td.getPackage().equals(superType.getPackage()))))
									inheritedCounter++;
					}
					
					counter += inheritedCounter;
					
					if (counter > 0)
						functionalAbstraction += (float) inheritedCounter / (float) counter;
				}
			}
		}
		
		return functionalAbstraction / (float) classCounter;
	}
	
	// Average amount of polymorphic methods 
	// (methods that are redefined/overwritten) per class.
	// Abstract method declarations and constructors are included.
	public float numberOfPolymorphicMethods()
	{
		int counter = 0;
		int classCounter = 0;
		SourceInfo si = this.units.get(0).getFactory().getServiceConfiguration().getSourceInfo();
		
		for (int i = 0; i < this.units.size(); i++)
		{			
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					// Prevents "Zero Service" outputs logged to the console.
					if (td.getProgramModelInfo() == null)
						td.getFactory().getServiceConfiguration().getChangeHistory().updateModel();
					
					classCounter++;
					for (MemberDeclaration md : td.getMembers())
						if (md instanceof MethodDeclaration)							
							if (MethodKit.getRedefiningMethods((CrossReferenceSourceInfo) si, (Method) md).size() > 0)
								counter++;
				}
			}
		}
		
		return (float) counter / (float) classCounter;
	}
	
	// Average amount of public methods per class.
	public float classInterfaceSize()
	{
		int counter = 0;
		int classCounter = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					for (MemberDeclaration md : td.getMembers())
						if ((md instanceof MethodDeclaration) && (((MethodDeclaration) md).getVisibilityModifier() instanceof Public))
							counter++;
				}
			}
		}

		return (float) counter / (float) classCounter;
	}
	
	// Average amount of methods per class.
	public float numberOfMethods()
	{
		int classCounter = 0;
		int methodCounter = 0;

		for (int i = 0; i < this.units.size(); i++)
		{			
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					for (MemberDeclaration md : td.getMembers())
						if (md instanceof MethodDeclaration)
							methodCounter++;
				}
			}
		}

		return (float) methodCounter / (float) classCounter;
	}
	
	public double REU() {
		return  (-0.25*directClassCoupling()+0.25*cohesionAmongMethods()+0.5*classInterfaceSize()+0.5*classDesignSize());
	}
	public double a;
	public double b;
	public double c;
	public double UND() {
		int i=1;
		if(i==1) {
			a=(-0.33*averageNumberOfAncestors()+0.33*dataAccessMetric()-0.33*directClassCoupling()+0.33*cohesionAmongMethods()-0.33*numberOfPolymorphicMethods()-0.33*numberOfMethods()-0.33*classDesignSize());
		    i++;
		    return a;
		}
		return (-0.33*averageNumberOfAncestors()+0.33*dataAccessMetric()-0.33*directClassCoupling()+0.33*cohesionAmongMethods()-0.33*numberOfPolymorphicMethods()-0.33*numberOfMethods()-0.33*classDesignSize());
	}
	
	public double EXT() {
		int j=1;
		if(j==1) {
			b=(0.5*averageNumberOfAncestors()-0.5*directClassCoupling()+0.5*functionalAbstraction()+0.5*numberOfPolymorphicMethods());
		    j++;
		    return b;
		}
		return (0.5*averageNumberOfAncestors()-0.5*directClassCoupling()+0.5*functionalAbstraction()+0.5*numberOfPolymorphicMethods());
	}
	
	public double EFE() {
		int k=1;
		if(k==1) {
			c=(0.2*averageNumberOfAncestors()+0.2*dataAccessMetric()+0.2*aggregation()+0.2*functionalAbstraction()+0.2*numberOfPolymorphicMethods());
			k++;
			return c;
		}
		return (0.2*averageNumberOfAncestors()+0.2*dataAccessMetric()+0.2*aggregation()+0.2*functionalAbstraction()+0.2*numberOfPolymorphicMethods());
	}
	public double quality() {
		return UND()+EFE()+EXT()-qualityOriginal()/qualityOriginal();
	}
	private double qualityOriginal() {
		// TODO Auto-generated method stub
		
		return a+b+c;
	}

	//codesmells function
	public double codesmells() throws BiffException, IOException {
		return num()/numoriginal();
	}
	//history
	public double history() {
		return scoreratio();
	}
	public double scoreratio() {
		//鍒涘缓宸ヤ綔钖�+杞藉叆宸ヤ綔娴�
		//ArrayList<String> columnList=new ArrayList<String>();
		//Workbook readwb=null;
				jxl.Workbook wb = null;
				InputStream is = null;
				try {
					is = new FileInputStream("C:\\Users\\gk152\\Desktop\\ObjectSet\\historyset.xls");
				} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					wb = Workbook.getWorkbook(is);
				} catch (BiffException e) {
		// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
		// TODO Auto-generated catch block
					e.printStackTrace();
				}

				int sheetSize = wb.getNumberOfSheets();
				Sheet[] sheet = wb.getSheets();
		//now閲嶆瀯绫诲瀷璇诲彇
				jxl.Workbook wbs = null;
				InputStream is2 = null;
				try {
					is2 = new FileInputStream("C:\\Users\\gk152\\Desktop\\ObjectSet\\Defects.xls");
				} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					wbs = Workbook.getWorkbook(is2);
				} catch (BiffException e) {
		// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
		// TODO Auto-generated catch block
					e.printStackTrace();
				}
		//int sheetSize2=wbs.getNumberOfSheets();
				Sheet[] sheet2 = wbs.getSheets();

				double score = 0;
				int k = 0;

				for (int i = 0; i < sheetSize; i++) {
		//鍒濆鍖杝heet椤电殑璁℃暟鍙傛暟鍊�
					score = 0;
					k = 0;
					int rownum = sheet[i].getRows();
					for (int j = 1; j < rownum; j++) {
						Cell[] cells = sheet[i].getRow(j);// 绗琷琛岀殑鍗曞厓鏍�
		//鑾峰彇鍘嗗彶閲嶆瀯绫诲瀷
						String RefactorName, RefactorNamePosition;
		//濡傛灉娌℃湁绗笁鍒� 鍒欒祴鍊糝efactorName涓虹┖
						if (cells.length < 3) {
							RefactorName = "";
						} else {
							RefactorName = (cells[2].getContents());
						}
						if (cells.length < 6) {
							RefactorNamePosition = "";
						} else {
							RefactorNamePosition = (cells[5].getContents());
						}
		//鑾峰彇now閲嶆瀯绫诲瀷

						int rownum2 = sheet2[i].getRows();
						for (int c = 0; c < rownum2; c++) {
							Cell[] cells2 = sheet2[i].getRow(c);
							String RefactorType, RefactorTypePosition;
		//濡傛灉娌℃湁绗笁鍒楋紝鍒橰efactorType璧嬪�间负绌�
							if (cells2.length < 3) {
								RefactorType = "";
							} else {
								if (cells2[2].getContents() != "") {

									RefactorType = cells2[2].getContents();

									RefactorTypePosition = cells2[0].getContents();

		//涓庡巻鍙茬浉浼艰绠�
									if (RefactorType == RefactorName) {
										k++;
		//閲嶆瀯浣嶇疆鐩稿悓
		//if(RefactorType.getposition()==RefactorName.getposition()) {
										if (RefactorNamePosition.contains(RefactorTypePosition)) {
											score += 2;
										} else {
											score++;
										}

									}
								}
							}

						}
					}

				}
				double percentage;
				if (k == 0) {
					percentage = 0;
					return percentage;
				}
				percentage = score / k * 2;
				return percentage;			
	}
	
	public int num() throws BiffException, IOException {
		int num=0;
		int row=readsheetrow(new File("C:\\Users\\gk152\\Desktop\\ObjectSet\\Defects.xls"));
		File file=new File("C:\\Users\\gk152\\Desktop\\ObjectSet\\Defects.xls");
		Workbook wb=Workbook.getWorkbook(file);
		Sheet sheet =wb.getSheet("sheet1");
//		new Search().randomRefactoring(refactorings);
//		new Search().randomElement(r);
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				for(int j=0;j< row;j++) {
					Cell cell=sheet.getCell(0,j);
					if((String)td.getName()==cell.getContents())
						num++;
					}
				}
			 }
		return num;
		}


	private int readsheetrow(File file) throws BiffException, IOException {
		// TODO Auto-generated method stub
		Workbook read = null;
		InputStream io = new FileInputStream(file.getAbsoluteFile());
		read = Workbook.getWorkbook(io);
		Sheet[] readsheet = read.getSheets();
		int row = readsheet[0].getRows(); 
		return row;
	}

	public int numoriginal() {
		int numoriginal=0;
		//需要改动的
//		String name="Gantt";
//		String name="ApacheAnt";
//		String name="Xerces-J";
//		String name="JFreeChart";
//		String name="Rihno";
//		String name="junit";
		String name="mango";
//		String name="mybatis";
//		String name="jhotdraw";
		try {
			List<Integer> a=new ArrayList<Integer>();
			a = readSpecifyColumns(new File("C:\\Users\\gk152\\Desktop\\ObjectSet\\Defects.xls"));
			Workbook wb = Workbook.getWorkbook(new File("C:\\Users\\gk152\\Desktop\\ObjectSet\\Defects.xls"));
			for (int i = 0; i < a.size(); i++) {
				Sheet sheet =wb.getSheet(i);
				if(sheet.getName()==name) {
					numoriginal = a.get(i);
				}else {
					
				}
				
			}
			copy_excel(new File("C:\\Users\\gk152\\Desktop\\ObjectSet\\Defects.xls"));
		}catch(Exception e) {
			e.printStackTrace();
		}		
		return numoriginal;
	}
	
	public static void copy_excel(File file) throws Exception {
		FileWriter fWriter = null;
		PrintWriter out = null;
		String fliename = file.getName().replace(".xls", "");
		fWriter = new FileWriter(file.getParent() + "/agetwo.xls");// 杈撳嚭鏍煎紡涓�.xls
		fWriter = new FileWriter(file.getParent() + "/" + fliename + ".txt");// 杈撳嚭鏍煎紡涓�.txt
		out = new PrintWriter(fWriter);
		InputStream is = new FileInputStream(file.getAbsoluteFile());
		Workbook wb = null;
		wb = Workbook.getWorkbook(is);
		int sheet_size = wb.getNumberOfSheets();
//Sheet sheet = wb.getSheet(0);
		Sheet[] sheet = wb.getSheets();
		for (int i = 0; i < sheet_size; i++) {
			for (int j = 1; j < sheet[i].getRows(); j++) {
				String cellinfo = sheet[i].getCell(0, j).getContents();// 璇诲彇鐨勬槸绗簩鍒楁暟鎹紝娌℃湁鏍囬锛屾爣棰樿捣濮嬩綅缃湪for寰幆涓畾涔�
				out.println(cellinfo);
			}

		}
		/*
		 * for (int j = 1; j < sheet.getRows(); j++) {鍘熸湰娉ㄩ噴 String cellinfo =
		 * sheet.getCell(0, j).getContents();//璇诲彇鐨勬槸绗簩鍒楁暟鎹紝娌℃湁鏍囬锛屾爣棰樿捣濮嬩綅缃湪for寰幆涓畾涔�
		 * out.println(cellinfo); }
		 */
		out.close();// 鍏抽棴娴�
		fWriter.close();
		out.flush();// 鍒锋柊缂撳瓨
//System.out.println("杈撳嚭瀹屾垚锛�");
	}
	
	public static List<Integer> readSpecifyColumns(File file) throws Exception {
		ArrayList<String> columnList = new ArrayList<String>();
		List<Integer> numList = new ArrayList<Integer>();
		Workbook readwb = null;
		InputStream io = new FileInputStream(file.getAbsoluteFile());
		readwb = Workbook.getWorkbook(io);
		int readsheet_size = readwb.getNumberOfSheets();
//Sheet readsheet = readwb.getSheet(0);
		Sheet[] readsheet = readwb.getSheets();
//int[] numList=new int[5];
		for (int k = 0; k < readsheet_size; k++) {
			int num = 0;
//int[] num={0,0,0,0,0};
			int rsColumns = readsheet[k].getColumns(); // lie
			int rsRows = readsheet[k].getRows(); // hang
			for (int i = 0; i < rsRows; i++) {
				Cell cell_name = readsheet[k].getCell(1, i); // 绗�2鍒楃殑鍊�
				if (cell_name.getContents() != "") {
					Cell cell_name1 = readsheet[k].getCell(0, i);
					columnList.add(cell_name1.getContents());
//columnList.add(cell_name.getContents());
//System.out.println(cell_name.getContents());
					int m = Integer.parseInt(cell_name.getContents());
					num = num + m;

				} else {

				}

			}

//System.out.println(columnList);
//System.out.println(num);
			numList.add(num);

		}

		return numList;
	}
	// Average amount of complexity of all methods per class.
	// The complexity is calculated using the amount of lines of code per method.
	public float weightedMethodsPerClass()
	{
		int classCounter = 0;
		int methodCounter = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					for (MemberDeclaration md : td.getMembers())
						if (md instanceof MethodDeclaration)
							methodCounter += (md.getEndPosition().getLine() - md.getStartPosition().getLine() + 1);
				}
			}
		}
		
		return (float) methodCounter / (float) classCounter;
	}
	
	// Average amount of direct child classes per class.
	// Only includes ordinary classes within the project.
	public float numberOfChildren()
	{		
		int childCounter = 0;
		int classCounter = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{				
				if (td.isOrdinaryClass())
				{					
					// Prevents "Zero Service" outputs logged to the console.
					if (td.getProgramModelInfo() == null)
						td.getFactory().getServiceConfiguration().getChangeHistory().updateModel();

					classCounter++;
					if (td.getSupertypes().get(0) instanceof TypeDeclaration)
						childCounter++;
				}
			}
		}

		return (float) childCounter / (float) classCounter;
	}
	
	
	// Ratio of the amount of interfaces over the overall amount of classes.
	public float abstractness()
	{
		int classCounter = 0;
		int interfaceCounter = 0;
		ForestWalker tw = new ForestWalker(this.units);

		while (tw.next(TypeDeclaration.class))
		{
			TypeDeclaration td = (TypeDeclaration) tw.getProgramElement();
			if ((td.getName() != null) && ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration)))
			{
				classCounter++;
				if (td instanceof InterfaceDeclaration)
					interfaceCounter++;
			}
		}
		
		float answer = (float) interfaceCounter / (float) classCounter;
		return answer;
	}
	
	// Average ratio of abstract elements over abstract
	// and potentially abstract elements per class.
	// Variables can't be abstract.
	public float abstractRatio()
	{
		int counter, abstractCounter;
		int classCounter = 0;
		float abstractAmount = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{				
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					counter = 1;
					abstractCounter = 0;
					classCounter++;
					
					if (td.isAbstract())
						abstractCounter++;
				
					for (MemberDeclaration md : td.getMembers())
					{
						if (md instanceof MethodDeclaration)
						{
							counter++;
							if (((MethodDeclaration) md).isAbstract())
								abstractCounter++;
						}
					}

					abstractAmount += (float) abstractCounter / (float) counter;
				}
			}
		}
		
		return abstractAmount / (float) classCounter;
	}

	// Average ratio of static elements over static
	// and potentially static elements per class.
	// Of the variable declarations, only a field can be static.
	public float staticRatio()
	{
		int counter, staticCounter;
		int classCounter = 0;
		float staticAmount = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{				
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					counter = 1;
				   	staticCounter = 0;
					classCounter++;
					
					if (td.isStatic())
						staticCounter++;
					
					for (MemberDeclaration md : td.getMembers())
					{
						if ((md instanceof MethodDeclaration) || (md instanceof FieldDeclaration))
						{
							counter++;
							if (md.isStatic())
								staticCounter++;
						}
					}
					
					staticAmount += (float) staticCounter / (float) counter;
				}
			}
		}

		return staticAmount / (float) classCounter;
	}
	
	// Average ratio of final elements over final
	// and potentially final elements per class.
	public float finalRatio()
	{
		int counter, finalCounter;
		int classCounter = 0;
		float finalAmount = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{				
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					counter = 1;
					finalCounter = 0;
					classCounter++;
					
					if (td.isFinal())
						finalCounter++;
					
					for (MemberDeclaration md : td.getMembers())
					{
						if (md instanceof MethodDeclaration)
						{
							counter++;
							if (((MethodDeclaration) md).isFinal())
								finalCounter++;
							
							TreeWalker tw = new TreeWalker(md);
							
							while (tw.next(VariableDeclaration.class))
							{
								counter++;
								VariableDeclaration vd = (VariableDeclaration)(tw.getProgramElement());
								if (vd.isFinal())
									finalCounter++;
							}
						}
						else if (md instanceof FieldDeclaration)
						{
							counter++;
							if (((FieldDeclaration) md).isFinal())
								finalCounter++;
						}
					}
					
					finalAmount += (float) finalCounter / (float) counter;
				}
			}
		}

		return finalAmount / (float) classCounter;
	}
	
	// Average ratio of constant elements over constant
	// and potentially constant elements per class.
	// Of the variable declarations, only a field can be constant.
	public float constantRatio()
	{
		int counter, constantCounter;
		int classCounter = 0;
		float constantAmount = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{				
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					counter = 1;
					constantCounter = 0;
					classCounter++;
					
					if ((td.isStatic()) && (td.isFinal()))
						constantCounter++;
					
					for (MemberDeclaration md : td.getMembers())
					{
						if (md instanceof MethodDeclaration)
						{
							counter++;
							if ((md.isStatic()) && (((MethodDeclaration) md).isFinal()))
								constantCounter++;
						}
						else if (md instanceof FieldDeclaration)
						{
							counter++;
							if ((md.isStatic()) && (((FieldDeclaration) md).isFinal()))
								constantCounter++;
						}
					}
					
					constantAmount += (float) constantCounter / (float) counter;
				}
			}
		}

		return constantAmount / (float) classCounter;
	}
	
	// Ratio of amount of classes in the project that are 
	// declared inside other classes over the amount of classes.
	public float innerClassRatio()
	{
		int innerClassCounter = 0;
		int classCounter = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{			
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{				
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					
					if (td.getContainingClassType() != null)
						innerClassCounter++;
				}
			}
		}
		
		return (float) innerClassCounter / (float) classCounter;
	}	
	
	// Average referenced inherited methods ratio per class.
	// Ratio gets the accumulation of the amount of inherited external methods accessed within
	// the methods of a class (methods declared in a super class of the current class) over
	// the overall distinct amount of external methods accessed in the methods of the class.
	public float referencedMethodsRatio()
	{
		int methodCount, inheritedMethodCount;
		int classCounter = 0;
		float referencedMethodsRatio = 0;

		ArrayList<MethodDeclaration> methods;
		SourceInfo si = this.units.get(0).getFactory().getServiceConfiguration().getSourceInfo();

		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					classCounter++;
					methods = new ArrayList<MethodDeclaration>();

					for (MemberDeclaration md : td.getMembers())
					{
						if (md instanceof MethodDeclaration)
						{							
							TreeWalker tw = new TreeWalker(md);
							while (tw.next(MethodReference.class)) 
							{
								ProgramElement pe = tw.getProgramElement();
								if (si.getMethod((MethodReference) pe) instanceof MethodDeclaration)
								{
									MethodDeclaration method = (MethodDeclaration) si.getMethod((MethodReference) pe);

									if (!(methods.contains(method)) && !(method.getContainingClassType().equals(td)))
										methods.add(method);
								}
							}
						}
					}

					methodCount = methods.size();
					inheritedMethodCount = 0;

					for (MethodDeclaration md : methods)
						if (td.getAllSupertypes().contains(md.getContainingClassType()))
							inheritedMethodCount++;

					if (methodCount > 0)
						referencedMethodsRatio += (float) inheritedMethodCount / (float) methodCount;
				}
			}
		}

		return referencedMethodsRatio / (float) classCounter;
	}
	
	// Average visibility ratio per class.
	// Ratio calculates the accumulative visibility value among 
	// type, method and variable declarations over the amount of 
	// declarations, where a higher value means more visibility.
	public float visibilityRatio()
	{
		int counter;
		float visibilityCounter;
		int classCounter = 0;
		float visibility = 0;
		
		for (int i = 0; i < this.units.size(); i++)
		{
			for (TypeDeclaration td : getAllTypes(this.units.get(i)))
			{				
				if ((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration))
				{
					counter = 1;
					classCounter++;
					
					visibilityCounter = identifier(td.getVisibilityModifier());
					
					for (MemberDeclaration md : td.getMembers())
					{
						if (md instanceof MethodDeclaration)
						{
							counter++;
							visibilityCounter += identifier(((MethodDeclaration) md).getVisibilityModifier());
						}
						else if (md instanceof FieldDeclaration)
						{
							counter++;
							visibilityCounter += identifier(((FieldDeclaration) md).getVisibilityModifier());
						}
					}
					
					visibility += (float) visibilityCounter / (float) counter;
				}
			}
		}

		return visibility / (float) classCounter;
	}
	
	// Amount of lines of code in the project.
	public int linesOfCode()
	{		
		int lineCounter = 0;

		for (CompilationUnit u : this.units)
			lineCounter += u.getEndPosition().getLine();

		return lineCounter;
	}

	// Amount of java source files in the project.
	public int numberOfFiles()
	{
		return this.units.size();
	}
	
	// Instances of priority classes (most important classes determined 
	// by the user) affected by the refactorings of a solution.
	public int priorityNotNormalised(ArrayList<String> priorityClasses)
	{		
		int priorityAmount = 0;

		for (String s1 : this.affectedClasses)
		{
			for (String s2 : priorityClasses)
			{
				if (s1.endsWith(s2))
				{
					priorityAmount++;
					break;
				}
			}
		}
		
		return priorityAmount;
	}
	
	// Instances of priority classes affected by the refactorings of a solution. This
	// override also incorporates a list of non priority classes (classes where
	// modifications are undesirable). The instances of non priority classes are also 
	// calculated and then taken away from the priority classes amount to give an overall value.
	public int priorityNotNormalised(ArrayList<String> priorityClasses, ArrayList<String> nonPriorityClasses)
	{				
		int nonPriorityAmount = 0;
		int priorityAmount = priorityNotNormalised(priorityClasses);

		for (String s1 : this.affectedClasses)
		{
			for (String s2 : nonPriorityClasses)
			{
				if (s1.endsWith(s2))
				{
					nonPriorityAmount++;
					break;
				}
			}
		}
		
		return priorityAmount - nonPriorityAmount;
	}
	
	// Priority objective updated to be normalised as a ratio between 0 and 1. The original
	// score is divided by the highest value it could be i.e. the overall amount of affected classes.
	public float priority(ArrayList<String> priorityClasses)
	{		
		int priorityAmount = 0;

		for (String s1 : this.affectedClasses)
		{
			for (String s2 : priorityClasses)
			{
				if (s1.endsWith(s2))
				{
					priorityAmount++;
					break;
				}
			}
		}
		
		return (float) priorityAmount / (float) this.affectedClasses.size();
	}
	
	// Objective updated to be normalised as a ratio between -1 and 1. 
	// The non priority score is normalised the same way as the priority
	// score. Then the non priority score is taken away from the priority score.
	public float priority(ArrayList<String> priorityClasses, ArrayList<String> nonPriorityClasses)
	{				
		float nonPriorityAmount = 0.0f;
		float priorityAmount = priority(priorityClasses);

		for (String s1 : this.affectedClasses)
		{
			for (String s2 : nonPriorityClasses)
			{
				if (s1.endsWith(s2))
				{
					nonPriorityAmount++;
					break;
				}
			}
		}
		
		nonPriorityAmount /= this.affectedClasses.size();
		return priorityAmount - nonPriorityAmount;
	}
	
	// Diversity of refactorings in refactoring solution. This is calculated by
	// finding the average amount of refactorings per refactored element, and then
	// dividing the amount of distinct refactored elements by this average. In the method
	// this calculation is a little more streamlined. Average = refactoring count / elements.
	// Therefore elements / average = elements * (elements / refactoring count).
	// The metric is calculated by finding elements squared over refactoring count.
	public float diversityNotNormalised()
	{
		int numerator = this.elementDiversity.size() * this.elementDiversity.size();
		int denominator = 0;
		
		for (Integer value : this.elementDiversity.values()) 
			denominator += value;
		
		return (float) numerator / (float) denominator;
	}
	
	// Diversity objective updated to be normalised as a ratio between 0 and 1.
	// The original score is divided by the highest value it could be i.e. the 
	// amount of distinct elements divided by 1. Again, this calculation is 
	// rearranged to improve efficiency. (elements / average) / elements = 
	// 1 / average => (1 / (refactoring count / elements)) => elements / refactoring count.
	public float diversity()
	{
		int numerator = 0;
		
		for (Integer value : this.elementDiversity.values()) 
			numerator += value;
		
		return (float) this.elementDiversity.size() / (float) numerator;
	}
	
	// Element recentness in refactoring solution. This is calculated by
	// finding how far back the element appeared amongst the previous versions of the code, 
	// denoted with an integer. The older the element is, the larger its corresponding value.
	// This value is calculated or extracted for each relevant element in the refactoring 
	// solution, and an accumulative value is calculated to give an overall measure of recentness.
	public int elementRecentnessNotNormalised(ArrayList<List<CompilationUnit>> previousUnits)
	{
		int numerator = 0;
		
		for (Entry<String, Integer> e : this.elementDiversity.entrySet())
		{			
			String key = e.getKey();
			int value = e.getValue();
			int amount = previousUnits.size();
			
			if (this.elementScores.containsKey(key))
			{
				amount = this.elementScores.get(key);			
			}
			else
			{
				String name;
				int elementType;
				
				if (!(key.contains(":")))
				{
					elementType = 1;
					name = key.substring(key.lastIndexOf('\\') + 1);
				}
				else if (key.charAt(1) == ':')
				{
					elementType = 3;
					name = key.substring(2);
				}
				else if (key.endsWith(":"))
				{
					elementType = 2;
					name = key.substring(1, key.length() - 1);
				}
				else
				{
					elementType = 4;
					name = key.substring(key.lastIndexOf(':') + 1);
				}
				
				for (int i = previousUnits.size() - 1; i >= 0; i--)
				{
					ForestWalker tw = new ForestWalker(previousUnits.get(i));
					boolean breakout = true;
					
					if (elementType == 1)
					{
						while (tw.next(TypeDeclaration.class))
						{
							TypeDeclaration td = (TypeDeclaration) tw.getProgramElement();
							if (((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration)) && 
								(td.getName() != null) && (td.getName().equals(name)))
							{
								breakout = false;
								break;
							}
						}
					}
					else if (elementType == 2)
					{
						while (tw.next(MethodDeclaration.class))
						{
							MethodDeclaration md = (MethodDeclaration) tw.getProgramElement();
							if ((md.getName() != null) && (Refactoring.getMethodName(md).equals(name)))
							{
								breakout = false;
								break;
							}
						}
					}
					else if (elementType == 3)
					{
						while (tw.next(FieldDeclaration.class))
						{
							FieldDeclaration fd = (FieldDeclaration) tw.getProgramElement();
							if ((fd.toString() != null) && (fd.toString().equals(name)))
							{
								breakout = false;
								break;
							}
						}
					}	
					else if (elementType == 4)
					{
						while (tw.next(VariableDeclaration.class))
						{
							VariableDeclaration vd = (VariableDeclaration) tw.getProgramElement();
							if ((vd.toString() != null) && (vd.toString().equals(name)))
							{
								breakout = false;
								break;
							}
						}
					}
					
					if (breakout)
						break;
					
					amount--;
				}	
				
				this.elementScores.put(key, amount);
			}
			
			numerator += (amount * value);
		}
		
		return numerator;
	}
	
	// Element recentness objective updated to be normalised as a ratio between 0 and 1.
	// The original score is divided by the amount of elements to get an average value per element. 
	// This is then divided by the highest value it could be i.e. the accumulative value that represents if
	// every element were only found in the current version of the project, in order to get a normalised ratio.
	public float elementRecentness(ArrayList<List<CompilationUnit>> previousUnits)
	{
		int numerator = 0;
		int elements = 0;
		
		for (Entry<String, Integer> e : this.elementDiversity.entrySet())
		{			
			String key = e.getKey();
			int value = e.getValue();
			int amount = previousUnits.size();
			
			if (this.elementScores.containsKey(key))
			{
				amount = this.elementScores.get(key);			
			}
			else
			{
				String name;
				int elementType;
				
				if (!(key.contains(":")))
				{
					elementType = 1;
					name = key.substring(key.lastIndexOf('\\') + 1);
				}
				else if (key.charAt(1) == ':')
				{
					elementType = 3;
					name = key.substring(2);
				}
				else if (key.endsWith(":"))
				{
					elementType = 2;
					name = key.substring(1, key.length() - 1);
				}
				else
				{
					elementType = 4;
					name = key.substring(key.lastIndexOf(':') + 1);
				}
				
				for (int i = previousUnits.size() - 1; i >= 0; i--)
				{
					ForestWalker tw = new ForestWalker(previousUnits.get(i));
					boolean breakout = true;
					
					if (elementType == 1)
					{
						while (tw.next(TypeDeclaration.class))
						{
							TypeDeclaration td = (TypeDeclaration) tw.getProgramElement();
							if (((td instanceof ClassDeclaration) || (td instanceof InterfaceDeclaration)) && 
								(td.getName() != null) && (td.getName().equals(name)))
							{
								breakout = false;
								break;
							}
						}
					}
					else if (elementType == 2)
					{
						while (tw.next(MethodDeclaration.class))
						{
							MethodDeclaration md = (MethodDeclaration) tw.getProgramElement();
							if ((md.getName() != null) && (Refactoring.getMethodName(md).equals(name)))
							{
								breakout = false;
								break;
							}
						}
					}
					else if (elementType == 3)
					{
						while (tw.next(FieldDeclaration.class))
						{
							FieldDeclaration fd = (FieldDeclaration) tw.getProgramElement();
							if ((fd.toString() != null) && (fd.toString().equals(name)))
							{
								breakout = false;
								break;
							}
						}
					}	
					else if (elementType == 4)
					{
						while (tw.next(VariableDeclaration.class))
						{
							VariableDeclaration vd = (VariableDeclaration) tw.getProgramElement();
							if ((vd.toString() != null) && (vd.toString().equals(name)))
							{
								breakout = false;
								break;
							}
						}
					}
					
					if (breakout)
						break;
					
					amount--;
				}	
				
				this.elementScores.put(key, amount);
			}
			
			numerator += (amount * value);
			elements += value;
		}
		
		float answer = (float) numerator / (float) elements;
		return answer / previousUnits.size();
	}
	
	// Returns a value to represent the visibility of a modifier.
	private float identifier(VisibilityModifier vm)
	{
		if (vm instanceof Public)
			return 1;
		else if (vm instanceof Protected)
			return (float) (2 / 3);
		else if (vm instanceof Private)
			return 0;
		else
			return (float) (1 / 3);
	}

	// Returns all the types in a compilation unit including nested types.
	private ArrayList<TypeDeclaration> getAllTypes(CompilationUnit cu)
	{
		AbstractTreeWalker tw = new TreeWalker(cu);
		ArrayList<TypeDeclaration> types = new ArrayList<TypeDeclaration>();

		while (tw.next(TypeDeclaration.class))
		{
			TypeDeclaration td = (TypeDeclaration) tw.getProgramElement();
			
			if (td.getName() != null)
				types.add(td);
		}

		return types;
	} 
	
	public void setUnits(List<CompilationUnit> units)
	{
		this.units = units;
	}
	
	public void setAffectedClasses(ArrayList<String> affectedClasses)
	{
		this.affectedClasses = affectedClasses;
	}
	
	public void setElementDiversity(HashMap<String, Integer> elementDiversity)
	{
		this.elementDiversity = elementDiversity;
	}

}
