package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

public class POMCheck extends XMLCheck {

	@DataBoundConstructor
	public POMCheck(String expression) {
		super("pom.xml", expression); 
	}

	@Extension
	public static class DescriptorImpl extends XMLCheck.DescriptorImpl {

		@Override
		public String getDisplayName() {
			return "Check POM for XML tags";
		}

	}

}