package de.binarytree.plugins.qualitygates;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheck;

public class ManualGate extends AndGate {

    @DataBoundConstructor
    public ManualGate(String name) {
        super(name, null);
        this.setUpSingleManualCheck();
    }

    private void setUpSingleManualCheck() {
        ManualCheck check = new ManualCheck();
        this.addStep(check);
    }

    @Extension
    public static class DescriptorImpl extends QualityGateDescriptor {
        @Override
        public String getDisplayName() {
            return "Manual Gate";
        }
    }
}
