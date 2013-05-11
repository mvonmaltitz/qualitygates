package de.binarytree.plugins.qualitygates;

import hudson.model.Result;
import hudson.model.AbstractBuild;

public class MavenSuccessCheck {

	public Result doCheck(AbstractBuild build) {
		if(build != null){
			return build.getResult(); 
		}else{
			return Result.FAILURE; 
		}
	}

}
