package com.kms.katalon.entity.project;

import java.io.File;
import java.io.PrintWriter;

public class ProjectImportingEntity 
{
	
	   private boolean isCreateNewProject;	   
       private String projectName;
       private ProjectEntity currentProject;
       private String folderImportTypeResult;
       private String testSuiteImportTypeResult;
       private String testCaseImportTypeResult;
       private String dataFileImportTypeResult;
       private String objectImportTypeResult;
       private String importFolderName;
       private String importGUID;
       private String zipFileInfo;
       private File importDirectory;
       private PrintWriter logPrintWriter;
     
       
       
       public ProjectImportingEntity()
       {
    	   isCreateNewProject = false; 	   
           projectName = "";
           currentProject = new ProjectEntity();
           folderImportTypeResult = "";
           testSuiteImportTypeResult = "";
           testCaseImportTypeResult = "";
           dataFileImportTypeResult = "";
           objectImportTypeResult = "";
           importFolderName = "";
           importGUID = "";
           zipFileInfo = "";
           importDirectory = null;
           setLogPrintWriter(null);
       }
       
       public String getImportData()
       {
    	   return zipFileInfo;
       }
       
       public void setImportData(String input)
       {
    	   zipFileInfo= input;
       }
       
       public Boolean getIsCreateNewProject()
       {
    	   return isCreateNewProject;
       }
       
       public void setIsCreateNewProject(boolean input)
       {
    	   isCreateNewProject= input;
       }
       
       
       public String getProjectName()
       {
    	   return projectName;
       }
       
       public void setProjectName(String input)
       {
    	   projectName= input;
       }
       
       
       public ProjectEntity getCurrentProject()
       {
    	   return currentProject;
       }
       
       public void setCurrentProject(ProjectEntity input)
       {
    	   currentProject= input;
       }
       
       
       public String getFolderImportTypeResult()
       {
    	   return folderImportTypeResult;
       }
       
       public void setFolderImportTypeResult(String input)
       {
    	   folderImportTypeResult= input;
       }
       
       
       public String getTestSuiteImportTypeResult()
       {
    	   return testSuiteImportTypeResult;
       }
       
       public void setTestSuiteImportTypeResult(String input)
       {
    	   testSuiteImportTypeResult= input;
       }
       
       
       public String getTestCaseImportTypeResult()
       {
    	   return testCaseImportTypeResult;
       }
       
       public void setTestCaseImportTypeResult(String input)
       {
    	   testCaseImportTypeResult= input;
       }
       
       public String getDataFileImportTypeResult()
       {
    	   return dataFileImportTypeResult;
       }
       
       public void setDataFileImportTypeResult(String input)
       {
    	   dataFileImportTypeResult= input;
       }
       
       
       public String getObjectImportTypeResult()
       {
    	   return objectImportTypeResult;
       }
       
       public void setObjectImportTypeResult(String input)
       {
    	   objectImportTypeResult= input;
       }
       
       public String getImportFolderName()
       {
    	   return importFolderName;
       }
       
       public void setImportFolderName(String input)
       {
    	   importFolderName= input;
       }
       
       public String getImportGUID()
       {
    	   return importGUID;
       }
       
       public void setImportGUID(String input)
       {
    	   importGUID = input;
       }

	public File getImportDirectory() {
		return importDirectory;
	}

	public void setImportDirectory(File importDirectory) {
		this.importDirectory = importDirectory;
	}

	public PrintWriter getLogPrintWriter() {
		return logPrintWriter;
	}

	public void setLogPrintWriter(PrintWriter logPrintWriter) {
		this.logPrintWriter = logPrintWriter;
	}
       
       

}
