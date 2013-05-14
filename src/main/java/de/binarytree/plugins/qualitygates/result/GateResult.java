package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.QualityGate;
import de.binarytree.plugins.qualitygates.checks.Check;

public class GateResult {

	private String gateName;
	private List<CheckResult> checks = new LinkedList<CheckResult>();
	private Result result; 

	public GateResult(QualityGate gate) {
		this.gateName = gate.getName(); 
	}

	public String getGateName(){
		return this.gateName; 
	}

	public CheckResult addResultFor(Check check) {
		CheckResult checkResult = new CheckResult(check); 
		checks.add(checkResult); 
		return checkResult; 
	}

	public int getNumberOfChecks() {
		return this.checks.size(); 
	}

	public void setResult(Result result) {
		this.result = result; 
	}

	public Result getResult() {
		return this.result; 
	}
}
