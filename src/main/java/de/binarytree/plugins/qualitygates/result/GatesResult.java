package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.QualityGate;

public class GatesResult extends ListContainer<GateResult>{

	private List<GateResult> gates(){
		return this.getItems(); 
	}
	
	public int getNumberOfGates() {
		return gates().size();
	}

	public void addGateResult(GateResult gateResult) {
		this.addOrReplaceItem(gateResult); 
	}

	public List<GateResult> getGateResults() {
		return new LinkedList<GateResult>(this.gates());
	}

	
	public Result getResultFor(QualityGate gate) {
		GateResult result = this.getGateResultFor(gate);
		if (result != null) {
			return result.getResult();
		}
		return Result.NOT_BUILT;
	}

	public GateResult getGateResultFor(QualityGate gate) {
		for (GateResult result : this.gates()) {
			if (result.belongsTo(gate)) {
				return result;
			}
		}
		return null;
	}

	@Override
	protected boolean isSameItem(GateResult a, GateResult b) {
		return a.referencesSameGateAs(b); 
	}
}
