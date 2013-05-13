package de.binarytree.plugins.qualitygates.checks;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.AbstractBuild;

public class FailingCheck extends Check {

	@DataBoundConstructor
	public FailingCheck(){}
	@Override
	public Result doCheck(AbstractBuild build) {
		return Result.FAILURE; 
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
