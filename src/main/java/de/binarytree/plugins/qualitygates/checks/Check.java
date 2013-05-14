package de.binarytree.plugins.qualitygates.checks;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;

public abstract class Check implements Describable<Check>, ExtensionPoint {

	public abstract Result doCheck(AbstractBuild build, BuildListener listener, Launcher launcher);
    
	public String toString(){
		return "Check [" + this.getDescriptor().getDisplayName() + "]"; 
	}

	public CheckDescriptor getDescriptor(){
		return (CheckDescriptor) Hudson.getInstance().getDescriptor(getClass()); 
	}
	public static DescriptorExtensionList<Check, CheckDescriptor> all(){
		return Hudson.getInstance().<Check, CheckDescriptor>getDescriptorList(Check.class); 
	}

}
