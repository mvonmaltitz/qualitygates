package de.binarytree.plugins.qualitygates;

import java.util.ArrayList;
import java.util.List;

public class QualityGates {

	private List<QualityGate> qualityGates = new ArrayList<QualityGate>();  
	public void add(QualityGate gate){
		qualityGates.add(gate);  
	}

	public int getNumberOfGates() {
		return qualityGates.size(); 
	}
}
