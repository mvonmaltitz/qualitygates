package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mockito;

import de.binarytree.plugins.qualitygates.AndGate;
import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.QualityLineEvaluator;
import de.binarytree.plugins.qualitygates.checks.GateStep;
import de.binarytree.plugins.qualitygates.checks.FailingCheck;
import de.binarytree.plugins.qualitygates.checks.manualcheck.ManualCheck;

public class BuildResultActionTest {

    private MockManualCheck mCheck1;

    private MockManualCheck mCheck2;

    private AndGate gate1;

    private AndGate gate2;

    private BuildResultAction action;

    private QualityLineEvaluator gateEvaluator;

    class MockManualCheck extends ManualCheck {

        public MockManualCheck(String hash) {
            this.setHash(hash);
        }
        public String getCurrentUser() {
            return "Current User";
        }

        public void setHash(String hash) {
            super.setHash(hash);
        }

        @Override
        public DescriptorImpl getDescriptor() {
            return new ManualCheck.DescriptorImpl();
        }
    }

    @Before
    public void setUp() throws Exception {
        mCheck1 = new MockManualCheck("Hash1");
        mCheck2 = new MockManualCheck("Hash2");
        final Launcher launcher = mock(Launcher.class);
        LinkedList<GateStep> checks = new LinkedList<GateStep>();
        checks.add(mCheck1);
        checks.add(mCheck2);
        gate1 = new AndGate("Gate 1", null);
        gate2 = new AndGate("Gate 2", checks);

        gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
        gateEvaluator.evaluate(null, null, null);
        action = new BuildResultAction(gateEvaluator) {

            @Override
            public Launcher getLauncher(BuildListener listener) {
                return launcher;

            }
        };
        mCheck1.setHash("Hash1");
        mCheck2.setHash("Hash2");
    }

    private QualityLineEvaluator getGateEvaluatorFromGates(Gate... gates) {
        LinkedList<Gate> gateList = new LinkedList<Gate>();
        for (Gate gate : gates) {
            gateList.add(gate);
        }
        gateEvaluator = new QualityLineEvaluator(gateList);
        return gateEvaluator;
    }


    private void fakeStaplerRequest() throws IOException {
        StaplerRequest req = mock(StaplerRequest.class);
        StaplerResponse res = mock(StaplerResponse.class);
        AbstractBuild build = mock(AbstractBuild.class);

        when(req.hasParameter("id")).thenReturn(true);
        when(req.getParameter("id")).thenReturn("Hash1");
        when(req.findAncestorObject(AbstractBuild.class)).thenReturn(build);
        when(build.getLogFile()).thenReturn(new File("/tmp/logfile.log"));
        action.doApprove(req, res);
    }
    @Test
    public void testManualApprovalViaGetRequest() throws IOException {
        assertFalse(mCheck1.isApproved()); 
        fakeStaplerRequest();
        assertFalse(mCheck1.isApproved()); // Check toggles after reevaluation back to false
    }

}
