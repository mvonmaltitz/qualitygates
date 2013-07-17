package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import hudson.model.Result;
import hudson.model.AbstractBuild;

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
import de.binarytree.plugins.qualitygates.steps.XPathExpressionCountCheck.DescriptorImpl;

public class XPathExpressionCountCheckTest {

    private DescriptorImpl descriptor = new XPathExpressionCountCheck.DescriptorImpl();

    private MockXMLCheck check;

    private String expression = "/pmd/file/violation";

    private String filePath = "pmd.xml";

    private ByteArrayInputStream xmlStream;

    private String xmlHeader = "<pmd>";

    private String snapshotExpression = "/project/dependencies/dependency/version[contains(.,'SNAPSHOT')]";

    private String xmlFileStart = "<file name='file.txt'>";

    private String xmlViolation = "<violation beginline='46' endline='46' class='User' method='setLastName' variable='asdf' externalInfoUrl='www' priority='3'>"
            + "Avoid unused local variables such as 'asdf'. </violation> ";

    private String pomString = "<project>" + "<dependencies>"
            + "<dependency><name>a</name><version>1.0</version></dependency>"
            + "<dependency><name>b</name><version>1.0</version></dependency>"
            + "<dependency><name>c</name><version>1.0-SNAPSHOT</version></dependency>" + "</dependencies>"
            + "</project>";

    private String xmlFileEnd = "</file>";

    private String xmlFooter = " </pmd>";

    private AbstractBuild build;

    private int successThreshold = 1;

    private int warningThreshold = 3;

    private String name = "XMLCheck";

    class MockXMLCheck extends XPathExpressionCountCheck {

        public MockXMLCheck(String name, String targetFile, String expression, int successThreshold,
                int warningThreshold) {
            super(name, targetFile, expression, successThreshold, warningThreshold);
        }

        @Override
        protected InputStream obtainInputStreamOfTargetfileRelativeToBuild(AbstractBuild build) {
            return xmlStream;
        }

        @Override
        protected boolean buildHasFileInWorkspace(AbstractBuild build) throws IOException, InterruptedException {
            return true;
        }

        @Override
        public GateStepDescriptor getDescriptor() {
            return descriptor;
        }
    }

    @Before
    public void setUp() throws Exception {
        build = mock(AbstractBuild.class);
        descriptor = new XPathExpressionCountCheck.DescriptorImpl();
        check = new MockXMLCheck(name, this.filePath, this.expression, this.successThreshold, this.warningThreshold);
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
        assertEquals(this.successThreshold, check.getSuccessThreshold());
        assertEquals(this.warningThreshold, check.getWarningThreshold());
        assertEquals(this.name, check.getName());
    }

    @Test
    public void testNoMatchIsSuccess() {
        this.xmlStream = byteStreamFromViolationCount(0);
        GateStepReport result = check.step(build, null, null);
        assertEquals(Result.SUCCESS, result.getResult());
        assertTrue(result.getReason().contains("0"));
    }

    @Test
    public void testOnSuccessThresholdIsSuccess() {
        this.xmlStream = byteStreamFromViolationCount(this.successThreshold);
        GateStepReport result = check.step(build, null, null);
        assertEquals(Result.SUCCESS, result.getResult());
        assertTrue(result.getReason().contains(Integer.toString(this.successThreshold)));
    }

    @Test
    public void testOverSuccessThresholdIsWarning() {
        this.xmlStream = byteStreamFromViolationCount(this.successThreshold + 1);
        GateStepReport result = check.step(build, null, null);
        assertEquals(Result.UNSTABLE, result.getResult());
        assertTrue(result.getReason().contains(Integer.toString(this.successThreshold + 1)));
    }

    private ByteArrayInputStream byteStreamFromViolationCount(int violationCount) {
        return new ByteArrayInputStream(this.getXMLforNumberOfViolations(violationCount).getBytes());
    }

    @Test
    public void testOnWarningThresholdIsWarning() {
        this.xmlStream = byteStreamFromViolationCount(this.warningThreshold);
        GateStepReport result = check.step(build, null, null);
        assertEquals(Result.UNSTABLE, result.getResult());
        assertTrue(result.getReason().contains(Integer.toString(this.warningThreshold)));
    }

    @Test
    public void testOverWarningThresholdIsFailure() {
        this.xmlStream = byteStreamFromViolationCount(this.warningThreshold + 1);
        GateStepReport result = check.step(build, null, null);
        assertEquals(Result.FAILURE, result.getResult());
        assertTrue(result.getReason().contains(Integer.toString(this.warningThreshold + 1)));
    }

    @Test
    public void testOverWarningThresholdInMultipleFilesIsFailure() {
        this.xmlStream = byteStreamFromViolationCount(this.warningThreshold + 1);
        GateStepReport result = check.step(build, null, null);
        assertEquals(Result.FAILURE, result.getResult());
        assertTrue(result.getReason().contains(Integer.toString(this.warningThreshold + 1)));
    }

    @Test
    public void testExceptionCausesFailureResult() {
        check = new MockXMLCheck(name, "pom.xml", "/project/parent/notHere", successThreshold, warningThreshold) {

            @Override
            protected InputStream obtainInputStreamOfTargetfileRelativeToBuild(AbstractBuild build) {
                throw new RuntimeException();
            }
        };
        GateStepReport checkReport = check.step(build, null, null);
        assertEquals(Result.FAILURE, checkReport.getResult());
        assertTrue(checkReport.getReason().contains("Exception"));
    }

    @Test
    public void testCheckFailsWhenFindingSNAPSHOTDependencies() throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException {
        this.xmlStream = new ByteArrayInputStream(this.pomString.getBytes());
        check = new MockXMLCheck(name, "pom.xml", snapshotExpression, 0, 0);
        GateStepReport report = check.step(build, null, null);
        assertEquals(Result.FAILURE, report.getResult());
    }

    @Test
    public void testCheckSucceedsWhenNotFindingSNAPSHOTDependencies() throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException {
        this.xmlStream = new ByteArrayInputStream(this.pomString.replace("SNAPSHOT", "").getBytes());
        check = new MockXMLCheck(name, "pom.xml", snapshotExpression, 0, 0);
        GateStepReport report = check.step(build, null, null);
        assertEquals(Result.SUCCESS, report.getResult());
    }

    public String getXMLforNumberOfViolations(int violations) {
        return this.getXMLforNumberOfViolationsInFiles(violations, 1);
    }

    public String getXMLforNumberOfViolationsInFiles(int violations, int files) {
        String xml = this.xmlHeader;
        for (int f = 0; f < files; f++) {
            xml += this.xmlFileStart;
            for (int i = 0; i < violations; i++) {
                xml += this.xmlViolation;
            }
            xml += this.xmlFileEnd;
        }
        xml += this.xmlFooter;
        return xml;
    }
}
