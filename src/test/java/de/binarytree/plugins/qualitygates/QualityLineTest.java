package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.XmlFile;
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

        public void verifyEvaluatorMock() {
            Mockito.verify(this.evaluatorMock).evaluate(
                    any(AbstractBuild.class), any(Launcher.class),
                    any(BuildListener.class));
        }

    }

    private MockQualityLine line;

    @Before
    public void setUp() throws Exception {
        List<Gate> gates = new LinkedList<Gate>();
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
    }

    @Test
    public void tesEvaluation() throws IOException {
        AbstractBuild build = mock(AbstractBuild.class);
        Launcher launcher = mock(Launcher.class);
        BuildListener listener = mock(BuildListener.class);
        line.perform(build, launcher, listener);
        line.verifyEvaluatorMock();
    }

}
