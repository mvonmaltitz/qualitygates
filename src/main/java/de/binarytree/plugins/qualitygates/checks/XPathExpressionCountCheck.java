package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.binarytree.plugins.qualitygates.result.CheckResult;

public class XPathExpressionCountCheck extends XMLCheck{

	private int successThreshold;
	private int warningThreshold;
	private String name;

	@DataBoundConstructor
	public XPathExpressionCountCheck(String name, String targetFile, String expression, int successThreshold, int warningThreshold){
		this(targetFile, expression, successThreshold, warningThreshold); 
		this.name = name; 
	}

	public XPathExpressionCountCheck(String targetFile, String expression, int successThreshold, int warningThreshold){
		this.expression = expression;
		this.targetFile = targetFile;
		this.successThreshold = successThreshold;
		this.warningThreshold = warningThreshold;
	}

	public int getSucessThreshold(){
		return this.successThreshold;
	}

	public int getWarningThreshold(){
		return this.warningThreshold;
	}

	@Override
	public void doCheck(AbstractBuild build, BuildListener listener, Launcher launcher, CheckResult checkResult){
		try{
			matchExpression(build, checkResult);
		}catch(Exception e){
			String reason = e.getMessage();
			if(reason == null){
				reason = Arrays.toString(e.getStackTrace());
			}
			checkResult.setResult(Result.FAILURE, "Exception: " + reason);
		}
	}

	private void matchExpression(AbstractBuild build, CheckResult checkResult) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException{
		InputStream stream = this.obtainInputStream(build);
		NodeList nodes = getMatchingNodes(stream);
		this.setCheckResult(checkResult, nodes);
	}

	private void setCheckResult(CheckResult checkResult, NodeList nodes){
		if(nodes != null){
			int length = nodes.getLength();

			Result result = Result.FAILURE;
			String reason;  
			if(this.countIsSuccess(length)){
				result = Result.SUCCESS;
				reason = length + " <= success threshold(" + this.successThreshold + ")";
			}else if(this.countIsWarning(length)){
				result = Result.UNSTABLE;
				reason = "success threshold(" + this.successThreshold + ") < " + length + " <= warning threshold(" + this.warningThreshold + ")";
			}else{
				reason = "warning threshold(" + this.warningThreshold + ") < " + length;
			}

			checkResult.setResult(result,this.name  + ": " +  reason);
		}else{
			checkResult.setResult(Result.SUCCESS, this.name + ": No occurrence");
		}
	}

	public boolean countIsSuccess(int count){
		return count <= this.successThreshold;
	}

	public boolean countIsWarning(int count){
		return count <= this.warningThreshold;
	}

	@Override
	public String toString(){
		return super.toString() + "[Occurence of " + this.getExpression() + " in " + this.getTargetFile() + "]";
	}

	@Override
	public String getDescription(){
		return "Count of " + this.getExpression() + " in " + this.getTargetFile();
	}

	@Extension
	public static class DescriptorImpl extends XMLCheckDescriptor{

		@Override
		public String getDisplayName(){
			return "Count the occurence of an XPath expression in an XML file";
		}

	}
}
