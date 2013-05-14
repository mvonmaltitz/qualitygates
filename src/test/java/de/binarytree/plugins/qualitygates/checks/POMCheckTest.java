package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.util.FormValidation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.binarytree.plugins.qualitygates.checks.POMCheck.DescriptorImpl;

public class POMCheckTest {

	private POMCheck pomCheck;
	private AbstractBuild build;
	private ByteArrayInputStream pomStream;

	@Before
	public void setUp() throws Exception {
		String pomString = "<project xmlns='http://maven.apache.org/POM/4.0.0' "
				+ "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
				+ "xsi:schemaLocation='http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd'>"
				+ "<modelVersion>4.0.0</modelVersion> "
				+ "<parent>  "
				+ "<groupId>org.jenkins-ci.plugins</groupId> "
				+ "<artifactId>plugin</artifactId> <version>1.480.3</version>"
				+ "<!-- which version of Jenkins is this plugin built against? --> "
				+ "</parent>" +
				"</project>";

		build = mock(AbstractBuild.class); 
		pomStream = new ByteArrayInputStream(pomString.getBytes());
	}

	@Test
	public void testDescriptionContainsPOM() {
		DescriptorImpl descriptor = new POMCheck.DescriptorImpl(); 
		assertTrue(descriptor.getDisplayName().contains("POM"));
	}

	@Test
	public void testSettingExpression(){
		pomCheck = new POMCheck("Expression"); 
		assertEquals("Expression", pomCheck.getExpression()); 
	}
	@Test
	public void testCheckFindsXMLTag() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		pomCheck = new POMCheck("/project/parent/groupId"){
			@Override
			protected InputStream obtainInputStream(AbstractBuild build){
				return pomStream; 
			}
		};
//		pomCheck = new POMCheck("////**./alsdf/7");
		assertEquals(Result.SUCCESS, pomCheck.doCheck(build)); 
	}

	@Test
	public void testValidationOkForValidExpression(){
		DescriptorImpl descriptor = new POMCheck.DescriptorImpl(); 
		assertEquals(descriptor.doCheckExpression("/project/parent/groupId"), FormValidation.ok()); 
	}
	@Test
	public void testValidationNotOkForInvalidExpression(){
		DescriptorImpl descriptor = new POMCheck.DescriptorImpl(); 
		assertEquals(descriptor.doCheckExpression("////**./alsdf/7").kind, FormValidation.error("Zeug").kind); 
	}
}
