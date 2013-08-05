package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.GateStepDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

public class FindbugsCheckTest {
    private GateStepDescriptor descriptor = new FindbugsCheck.DescriptorImpl();

    private ByteArrayInputStream xmlStream;

    private String violationXml = "<?xml version='1.0'?> "
            + "<BugCollection timestamp='1374021561000' analysisTimestamp='1374021585289' sequence='0' release='' version='2.0.1'> "
            + "<BugInstance category='PERFORMANCE' instanceHash='a439b80dcda11f0261e6092eda1fb9ed' rank='18' instanceOccurrenceNum='0' priority='2' abbrev='SIC' type='SIC_INNER_SHOULD_BE_STATIC' instanceOccurrenceMax='0'> "
            + "</BugInstance> "
            + "<BugInstance category='PERFORMANCE' instanceHash='afae45f5332998cca0c2f4407edbdf46' rank='18' instanceOccurrenceNum='0' priority='2' abbrev='SIC' type='SIC_INNER_SHOULD_BE_STATIC' instanceOccurrenceMax='0'> "
            + "</BugInstance> "
            + "<BugInstance category='PERFORMANCE' instanceHash='12b53c0f694c4a989a499ab9718c152d' rank='18' instanceOccurrenceNum='0' priority='2' abbrev='SIC' type='SIC_INNER_SHOULD_BE_STATIC' instanceOccurrenceMax='0'>  "
            + "</BugInstance>  "
            + "<BugInstance category='PERFORMANCE' instanceHash='6e08f4e74f7bf808816dba7497dc86c8' rank='18' instanceOccurrenceNum='0' priority='2' abbrev='SIC' type='SIC_INNER_SHOULD_BE_STATIC' instanceOccurrenceMax='0'>  "
            + "</BugInstance>  " + "</BugCollection> ";

    class MockFindbugsCheck extends FindbugsCheck {

        public MockFindbugsCheck(int successThreshold, int warningThreshold) {
            super(successThreshold, warningThreshold);
        }

        @Override
        protected InputStream obtainInputStreamOfTargetfileRelativeToBuild(AbstractBuild<?, ?> build) {
            return xmlStream;
        }

        @Override
        protected boolean buildHasFileInWorkspace(AbstractBuild<?, ?> build) throws java.io.IOException, InterruptedException {
            return true;
        }

        @Override
        public GateStepDescriptor getDescriptor() {
            return descriptor;
        }
    }

    private FindbugsCheck check;

    private AbstractBuild<?, ?> build;

    @Before
    public void setUp() {
        check = new MockFindbugsCheck(3, 4);
        build = mock(AbstractBuild.class);
    }

    @Test
    public void testDescription() {
        assertTrue(check.getDescription().toLowerCase().contains("findbugs"));
    }

    @Test
    public void testInitializiation() {
        assertEquals(check.getTargetFile(), FindbugsCheck.FILE);
        assertEquals(check.getExpression(), FindbugsCheck.VIOLATION_EXPRESSION);
    }

    @Test
    public void testCheckFindCorrectNumberOfViolations() {
        this.xmlStream = new ByteArrayInputStream(this.violationXml.getBytes());
        GateStepReport report = check.step(build, null, null);
        assertEquals(Result.UNSTABLE, report.getResult());
        System.out.println(report.getReason());

    }
}
