package de.binarytree.plugins.qualitygates.checks;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.kohsuke.stapler.QueryParameter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class XMLCheck extends GateStep {

	private String expression;
	private String targetFile;

	public XMLCheck(String expression, String targetFile) {
		this.expression = expression;
		this.targetFile = targetFile;
	}

	public String getExpression() {
		return this.expression;
	}

	public String getTargetFile() {
		return this.targetFile;
	}

	protected NodeList getMatchingNodes(InputStream stream)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(false);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(stream);

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(this.getExpression());

		return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	}

	protected InputStream obtainInputStream(AbstractBuild build)
			throws IOException {
		FilePath pom = build.getModuleRoot().child(this.targetFile);
		return pom.read();
	}

	@Override
	public String toString() {
		return super.toString() + "[" + this.getDescription() + "]";
	}

	public abstract static class XMLCheckDescriptor extends GateStepDescriptor {

		public FormValidation doCheckExpression(@QueryParameter String value) {
			if (value.length() == 0) {
				return FormValidation
						.error("XPath expression must not be empty");
			} else {
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				try {
					XPathExpression expr = xpath.compile(value);
				} catch (XPathExpressionException e) {
					return FormValidation
							.error("XPath expression is not valid.");
				}
				return FormValidation.ok();
			}
		}

		public FormValidation doCheckTargetFile(@QueryParameter String value) {
			if (value.length() == 0) {
				return FormValidation.error("Target file must not be empty");
			} else if (value.contains("..")) {
				return FormValidation
						.error("Parent directory '..' may not be referenced");
			} else if (value.startsWith("/")) {
				return FormValidation.error("Path may not be absolute.");
			}
			return FormValidation.ok();
		}

	}
}
