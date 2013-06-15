package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.Gate;

public class QualityLineReport extends ListContainer<GateReport>{

	private List<GateReport> gates(){
		return this.getItems(); 
	}
	
	public int getNumberOfGates() {
		return gates().size();
	}

	public void addGateResult(GateReport gateReport) {
		this.addOrReplaceItem(gateReport); 
	}

	public List<GateReport> getGateResults() {
		return new LinkedList<GateReport>(this.gates());
	}

	
	public Result getResultFor(Gate gate) {
		GateReport result = this.getGateResultFor(gate);
		if (result != null) {
			return result.getResult();
		}
		return Result.NOT_BUILT;
	}

	public GateReport getGateResultFor(Gate gate) {
		for (GateReport result : this.gates()) {
			if (result.belongsTo(gate)) {
				return result;
			}
		}
		return null;
	}

	@Override
	protected boolean isSameItem(GateReport a, GateReport b) {
		return a.referencesSameGateAs(b); 
	}

	public int getNumberOfSuccessfulGates() {
		int count = 0; 
		for(GateReport result : this.gates()){
			if(resultCausedTermination(result)){
				break; 
			}
			count++; 
		}
		return count;
	}

	public List<String> getReasonsOfTermination() {
		LinkedList<String> reasons = new LinkedList<String>(); 
		for(GateReport result : this.gates()){
			if(resultCausedTermination(result)){
				reasons = result.getReasonOfFailure(); 
				break; 
			}
		}
		return reasons; 
	}

	private boolean resultCausedTermination(GateReport result) {
		return result.getResult().isWorseOrEqualTo(Result.FAILURE);
	}
}
