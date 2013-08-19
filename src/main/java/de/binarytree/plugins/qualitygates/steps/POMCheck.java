package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This check evaluates whether a given expression is contained in the pom.xml.
 * It is a success to find the expression.
 * 
 * @author Marcel von Maltitz
 * 
 */
public class POMCheck extends XPathExpressionCheck {

    /**
     * Constructs a new pom check.
     * 
     * @param expression
     *            the expression to be matched
     * @param reportContent
     *            whether or not the content of a found expression shall be
     *            reported via the result reason
     */
    @DataBoundConstructor
    public POMCheck(String expression, boolean reportContent) {
        super("pom.xml", expression, reportContent);
    }

    @Extension
    public static class DescriptorImpl extends
            XPathExpressionCheck.DescriptorImpl {

        @Override
        public String getDisplayName() {
            return "Check POM for XML tags";
        }

    }

}