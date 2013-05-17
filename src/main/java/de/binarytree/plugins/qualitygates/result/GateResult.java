package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import org.mockito.Matchers;

import de.binarytree.plugins.qualitygates.QualityGate;
import de.binarytree.plugins.qualitygates.checks.Check;

public class GateResult extends ListContainer<CheckResult>{

	private String gateName;
	private Result result = Result.NOT_BUILT;  
	private transient QualityGate gate; 

	public GateResult(QualityGate gate) {
		this.gateName = gate.getName(); 
		this.gate = gate; 
	}

	private List<CheckResult> checks(){
		return this.getItems(); 
	}
	
	public String getGateName(){
		return this.gateName; 
	}

	public Result getResult() {
		return this.result; 
	}

	public int getNumberOfChecks() {
		return this.checks().size(); 
	}

	public void setResult(Result result) {
		this.result = result; 
	}

	public void addCheckResult(CheckResult checkResult) {
		this.addOrReplaceItem(checkResult); 
		
	}

	public List<CheckResult> getCheckResults() {
		return new LinkedList<CheckResult>(this.checks()); 
	}

	public boolean belongsTo(QualityGate gate){
		return this.gate == gate; 
	}
	
	public boolean referencesSameGateAs(GateResult gateResult){
		return this.gate == gateResult.gate; 
	}
	@Override
	protected boolean isSameItem(CheckResult a, CheckResult b) {
		return a.referencesSameCheckAs(b); 
	}

	public LinkedList<String> getReasonOfFailure() {
		LinkedList<String> reasons = new LinkedList<String>(); 
		for(CheckResult result : this.checks()){
			if(result.getResult().isWorseOrEqualTo(Result.FAILURE)){
				reasons.add(result.getReason()); 
			}
		}
		return reasons; 
	}
}
