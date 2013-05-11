package de.binarytree.plugins.qualitygates;

import hudson.model.Descriptor;

public abstract class CheckDescriptor extends Descriptor<Check>{
	
	protected CheckDescriptor(Class<? extends Check> clazz){
		super(clazz); 
	}
	protected CheckDescriptor(){}
}
