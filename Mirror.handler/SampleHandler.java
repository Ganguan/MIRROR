package MIRROR.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;

import MIRROR.ast.ClassMethodVisitor;
import MIRROR.utils.Log;
import MIRROR.utils.SingletonNullProgressMonitor;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;


public class SampleHandler extends AbstractHandler{

	public static IJavaProject projectOriginal;
	public static IJavaProject projectCopy;
	private ArrayList<IType> allTypes;
	private ArrayList<IMethod> allMethods;
	private String RefactoringType;
	
	

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"MyRef",
				"Hello, Eclipse world");
		try {
			//select a project
			projectOriginal = getProjectFromWorkspace(event);
			projectOriginal.exists();
			//tan chuang
			 RefactoringType = showCalibrationDialog(projectOriginal.getProject());
			
			//process bar
			
			IWorkbench wb = PlatformUI.getWorkbench();
			IProgressService ps = wb.getProgressService();
		
			try {
				ps.busyCursorWhile(new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						// (re)create log file
						Log.refreshLog();
						// clone project
						Log.writeLog("Start Task1:getting classes and methods ");
						monitor.beginTask("Start Task1:getting classes and methods ...",IProgressMonitor.UNKNOWN);
						
						// get all classes and methods from project
						
							try {
								getAllClassesAndMethods(projectOriginal, monitor);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						int methodsCount = 0;
						Log.writeLog("Start Task2:Analyzing "+allMethods.size());
						monitor.beginTask("Start Task2:Analyzing "+allMethods.size(),IProgressMonitor.UNKNOWN);					
					}
					
				});
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}catch(NullPointerException e) {
			Log.writeError(e);
		}
		
		return null;
		
			
		
		
	}


	private String showCalibrationDialog(IProject iProject) {
		// TODO Auto-generated method stub
		
		Object[] possibilities = { "Decrease Field Visibility", "Increase Field Visibility", "Make Field Final", "Make Field NonFinal", "Make Field NonStatic", "Make Field Static", "Make Field Down", "Make Field Up", "Remove Field",
				"Decrease Method Visibility", "Increase Method Visibility", "Make Method Final", "Make Method NonFinal", "Make Method NonStatic", "Make Method Static", "Make Method Down", "Make Method Up", "Remove Method",
				 "Collapse Hierarchy", "Extract Subclass", "Make Class Concrete", "Make Class Abstract", "Make Class Final", "Make Class NonFinal","Remove Class","Remove Interface"};
		String s = (String) JOptionPane.showInputDialog(null, "All RefactoringType:26",
				"MyRef-RecommendationsInfo", JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[5]);
		String s1= JOptionPane.showInputDialog("Project name:",iProject.getName());
		return s;
	}

	private void getAllClassesAndMethods(final IJavaProject jprojectCopy, final IProgressMonitor monitor)
			throws CoreException {

		try {

			jprojectCopy.getProject().accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws JavaModelException {
					if (resource instanceof IFile && resource.getName().endsWith(".java")) {
						ICompilationUnit unit = ((ICompilationUnit) JavaCore.create((IFile) resource));

						ClassMethodVisitor cmv = new ClassMethodVisitor(unit);
						if (cmv.getArrayTypes() != null) {
							allTypes.addAll(cmv.getArrayTypes());
						}

						if (cmv.getArrayMethod() != null) {
							allMethods.addAll(cmv.getArrayMethod());
						}

					}
					checkIfCanceled(monitor);
					return true;
				}
			});

		}

		// another way to read class and methods if the first one don't work
		catch (NullPointerException e) {
			IPackageFragment[] packages = jprojectCopy.getPackageFragments();
			for (IPackageFragment mypackage : packages) {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
						IType[] types = unit.getTypes();
						for (int i = 0; i < types.length; i++) {
							IType type = types[i];
							allTypes.add(type);
							IMethod[] imethods = type.getMethods();
							for (int j = 0; j < imethods.length; j++) {
								if (!imethods[j].getDeclaringType().isAnonymous()) {
									allMethods.add(imethods[j]);
								}
							}
						}
					}
				}
				checkIfCanceled(monitor);
			}
		}
	}
	private void checkIfCanceled(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			if (monitor != null)
				monitor.done();
			throw new OperationCanceledException();
		}
	}
	@SuppressWarnings("deprecation")
//	private IJavaProject cloneProject(IProject iProject) throws CoreException {
//
//		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//		IProjectDescription projectDescription = iProject.getDescription();
//		String cloneName = iProject.getName() + "Temp";
//
//		// create clone project in workspace
//		IProjectDescription cloneDescription = workspaceRoot.getWorkspace().newProjectDescription(cloneName);
//
//		// copy project files
//		iProject.copy(cloneDescription, true, SingletonNullProgressMonitor.getNullProgressMonitor());
//		IProject clone = workspaceRoot.getProject(cloneName);
//
//		cloneDescription.setNatureIds(projectDescription.getNatureIds());
//		cloneDescription.setReferencedProjects(projectDescription.getReferencedProjects());
//		cloneDescription.setDynamicReferences(projectDescription.getDynamicReferences());
//		cloneDescription.setBuildSpec(projectDescription.getBuildSpec());
//		cloneDescription.setReferencedProjects(projectDescription.getReferencedProjects());
//
//		clone.setDescription(cloneDescription, null);
//		clone.open(IResource.BACKGROUND_REFRESH, SingletonNullProgressMonitor.getNullProgressMonitor());
//
//		return JavaCore.create(clone);
//	}
	private IJavaProject getProjectFromWorkspace(ExecutionEvent event) {

		TreeSelection selection = (TreeSelection) HandlerUtil.getCurrentSelection(event);

		if (selection == null || selection.getFirstElement() == null) {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Information", "Please select a project");
			return null;
		}

		JavaProject jp;
		Project p;

		try {
			jp = (JavaProject) selection.getFirstElement();
			return JavaCore.create(jp.getProject());
		} catch (ClassCastException e) {
			p = (Project) selection.getFirstElement();
			return JavaCore.create(p.getProject());
		}
	}
}
