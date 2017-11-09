package comparatorp.views;


import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import javax.inject.Inject;

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "comparatorp.views.SampleView";

	@Inject IWorkbench workbench;	
	private TableViewer viewer;
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;
	private Text text1;
	private Text text2;
	private Text text3;
	private Text pathField1;
	private Text pathField2;
	private Text pathField3;


	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(3,false));	
		Image image = new Image(parent.getShell().getDisplay(), 
		          getClass().getClassLoader().getResourceAsStream("icons/fold.png"));
		/*UI description of pathField1*/
		text1 = new Text(parent, SWT.WRAP);
		text1.setText("Choose folder ¹1!->");
		text1.setEditable(false);
		
		/*Create field for input path for Folder ¹1*/
		pathField1 = new Text(parent, SWT.BORDER);	
		/*Create button for choosing folder ¹1 path  */
		btn1 = new Button(parent, SWT.PUSH);		
		btn1.setImage(image);
		/*UI description of pathField2*/
		text2 = new Text(parent, SWT.WRAP);
		text2.setText("Choose folder ¹2!->");
		text2.setEditable(false);
		
		/*Create field for input path for Folder ¹2*/
		pathField2 = new Text(parent, SWT.BORDER);
		/* Create button for choosing folder ¹2 path  */
		btn2 = new Button(parent, SWT.PUSH);		
		btn2.setImage(image);
		/*UI description of pathField3*/
		text3 = new Text(parent, SWT.WRAP);
		text3.setText("Choose result folder!->");
		text3.setEditable(false);
		
		/*Create field for input path for result folder */
		pathField3 = new Text(parent, SWT.BORDER);
		/*UI description of pathField3*/
		btn3 = new Button(parent, SWT.PUSH);				
		btn3.setImage(image);
		/*Create button for starting comparing. Check to approving folders path. */
		btn4 = new Button(parent, SWT.PUSH);
		btn4.setText("Let's do it!");	
		
		makeListeners();
	}
		
	
	/* 
	 * Create listeners for buttons
	 * **/
	private void makeListeners() {	
	btn1.addSelectionListener(new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub			
		}
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			DirectoryDialog dd = new DirectoryDialog(new Shell());	
			String buff = dd.open();
			pathField1.setText(buff);
		}		
	});
		
	btn2.addSelectionListener(new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub			
		}
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			DirectoryDialog dd = new DirectoryDialog(new Shell());	
			String buff = dd.open();
			pathField2.setText(buff);
		}		
	});
	
	btn3.addSelectionListener(new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub			
		}
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			DirectoryDialog dd = new DirectoryDialog(new Shell());	
			String buff = dd.open();
			pathField3.setText(buff);
		}		
	});
	
	btn4.addSelectionListener(new SelectionListener() {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub			
		}
		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
			boolean isOK = true;				
			File directory1 = new File(pathField1.getText());					
			if(!directory1.exists()) {
				MessageDialog.openInformation(null, null, "Folder ¹1 is incorrect");
				isOK = false;
			}		
			
			File directory2 = new File(pathField2.getText());					
			if(!directory2.exists()) {
				MessageDialog.openInformation(null, null, "Folder ¹2 is incorrect");					
				isOK = false;
			}
			
			File directory3 = new File(pathField3.getText());
			
			if(!directory3.exists()) {	
				MessageDialog.openInformation(null, null, "Result folder is incorrect");				
				isOK = false;
			}
			if(directory3.toString().equals(directory2.toString())||directory3.toString().equals(directory1.toString())) {
				MessageDialog.openInformation(null, null, "Result folder must be not \"Folder ¹1\""
						+ " or \"Folder ¹2\"");				
				isOK = false;
			}
			
			if (isOK) {				
				try {
					compare(directory1, directory2, pathField3.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				MessageDialog.openInformation(null, null, "Done");		        
			}
		}		
	});}

	/* 
	 * Compare files and push it in result folder
	 * **/
	public void compare (File folder1, File folder2, String foldResult) throws IOException {
		/*Create changers for files list*/
		ArrayList <File> filesArray = new ArrayList <File>();
		Stack <File> filesStack = new Stack<File>();
		
		Path destinationFolder = Paths.get(foldResult);
		
		File[] files1 = folder1.listFiles();
		File[] files2 = folder2.listFiles();
		
		String folderNum1 = "(1)";
		String folderNum2 = "(2)";		
						
		Collections.addAll(filesStack, files1);
		Collections.addAll(filesArray, files2);
		
		
		/*Compare files from two arrays and put files to result folder if they have differences by name, size or changing time*/
		while (!filesStack.empty()) {
			File file1 = filesStack.pop();
			boolean isCoincided = false;			
			long sizeF1 = file1.length();
			long dateF1 = file1.lastModified();			
			
			for(int i =0; i<filesArray.size();i++) {	
				File file2 = filesArray.get(i);
				if(file1.getName().equals(file2.getName())){
					if(sizeF1 == file2.length()) {
						if(dateF1 == file2.lastModified()) {
							isCoincided = true;
							filesArray.remove(i);
							}
						else {
							if (file2.isDirectory())
								copyFolder(file2, foldResult, folderNum2);
							else 
								Files.copy(file2.toPath(), destinationFolder.resolve(folderNum2+file2.getName()), StandardCopyOption.REPLACE_EXISTING);								
								filesArray.remove(i);						
						}
					}
					else {	
						if (file2.isDirectory())
							copyFolder(file2, foldResult, folderNum2);
						else 
							Files.copy(file2.toPath(), destinationFolder.resolve(folderNum2+file2.getName()), StandardCopyOption.REPLACE_EXISTING);							
							filesArray.remove(i);						
					}
				}				
			}
			
			if(isCoincided) continue;
			
			if (file1.isDirectory())
				copyFolder(file1, foldResult, folderNum1);
			else
				Files.copy(file1.toPath(), destinationFolder.resolve(folderNum1 + file1.getName()), StandardCopyOption.REPLACE_EXISTING);
		}			
		
		/*if files remain in second folder, they puted in result folder*/
		for(File file: filesArray) {
			if (file.isDirectory())
				copyFolder(file, foldResult , folderNum2);
			else
				Files.copy(file.toPath(), destinationFolder.resolve(folderNum2 + file.getName()), StandardCopyOption.REPLACE_EXISTING);			
		}
	}
	
	/* 
	 * Recursive copying for folders and folders in folders
	 * **/
	public void copyFolder (File fold, String path, String foldNum) throws IOException {
		File[] files = fold.listFiles();
		if(!new File(path+"\\"+foldNum+fold.getName()).exists()) {
			Files.copy(fold.toPath(), Paths.get(path).resolve(foldNum+fold.getName()), StandardCopyOption.REPLACE_EXISTING);	
			Path destDir = Paths.get(path+"\\"+foldNum+fold.getName());		
			for(File file: files) {			
				if(file.isDirectory()) {
					copyFolder(file, destDir.toString(), "");
				}
				else {				
					Files.copy(file.toPath(), destDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);				
				}
			}
		}
	}


	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
