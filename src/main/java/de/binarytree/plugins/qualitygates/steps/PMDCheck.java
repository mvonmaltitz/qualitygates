package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This check is an adapter for PMD reports. The number of violations is
 * evaluated against the thresholds given at construction time.
 * 
 * @author mvm
 * 
 */
public class PMDCheck extends XPathExpressionCountCheck {

    public static final String VIOLATION_EXPRESSION = "/pmd/file/violation";

    public static final String FILE = "target/pmd.xml";

    @DataBoundConstructor
    public PMDCheck(int successThreshold, int warningThreshold) {
        super("pmd", FILE, VIOLATION_EXPRESSION, successThreshold, warningThreshold);
    }

    @Override
    public String getDescription() {
        return "pmd Violation Check";
    }

    @Extension
    public static class DescriptorImpl extends XPathExpressionCountCheck.DescriptorImpl {
        @Override
        public String getDisplayName() {
            return "Check number of pmd violations";
        }
    }
}
