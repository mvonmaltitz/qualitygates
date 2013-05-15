package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.QualityGate;

public class GatesResult {

	List<GateResult> gates = new LinkedList<GateResult>();

	public int getNumberOfGates() {
		return gates.size();
	}

	public void addGateResult(GateResult gateResult) {
		int index = getIndexOfResultWithSameGateReference(gateResult);
		if (index == -1) {
			this.gates.add(gateResult);
		} else {
			this.gates.remove(index);
			this.gates.add(index, gateResult);
		}

	}

	private int getIndexOfResultWithSameGateReference(GateResult gateResult) {
		for (int i = 0; i < this.gates.size(); i++) {
			if (this.gates.get(i).referencesSameGateAs(gateResult)) {
				return i;
			}
		}
		return -1;
	}

	public List<GateResult> getGateResults() {
		return new LinkedList<GateResult>(this.gates);
	}

	public Result getResultFor(QualityGate gate) {
		GateResult result = this.getResultForGate(gate);
		if (result != null) {
			return result.getResult();
		}
		return Result.NOT_BUILT;
	}

	private GateResult getResultForGate(QualityGate gate) {
		for (GateResult result : this.gates) {
			if (result.belongsTo(gate)) {
				return result;
			}
		}
		return null;
	}
}
