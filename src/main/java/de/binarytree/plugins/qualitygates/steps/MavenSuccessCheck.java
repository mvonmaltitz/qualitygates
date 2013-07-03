package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.GateStepReport;

public class MavenSuccessCheck extends GateStep {

    @DataBoundConstructor
    public MavenSuccessCheck() {
    }

    @Override
    public void doStep(AbstractBuild build, BuildListener listener,
            Launcher launcher, GateStepReport checkReport) {
        if (build != null && build.getResult() != null) {
            checkReport.setResult(build.getResult(),
                    build.getBuildStatusSummary().message);
        } else {
            checkReport.setResult(Result.FAILURE,
                    "Build or build result was null");
        }
    }

    @Extension
    public static class MavenSuccessCheckDescriptor extends GateStepDescriptor {

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
