package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import de.binarytree.plugins.qualitygates.AndGate;
import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.QualityLineEvaluator;
import de.binarytree.plugins.qualitygates.TestHelper;
import de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheck;


public class BuildResultActionTest {

    private MockManualCheck mCheck1;

    private MockManualCheck mCheck2;

    private AndGate gate1;

    private AndGate gate2;

    private BuildResultAction action;

    private QualityLineEvaluator gateEvaluator;

    private QualityLineEvaluator fakeEvaluator;

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
        final Launcher launcher = TestHelper.getLauncherMock(); 
        final BuildListener listener = TestHelper.getListenerMock(); 
        LinkedList<GateStep> checks = new LinkedList<GateStep>();
        checks.add(mCheck1);
        checks.add(mCheck2);
        gate1 = new AndGate("Gate 1", null);
        gate2 = new AndGate("Gate 2", checks);

        gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
        gateEvaluator.evaluate(null, launcher, listener);
        fakeEvaluator = mock(QualityLineEvaluator.class); 
        when(fakeEvaluator.getLatestResults()).thenReturn(gateEvaluator.getLatestResults()); 
        when(fakeEvaluator.evaluate(any(AbstractBuild.class), any(Launcher.class), any(BuildListener.class))).thenReturn(gateEvaluator.getLatestResults()); 
        action = new BuildResultAction(fakeEvaluator) {

            @Override
            public Launcher getLauncher(BuildListener listener) {
                return launcher;

            }
        };
        mCheck1.setHash("Hash1");
        mCheck2.setHash("Hash2");
    }

    private QualityLineEvaluator getGateEvaluatorFromGates(Gate... gates) {
        LinkedList<Gate> gateList = new LinkedList<Gate>(); for (Gate gate : gates) {
            gateList.add(gate);
        }
        gateEvaluator = new QualityLineEvaluator(gateList);
        return gateEvaluator;
    }


    private StaplerRequest prepareFakedStaplerRequest() throws IOException {
        StaplerRequest req = mock(StaplerRequest.class);
        AbstractBuild<?, ?> build = mock(AbstractBuild.class);
        when(req.hasParameter("id")).thenReturn(true);
        when(req.getParameter("id")).thenReturn("Hash1");
        when(req.findAncestorObject(AbstractBuild.class)).thenReturn(build);
        when(build.getLogFile()).thenReturn(new File("/tmp/logfile.log"));
        return req; 
    }
    private StaplerResponse prepareFakedStaplerResponse() throws IOException {
       return mock(StaplerResponse.class);
    }
    
    @Test
    public void testManualApprovalViaGetRequestSuccessful() throws IOException {
        assertFalse(mCheck1.isApproved()); 
        StaplerRequest req = prepareFakedStaplerRequest();
        StaplerResponse res = prepareFakedStaplerResponse();
        action.doApprove(req, res);
        assertTrue(mCheck1.isApproved()); // Check toggles after reevaluation back to false
    }
    
    @Test
    public void testManualApprovalViaGetRequestWithoutIdParameterDoesNothing() throws IOException {
        assertFalse(mCheck1.isApproved()); 
        StaplerRequest req = prepareFakedStaplerRequest();
        StaplerResponse res = prepareFakedStaplerResponse();
        when(req.hasParameter("id")).thenReturn(false);
        action.doApprove(req, res);
        assertFalse(mCheck1.isApproved()); // Check toggles after reevaluation back to false
    }
    
    @Test
    public void testManualApprovalViaGetRequestOnInexistentCheckDoesNothing() throws IOException {
        assertFalse(mCheck1.isApproved()); 
        StaplerRequest req = prepareFakedStaplerRequest();
        StaplerResponse res = prepareFakedStaplerResponse();
        when(req.getParameter("id")).thenReturn("WrongHash");
        action.doApprove(req, res);
        assertFalse(mCheck1.isApproved()); // Check toggles after reevaluation back to false
    }
    
    @Test
    public void testManualDisapprovalViaGetRequest() throws IOException {
        assertFalse(mCheck1.isApproved()); 
        StaplerRequest req = prepareFakedStaplerRequest();
        StaplerResponse res = prepareFakedStaplerResponse();
        action.doDisapprove(req, res);
        assertTrue(mCheck1.isDisapproved()); // Check toggles after reevaluation back to false
    }

}
