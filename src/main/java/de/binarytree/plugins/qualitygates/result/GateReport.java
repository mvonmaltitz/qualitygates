package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.checks.Check;

public class GateReport extends ListContainer<CheckReport>{

	private String gateName;
	private Result result = Result.NOT_BUILT;  
	private transient Gate gate; 

	public GateReport(Gate gate) {
		this.gateName = gate.getName(); 
		this.gate = gate; 
	}

	private List<CheckReport> checks(){
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

	public void addCheckResult(CheckReport checkReport) {
		this.addOrReplaceItem(checkReport); 
		
	}

	public List<CheckReport> getCheckResults() {
		return new LinkedList<CheckReport>(this.checks()); 
	}

	public boolean belongsTo(Gate gate){
		return this.gate == gate; 
	}
	
	public boolean referencesSameGateAs(GateReport gateReport){
		return this.gate == gateReport.gate; 
	}
	@Override
	protected boolean isSameItem(CheckReport a, CheckReport b) {
		return a.referencesSameCheckAs(b); 
	}

	public LinkedList<String> getReasonOfFailure() {
		LinkedList<String> reasons = new LinkedList<String>(); 
		for(CheckReport result : this.checks()){
			if(result.getResult().isWorseOrEqualTo(Result.FAILURE)){
				reasons.add(result.getReason()); 
			}
		}
		return reasons; 
	}

	public CheckReport getResultFor(Check check) {
		for(CheckReport result : this.getCheckResults()){
			if(result.references(check)){
				return result; 
			}
		}
		return null; 
	}
}
