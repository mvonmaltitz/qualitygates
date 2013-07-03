package de.binarytree.plugins.qualitygates.steps;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

public class POMCheck extends XPathExpressionCheck {

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