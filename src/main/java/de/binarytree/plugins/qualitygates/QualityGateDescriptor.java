package de.binarytree.plugins.qualitygates;

import hudson.model.Descriptor;

public abstract class QualityGateDescriptor extends Descriptor<QualityGate>{

	
	protected QualityGateDescriptor(Class<? extends QualityGate> clazz){
		super(clazz); 
	}
	protected QualityGateDescriptor(){}
	

}
