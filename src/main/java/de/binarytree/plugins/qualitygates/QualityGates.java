package de.binarytree.plugins.qualitygates;

import java.util.ArrayList;
import java.util.List;

public class QualityGates {

	private List<Gate> gates = new ArrayList<Gate>();  
	public void add(Gate gate){
		gates.add(gate);  
	}

	public int getNumberOfGates() {
		return gates.size(); 
	}
}
