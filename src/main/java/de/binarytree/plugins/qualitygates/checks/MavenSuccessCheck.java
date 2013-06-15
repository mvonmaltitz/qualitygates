package de.binarytree.plugins.qualitygates.checks;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.CheckReport;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;


public class MavenSuccessCheck extends Check{
	
	@DataBoundConstructor
	public MavenSuccessCheck(){}
	
	@Override
	public void doCheck(AbstractBuild build, BuildListener listener,
			Launcher launcher, CheckReport checkReport) {
		if(build != null && build.getResult() != null){
			checkReport.setResult(build.getResult(), build.getBuildStatusSummary().message); 
		}else{
			checkReport.setResult(Result.FAILURE, "Build or build result was null"); 
		}
	}

	@Extension
	public static class MavenSuccessCheckDescriptor extends CheckDescriptor{

		@Override
		public String getDisplayName() {
			return "Result of Maven build"; 
		}
		
	}

	@Override
	public String getDescription() {
		return "The result of the maven build process"; 
	}

}
