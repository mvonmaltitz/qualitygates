package de.binarytree.plugins.qualitygates;

import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.ArrayList;
import java.util.List;

import de.binarytree.plugins.qualitygates.checks.Check;

public class QualityGate {

	
	private List<Check> checks = new ArrayList<Check>(); 

	public void addCheck(Check check) {
		this.checks.add(check); 
	}

	public int getNumberOfChecks() {
		return this.checks.size(); 
	}

	public Result doCheck(AbstractBuild build) {
		Result result = Result.SUCCESS; 
		for(Check check: this.checks){
			result = result.combine(check.doCheck(build));
		}
		return result; 
	}

}
