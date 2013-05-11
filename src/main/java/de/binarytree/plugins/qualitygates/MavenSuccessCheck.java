package de.binarytree.plugins.qualitygates;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.AbstractBuild;
@Extension
public class MavenSuccessCheck extends Check {

	@Override
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
			return "Quality Gate Checker for whether the build is stable";
		}
		
		
	}

}
