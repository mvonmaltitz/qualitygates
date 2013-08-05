package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.GateStepDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

/**
 * This check returns the result of the performed build process by Jenkins.
 * @author mvm
 *
 */
public class MavenBuildSuccessCheck extends GateStep {

    @DataBoundConstructor
    public MavenBuildSuccessCheck() {
    }

    @Override
    public void doStep(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener, GateStepReport checkReport) {
        if (build != null && build.getResult() != null) {
            checkReport.setResult(build.getResult(),
                    "Summary: " + build.getBuildStatusSummary().message);
        } else {
            checkReport.setResult(Result.FAILURE,
                    "Build or build result was null");
        }
    }
    
    @Override
    public String getDescription() {
        return "The result of the maven build process";
    }


    @Extension
    public static class MavenSuccessCheckDescriptor extends GateStepDescriptor {

        @Override
        public String getDisplayName() {
            return "Result of Maven build";
        }

    }

}
