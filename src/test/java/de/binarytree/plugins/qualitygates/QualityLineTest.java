package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.binarytree.plugins.qualitygates.QualityLine.DescriptorImpl;

public class QualityLineTest {

    class MockQualityLine extends QualityLine {

        QualityLineEvaluator evaluatorMock;
        public MockQualityLine(String name, Collection<Gate> gates)
                throws IOException {
            super(name, gates);
            this.evaluatorMock = mock(QualityLineEvaluator.class);
            ;
        }

        @Override
        protected XmlFile getConfigXml() {
            return new XmlFile(new File("/tmp/tmp.xml"));
        }

        @Override
        protected QualityLineEvaluator getGateEvaluatorForGates() {
            return this.evaluatorMock;
        }

        @Override
        protected void logQualityLineStart(BuildListener listener) {

        }

        @Override
        protected void logQualityLineEnd(BuildListener listener) {

        }

        public void verifyEvaluatorMock() {
            Mockito.verify(this.evaluatorMock).evaluate(
                    any(AbstractBuild.class), any(Launcher.class),
                    any(BuildListener.class));
        }

    }

    private MockQualityLine line;
    private List<Gate> gates;

    @Before
    public void setUp() throws Exception {
        gates = new LinkedList<Gate>();
        AndGate gate = mock(AndGate.class);
        gates.add(gate);
        line = new MockQualityLine("Line", gates);
    }

    @Test
    public void testDefaultConfiguration() {
        DescriptorImpl descriptor = new QualityLine.DescriptorImpl();
        assertTrue(descriptor.getDisplayName().toLowerCase()
                .contains("quality"));
        assertTrue(descriptor.getDisplayName().toLowerCase()
                .contains("quality"));
        assertTrue(descriptor.isApplicable(null));
        assertEquals(BuildStepMonitor.BUILD, line.getRequiredMonitorService());

    }

    @Test
    public void testInitializeWithNullCollection() throws IOException {
        QualityLine builder = new MockQualityLine("Line", null);
        assertEquals(0, builder.getNumberOfGates());
        assertEquals("Line", builder.getName());
    }

    @Test
    public void testInitializeWithCollection() throws IOException {
        assertEquals(1, line.getNumberOfGates());
        assertEquals(gates, line.getGates());
        assertNotSame(gates, line.getGates());
    }
    
    @Test
    public void testEvaluation() throws IOException {
        AbstractBuild<?, ?> build = TestHelper.getBuildMock();
        Launcher launcher = TestHelper.getLauncherMock();
        BuildListener listener = TestHelper.getListenerMock();
        boolean buildMayContinue = line.perform(build, launcher, listener);
        line.verifyEvaluatorMock();
        Mockito.verify(build).addAction(any(Action.class));
        assertTrue(buildMayContinue);
    }

}
