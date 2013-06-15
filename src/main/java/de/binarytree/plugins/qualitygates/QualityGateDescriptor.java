package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.CheckDescriptor;

public abstract class QualityGateDescriptor extends Descriptor<Gate>{

	
	protected QualityGateDescriptor(Class<? extends Gate> clazz){
		super(clazz); 
	}
	protected QualityGateDescriptor(){}
	

	public DescriptorExtensionList<Check,CheckDescriptor> getDescriptors(){
		return Check.all(); 
	}

}
