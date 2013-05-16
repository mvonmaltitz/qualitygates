package de.binarytree.plugins.qualitygates.checks;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import de.binarytree.plugins.qualitygates.result.CheckResult;

public abstract class Check implements Describable<Check>, ExtensionPoint {

	public CheckResult check(AbstractBuild build, BuildListener listener, Launcher launcher){
		CheckResult checkResult = this.document(); 
		this.doCheck(build, listener, launcher, checkResult); 
		return checkResult; 
	}
	
	public CheckResult document(){
		CheckResult checkResult = new CheckResult(this); 
		return checkResult; 
	}
	public abstract void doCheck(AbstractBuild build, BuildListener listener, Launcher launcher, CheckResult checkResult);
    
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
