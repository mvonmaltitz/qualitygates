package de.binarytree.plugins.qualitygates.checks;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;


public class MavenSuccessCheck extends Check{
	
	@DataBoundConstructor
	public MavenSuccessCheck(){}
	
	public Result doCheck(AbstractBuild build) {
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
			return "QG Check: Build-Status"; 
		}
		
	}
}
