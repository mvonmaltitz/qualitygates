package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This check is an adapter for Findbugs reports. The number of violations is
 * evaluated against the thresholds given at construction time.
 * 
 * This check actually is a predefined {@link XPathExpressionCountCheck}.
 * @author Marcel von Maltitz
 * 
 */
public class FindbugsCheck extends XPathExpressionCountCheck {

    public static final String VIOLATION_EXPRESSION = "/BugCollection/BugInstance";

    public static final String FILE = "target/findbugs.xml";


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
