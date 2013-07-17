package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

public class FindbugsCheck extends XPathExpressionCountCheck {

    public static final String VIOLATION_EXPRESSION = "/BugCollection/BugInstance";

    public static final String FILE = "target/findbugs.xml";

    @DataBoundConstructor
    public FindbugsCheck(int successThreshold, int warningThreshold) {
        super("Findbugs", FILE, VIOLATION_EXPRESSION, successThreshold, warningThreshold);
    }

    @Override
    public String getDescription() {
        return "Findbugs Violation Check";
    }

    @Extension
    public static class DescriptorImpl extends XPathExpressionCountCheck.DescriptorImpl {
        @Override
        public String getDisplayName() {
            return "Check number of findbugs violations";
        }
    }
}
