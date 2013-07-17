package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.binarytree.plugins.qualitygates.GateStepDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.steps.XPathExpressionCheck.DescriptorImpl;

public class XPathExpressionCheckTest {

    private DescriptorImpl descriptor = new XPathExpressionCheck.DescriptorImpl();
    private XPathExpressionCheck check;
    private String expression = "/parent";
    private String filePath = "aFile.xml";
    private ByteArrayInputStream pomStream;
    private String content = "contentCONTENTcontent";
    private String pomString = "<project>"
            + "<parent>  "
            + "<groupId>"
            + content
            + "</groupId> "
            + "<artifactId>plugin</artifactId> <version>1.480.3</version>"
            + "<!-- which version of Jenkins is this plugin built against? --> "
            + "</parent>"
            + "<dependencies>"
            + "<dependency><name>a</name><version>1.0</version></dependency>"  
            + "<dependency><name>b</name><version>1.0-SNAPSHOT</version></dependency>"  
            + "</dependencies>"
            +"</project>";
    private XMLCheck xmlCheck;
    private AbstractBuild build;

    class MockXMLCheck extends XPathExpressionCheck {

        public MockXMLCheck(String targetFile, String expression,
                boolean reportContent) {
            super(targetFile, expression, reportContent);
        }

        public MockXMLCheck(String targetFile, String expression) {
            super(targetFile, expression, true);
        }

        @Override
        protected InputStream obtainInputStreamOfTargetfileRelativeToBuild(AbstractBuild build) {
            return pomStream;
        }

        public GateStepDescriptor getDescriptor() {
            return descriptor;
        }
    }

    @Before
    public void setUp() throws Exception {
        build = mock(AbstractBuild.class);
        pomStream = new ByteArrayInputStream(pomString.getBytes());
        descriptor = new XPathExpressionCheck.DescriptorImpl();
        check = new MockXMLCheck(filePath, expression) {
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
        assertEquals(true, check.getReportContent());
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
    public void testDisplayNameContainsXPath(){
        assertTrue(descriptor.getDisplayName().contains("XPath")); 
    }
    @Test
    public void testInvalidEmptyFilePath() {
        assertEquals(
                FormValidation.error("Absolute File path disallowed").kind,
                descriptor.doCheckTargetFile("").kind);
    }
    @Test
    public void testCheckFindsPresentNonTextXMLTag() throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException {
        xmlCheck = new MockXMLCheck("pom.xml", "/project");
        GateStepReport result = xmlCheck.step(build, null, null);
        assertEquals(Result.SUCCESS, result.getResult());
        assertTrue(result.getReason().contains(content));
    }

    @Test
    public void testCheckFindsPresentXMLTag() throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException {
        xmlCheck = new MockXMLCheck("pom.xml", "/project/parent/groupId");
        GateStepReport result = xmlCheck.step(build, null, null);
        assertEquals(Result.SUCCESS, result.getResult());
        assertTrue(result.getReason().contains(content));
    }

    @Test
    public void testCheckDoesNotFindNonPresentXMLTag()
            throws XPathExpressionException, ParserConfigurationException,
            SAXException, IOException {
        xmlCheck = new MockXMLCheck("pom.xml", "/project/parent/notHere");
        GateStepReport checkReport = xmlCheck.step(build, null, null);
        assertEquals(Result.FAILURE, checkReport.getResult());
        assertFalse(checkReport.getReason().contains("Exception"));
    }

    @Test
    public void testExceptionCausesFailureResult() {
        xmlCheck = new MockXMLCheck("pom.xml", "/project/parent/notHere") {
            @Override
            protected InputStream obtainInputStreamOfTargetfileRelativeToBuild(AbstractBuild build) {
                throw new RuntimeException();
            }
        };
        GateStepReport checkReport = xmlCheck.step(build, null, null);
        assertEquals(Result.FAILURE, checkReport.getResult());
        assertTrue(checkReport.getReason().contains("Exception"));
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

    @Test
    public void testReasonIsEmptyWhenReportingIsOff() {
        xmlCheck = new MockXMLCheck("pom.xml", "/project/parent/groupId", false);
        GateStepReport result = xmlCheck.step(build, null, null);
        assertEquals(Result.SUCCESS, result.getResult());
        assertEquals("", result.getReason());
    }
}
