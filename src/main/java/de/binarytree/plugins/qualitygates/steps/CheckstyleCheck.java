package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

public class CheckstyleCheck extends XPathExpressionCountCheck {
    public static final String VIOLATION_EXPRESSION = "/checkstyle/file/violation | checkstyle/file/error";

    public static final String FILE = "target/checkstyle-result.xml";

    @DataBoundConstructor
    public CheckstyleCheck(int successThreshold, int warningThreshold) {
        super("Checkstyle", FILE, VIOLATION_EXPRESSION, successThreshold, warningThreshold);
    }

    @Override
    public String getDescription() {
        return "Checkstyle Violation Check";
    }

    @Extension
    public static class DescriptorImpl extends XPathExpressionCountCheck.DescriptorImpl {
        @Override
        public String getDisplayName() {
            return "Check number of checkstyle violations";
        }
    }
}
