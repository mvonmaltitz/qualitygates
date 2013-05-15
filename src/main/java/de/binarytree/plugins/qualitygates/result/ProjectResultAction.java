package de.binarytree.plugins.qualitygates.result;

import hudson.model.Action;

public class ProjectResultAction implements Action {
	
	public String getIconFileName() {
		return "project_qg.png";
	}

	public String getDisplayName() {
		return "Project Quality Gates";
	}

	public String getUrlName() {
		return "lastBuild/qualitygates";
	}

}
