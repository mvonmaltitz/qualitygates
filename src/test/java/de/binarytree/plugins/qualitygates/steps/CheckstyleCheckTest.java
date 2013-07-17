package de.binarytree.plugins.qualitygates.steps;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.GateStepDescriptor;
import de.binarytree.plugins.qualitygates.result.GateStepReport;

public class CheckstyleCheckTest {
    private GateStepDescriptor descriptor = new CheckstyleCheck.DescriptorImpl();

    private ByteArrayInputStream xmlStream;

    private final String violationXml = "<?xml version='1.0' encoding='UTF-8'?>"
            + "<checkstyle version='5.6'>"
            + "<file name='/home/hudson/inst/java/workspace/Application Monitoring Web/livewatch.admin/src/main/java/com/unitedinternet/mam/applicationmonitoring/web/livewatch/admin/LivewatchAdminWicketApplication.java'>"
            + "<violation line='36' severity='error' message='Missing a Javadoc comment.' source='com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTypeCheck'/>"
            + "<error line='49' column='5' severity='error' message='Missing a Javadoc comment.' source='com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck'/>"
            + "<error line='61' column='5' severity='warning' message='Missing a Javadoc comment.' source='com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck'/>"
            + "<error line='99' column='5' severity='error' message='Missing a Javadoc comment.' source='com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck'/>"
            + "<error line='139' column='5' severity='error' message='Missing a Javadoc comment.' source='com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck'/>"
            + "</file>"
            + "<file name='/home/hudson/inst/java/workspace/Application Monitoring Web/livewatch.admin/src/main/java/com/unitedinternet/mam/applicationmonitoring/web/livewatch/admin/pages/monitoring/Port.java'>"
            + "<error line='11' severity='error' message='Missing a Javadoc comment.' source='com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTypeCheck'/>"
            + "<error line='15' column='5' severity='error' message='Missing a Javadoc comment.' source='com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck'/>"
            + "</file>" + "</checkstyle>";

    private MockCheckstyleCheck check;

    private AbstractBuild build;

    class MockCheckstyleCheck extends CheckstyleCheck {

        public MockCheckstyleCheck(int successThreshold, int warningThreshold) {
            super(successThreshold, warningThreshold);
        }

        @Override
        protected InputStream obtainInputStreamOfTargetfileRelativeToBuild(AbstractBuild build) {
            return xmlStream;
        }

        @Override
        protected boolean buildHasFileInWorkspace(AbstractBuild build) throws java.io.IOException, InterruptedException {
            return true;
        }

        @Override
        public GateStepDescriptor getDescriptor() {
            return descriptor;
        }
    }

    @Before
    public void setUp() {
        check = new MockCheckstyleCheck(6, 7);
        build = mock(AbstractBuild.class);
    }

    @Test
    public void testCheckFindCorrectNumberOfViolations() {
        this.xmlStream = new ByteArrayInputStream(this.violationXml.getBytes());
        GateStepReport report = check.step(build, null, null);
        assertEquals(Result.UNSTABLE, report.getResult());
        System.out.println(report.getReason());

    }

}
