package de.binarytree.plugins.qualitygates.checks;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.AbstractBuild;

public class SonarPMDViolationCheck extends SonarCheck {

	public final String threshold; 
	@DataBoundConstructor
	public SonarPMDViolationCheck(String threshold){
	 this.threshold = threshold; 	
	}
	@Override
	public Result doCheck(AbstractBuild build) {
		return Result.FAILURE;
	}

    @Override
    public SonarPMDViolationCheckDescriptor getDescriptor() {
        return (SonarPMDViolationCheckDescriptor)super.getDescriptor();
    }
	
    @Extension
    public static final class SonarPMDViolationCheckDescriptor extends SonarCheckDescriptor{

		@Override
		public String getDisplayName() {
			return "Quality Gate: Sonar PMD Violation Check";
		}
    	
    	
    }
}
