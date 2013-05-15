package de.binarytree.plugins.qualitygates.result;

import hudson.model.ProminentProjectAction;

public class BuildResultAction implements ProminentProjectAction {


	private GatesResult gatesResult;

	public BuildResultAction(GatesResult gatesResult){
		this.gatesResult = gatesResult;  
	}
	public GatesResult getGatesResult(){
		return this.gatesResult; 
	}
	
	public String getIconFileName() {
		return "icon.png";
	}

	public String getDisplayName() {
		return "Execution Results of Qualtity Gates";
	}

	public String getUrlName() {
		return "qualitygates";
	}
}
