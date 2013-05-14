package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import hudson.FilePath;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Job;
import hudson.util.FormValidation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.binarytree.plugins.qualitygates.checks.XMLCheck.DescriptorImpl;

public class XMLCheckTest {

	private DescriptorImpl descriptor = new XMLCheck.DescriptorImpl();
	private XMLCheck check;
	private String expression = "/parent";
	private String filePath = "aFile.xml";
	private ByteArrayInputStream pomStream;
	private String pomString = "<project xmlns='http://maven.apache.org/POM/4.0.0' "
			+ "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
			+ "xsi:schemaLocation='http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd'>"
			+ "<modelVersion>4.0.0</modelVersion> "
			+ "<parent>  "
			+ "<groupId>org.jenkins-ci.plugins</groupId> "
			+ "<artifactId>plugin</artifactId> <version>1.480.3</version>"
			+ "<!-- which version of Jenkins is this plugin built against? --> "
			+ "</parent>" + "</project>";
	private XMLCheck xmlCheck;
	private AbstractBuild build;

	@Before
	public void setUp() throws Exception {
		build = mock(AbstractBuild.class);
		pomStream = new ByteArrayInputStream(pomString.getBytes());
		descriptor = new XMLCheck.DescriptorImpl();
		check = new XMLCheck(filePath, expression) {
			public CheckDescriptor getDescriptor() {
				return descriptor;
			}
		};
	}

	@Test
	public void testDescription() {
		assertTrue(check.toString().contains(expression));
		assertTrue(check.toString().contains(filePath));
	}

	@Test
	public void testSettingAndGettingParameters() {
		assertEquals(expression, check.getExpression());
		assertEquals(filePath, check.getTargetFile());
	}

	@Test
	public void testExpressionSuccessfullValidation() {
		assertEquals(FormValidation.ok().kind,
				descriptor.doCheckExpression("/parent").kind);
	}

	@Test
	public void testExpressionNotSuccessfullValidationBecauseInvalid() {
		assertEquals(FormValidation.error("Invalid").kind,
				descriptor.doCheckExpression("///asdlfkj").kind);
	}

	@Test
	public void testExpressionNotSuccessfullValidationBecausEmpty() {
		assertEquals(FormValidation.error("Invalid").kind,
				descriptor.doCheckExpression("").kind);
	}

	@Test
	public void testValidFilePath() {
		assertEquals(FormValidation.ok().kind,
				descriptor.doCheckTargetFile("aFile.xml").kind);
	}

	@Test
	public void testInvalidAbsoluteFilePath() {
		assertEquals(
				FormValidation.error("Absolute File path disallowed").kind,
				descriptor.doCheckTargetFile("/aFile.xml").kind);
	}

	@Test
	public void testInvalidBackreferencingFilePath() {
		assertEquals(
				FormValidation.error("Absolute File path disallowed").kind,
				descriptor.doCheckTargetFile("target/../../aFile.xml").kind);
	}

	@Test
	public void testInvalidEmptyFilePath() {
		assertEquals(
				FormValidation.error("Absolute File path disallowed").kind,
				descriptor.doCheckTargetFile("").kind);
	}

	@Test
	public void testCheckFindsPresentXMLTag() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException {
		xmlCheck = new XMLCheck("pom.xml", "/project/parent/groupId") {
			@Override
			protected InputStream obtainInputStream(AbstractBuild build) {
				return pomStream;
			}
		};
		assertEquals(Result.SUCCESS, xmlCheck.doCheck(build, null, null));
	}

	@Test
	public void testCheckDoesNotFindNonPresentXMLTag()
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException {
		xmlCheck = new XMLCheck("pom.xml", "/project/parent/notHere") {
			@Override
			protected InputStream obtainInputStream(AbstractBuild build) {
				return pomStream;
			}
		};
		assertEquals(Result.FAILURE, xmlCheck.doCheck(build, null, null));
	}

	@Test
	public void testExceptionCausesFailureResult(){
		xmlCheck = new XMLCheck("pom.xml", "/project/parent/notHere") {
			@Override
			protected InputStream obtainInputStream(AbstractBuild build) {
				throw new RuntimeException(); 
			}
		};
			Result result = xmlCheck.doCheck(build, null, null); 
			assertEquals(Result.FAILURE, result); 
	}
	
	@Test
	public void testValidationOkForValidExpression() {
		assertEquals(descriptor.doCheckExpression("/project/parent/groupId"),
				FormValidation.ok());
	}

	@Test
	public void testValidationNotOkForInvalidExpression() {
		assertEquals(descriptor.doCheckExpression("////**./alsdf/7").kind,
				FormValidation.error("Invalid XPath expression").kind);
	}

}
