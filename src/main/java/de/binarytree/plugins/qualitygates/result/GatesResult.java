package de.binarytree.plugins.qualitygates.result;

import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.QualityGate;

public class GatesResult {

	List<GateResult> gates = new LinkedList<GateResult>(); 
	
	public GateResult addResultFor(QualityGate gate) {
		GateResult gateResult = new GateResult(gate); 
		gates.add(gateResult); 
		return gateResult; 
	}

	public int getNumberOfGates() {
		return gates.size(); 
	}

}
