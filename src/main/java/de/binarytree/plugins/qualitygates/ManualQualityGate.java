package de.binarytree.plugins.qualitygates;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.checks.ManualCheck;

public class ManualQualityGate extends QualityGateImpl {

    @DataBoundConstructor
    public ManualQualityGate(String name) {
        super(name, null);
        this.setUpCheck();
    }

    private void setUpCheck() {
        ManualCheck check = new ManualCheck();
        this.checks.add(check);
    }

    @Extension
    public static class DescriptorImpl extends QualityGateDescriptor {
        @Override
        public String getDisplayName() {
            return "Manual Quality Gate";
        }
    }
}
