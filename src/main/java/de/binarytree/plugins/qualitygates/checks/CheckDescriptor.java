package de.binarytree.plugins.qualitygates.checks;

import hudson.ExtensionPoint;
import hudson.model.Descriptor;

public abstract class CheckDescriptor extends Descriptor<Check> implements ExtensionPoint{
	
	protected CheckDescriptor(Class<? extends Check> clazz){
		super(clazz); 
	}
	protected CheckDescriptor(){}
}
