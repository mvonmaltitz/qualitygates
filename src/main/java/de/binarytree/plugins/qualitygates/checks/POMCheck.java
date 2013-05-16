package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

public class POMCheck extends XMLCheck {

	@DataBoundConstructor
	public POMCheck(String expression, boolean reportContent) {
		super("pom.xml", expression, reportContent); 
	}

	@Extension
	public static class DescriptorImpl extends XMLCheck.DescriptorImpl {

		@Override
		public String getDisplayName() {
			return "Check POM for XML tags";
		}

	}

}