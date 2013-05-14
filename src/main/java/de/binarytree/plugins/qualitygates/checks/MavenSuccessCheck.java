package de.binarytree.plugins.qualitygates.checks;

import org.kohsuke.stapler.DataBoundConstructor;

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
	public Result doCheck(AbstractBuild build, BuildListener listener,
			Launcher launcher) {
		if(build != null){
			return build.getResult(); 
		}else{
			return Result.FAILURE; 
		}
	}

	@Extension
	public static class MavenSuccessCheckDescriptor extends CheckDescriptor{

		@Override
		public String getDisplayName() {
			return "Result of Maven build"; 
		}
		
	}

}
