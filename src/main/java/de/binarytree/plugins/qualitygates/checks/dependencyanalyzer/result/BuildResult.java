package de.binarytree.plugins.qualitygates.checks.dependencyanalyzer.result;

import hudson.model.AbstractBuild;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class BuildResult implements Serializable {
	private static final long serialVersionUID = -1384220307801729741L;
	
	List<ModuleResult> modulesResults = new ArrayList<ModuleResult>();
	
	public void addResult(ModuleResult result) {
		modulesResults.add(result);
	}
	
	public List<ModuleResult> getModules() {
		return modulesResults;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public int getOverallNumberOfUndeclaredDependencies() {
		int count = 0; 
		for(ModuleResult result : this.getModules()){
			count += result.getUndeclaredDependenciesCount(); 
		}
		return count; 
	}
	
	public int getOverallNumberOfUnusedDependencies() {
		int count = 0; 
		for(ModuleResult result : this.getModules()){
			count += result.getUnusedDependenciesCount(); 
		}
		return count; 
	}
		
}
