package de.binarytree.plugins.qualitygates.checks;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

public class FailingCheck extends Check {

	@DataBoundConstructor
	public FailingCheck(){}
	
	@Override
	public Result doCheck(AbstractBuild build, BuildListener listener,
			Launcher launcher) {
		return Result.FAILURE; 
	}
	@Override
	public String toString(){
		return super.toString() +"[Will FAIL]"; 
	}
	@Extension
	public static class DescriptorImpl extends CheckDescriptor
	{

		@Override
		public String getDisplayName() {
			return "Always failing check";
		}
		
		
	}


}
