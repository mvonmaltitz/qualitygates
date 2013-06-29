package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.binarytree.plugins.qualitygates.result.CheckReport;

public class XPathExpressionCheck extends XMLCheck {

	private boolean reportContent;

	@DataBoundConstructor
	public XPathExpressionCheck(String targetFile, String expression,
			boolean reportContent) {
		this.expression = expression;
		this.targetFile = targetFile;
		this.reportContent = reportContent;
	}

	public boolean getReportContent() {
		return this.reportContent;
	}

	@Override
	public void doCheck(AbstractBuild build, BuildListener listener,
			Launcher launcher, CheckReport checkReport) {
		try {
			matchExpression(build, checkReport);
		} catch (Exception e) {
			failCheckAndlogExceptionInCheckReport(checkReport, e);
		}
	}

	private void matchExpression(AbstractBuild build, CheckReport checkReport)
			throws IOException, ParserConfigurationException, SAXException,
			XPathExpressionException {
		InputStream stream = this.obtainInputStream(build);
		String content = this.getXMLContentForXPathExpression(stream);
		this.setCheckResult(checkReport, content);
	}

	private void setCheckResult(CheckReport checkReport, String content) {
		if (content != null) {
			if (reportContent) {
				checkReport.setResult(Result.SUCCESS, "Content: " + content);
			} else {
				checkReport.setResult(Result.SUCCESS, "");
			}
		} else {
			checkReport.setResult(Result.FAILURE, this.getExpression()
					+ " not found in " + this.getTargetFile());
		}
	}

	private String getXMLContentForXPathExpression(InputStream stream)
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException {
		NodeList nodes = getMatchingNodes(stream);
		if (nodes.getLength() > 0) {
			return nodes.item(0).getTextContent();
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return super.toString() + "[Occurence of " + this.getExpression()
				+ " in " + this.getTargetFile() + "]";
	}

	@Override
	public String getDescription() {
		return "Occurence of " + this.getExpression() + " in "
				+ this.getTargetFile();
	}

	@Extension
	public static class DescriptorImpl extends XMLCheckDescriptor {

		@Override
		public String getDisplayName() {
			return "Check XML file for the occurence of an XPath expression";
		}

	}
}
