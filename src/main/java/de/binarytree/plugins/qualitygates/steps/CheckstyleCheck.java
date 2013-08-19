package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This check is an adapter for checkstyle reports. The number of violations and
 * errors is evaluated against the thresholds given at construction time.
 * This check actually is a predefined {@link XPathExpressionCountCheck}.
 * 
 * @author mvm
 * 
 */
public class CheckstyleCheck extends XPathExpressionCountCheck {
    public static final String VIOLATION_EXPRESSION = "/checkstyle/file/violation | checkstyle/file/error";

    public static final String FILE = "target/checkstyle-result.xml";

    /**
     * Constructs a new check using the given thresholds.
     * @param successThreshold
     *            the number of matches which may not be exceeded to be a
     *            success
     * @param warningThreshold
     *            the number of matches which may not be exceeded to be a
     *            warning
     */
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
