package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import org.mockito.Matchers;

import de.binarytree.plugins.qualitygates.QualityGate;
import de.binarytree.plugins.qualitygates.checks.Check;

public class GateResult {

	private String gateName;
	private List<CheckResult> checks = new LinkedList<CheckResult>();
	private Result result = Result.NOT_BUILT;  
	private transient QualityGate gate; 

	public GateResult(QualityGate gate) {
		this.gateName = gate.getName(); 
		this.gate = gate; 
	}

	public boolean belongsTo(QualityGate gate){
		return this.gate == gate; 
	}
	
	public boolean referencesSameGateAs(GateResult gateResult){
		return this.gate == gateResult.gate; 
	}
	
	public String getGateName(){
		return this.gateName; 
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

	public void addCheckResult(CheckResult checkResult) {
		this.checks.add(checkResult); 
		
	}

	public List<CheckResult> getCheckResults() {
		return new LinkedList<CheckResult>(this.checks); 
	}
}
