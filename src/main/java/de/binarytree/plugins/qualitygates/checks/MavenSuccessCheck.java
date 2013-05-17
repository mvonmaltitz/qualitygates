package de.binarytree.plugins.qualitygates.checks;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.CheckResult;

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
			Launcher launcher, CheckResult checkResult) {
		if(build != null){
			checkResult.setResult(build.getResult(), build.getBuildStatusSummary().message); 
		}else{
			checkResult.setResult(Result.FAILURE, "Build was null"); 
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
