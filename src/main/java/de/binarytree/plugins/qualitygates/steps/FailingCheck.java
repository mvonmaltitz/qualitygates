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
 * This check always fails. It can be used to deactivate parts of an active
 * quality line as long as the quality line stops at failing gates.
 * 
 * @author mvm
 * 
 */
public class FailingCheck extends GateStep {
    /**
     * Constructs a new failing check.
     */
    @DataBoundConstructor
    public FailingCheck() {
    }

    @Override
    public void doStep(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener, GateStepReport checkReport) {
        checkReport.setResult(Result.FAILURE, "This check always fails.");
    }

    @Override
    public String toString() {
        return super.toString() + "[Will FAIL]";
    }

    @Extension
    public static class DescriptorImpl extends GateStepDescriptor {

        @Override
        public String getDisplayName() {
            return "Always failing check";
        }

    }

    @Override
    public String getDescription() {
        return "Always failing check";
    }

}
