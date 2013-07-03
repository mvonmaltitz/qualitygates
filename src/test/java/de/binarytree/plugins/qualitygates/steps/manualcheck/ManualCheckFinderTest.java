package de.binarytree.plugins.qualitygates.steps.manualcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.AndGate;
import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.GateStep;
import de.binarytree.plugins.qualitygates.QualityLineEvaluator;
import de.binarytree.plugins.qualitygates.result.GateReport;
import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.result.QualityLineReport;
import de.binarytree.plugins.qualitygates.steps.FailingCheck;
import de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheck;
import de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheckFinder;
import de.binarytree.plugins.qualitygates.steps.manualcheck.ManualCheckFinder.ManualCheckManipulator;

public class ManualCheckFinderTest {
    private MockManualCheck mCheck1;
    private MockManualCheck mCheck2;
    private AndGate gate1;
    private AndGate gate2;
    private QualityLineEvaluator gateEvaluator;
    private ManualCheckFinder finder;

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
        LinkedList<GateStep> checks = new LinkedList<GateStep>();
        checks.add(mCheck1);
        checks.add(mCheck2);
        gate1 = new AndGate("Gate 1", null);
        gate2 = new AndGate("Gate 2", checks);

        reevaluateObjects();
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

    @Test
    public void testGetNextUnbuiltGateUnsuccessfullAsSecondGateFails() {
        GateStep check = new FailingCheck() {
            public DescriptorImpl getDescriptor() {
                return new FailingCheck.DescriptorImpl();
            }
        };
        List<GateStep> checks = new LinkedList<GateStep>();
        checks.add(check);
        gate2 = new AndGate("Gate 2", checks);
        reevaluateObjects();
        GateReport gateReport = finder.getNextUnbuiltGate(gateEvaluator
                .getLatestResults());
        assertNull(gateReport);
    }

    private void reevaluateObjects() {
        gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
        gateEvaluator.evaluate(null, null, null);
        finder = new ManualCheckFinder(gateEvaluator.getLatestResults());
    }

    private void createEmptyLine() {
        gate2 = new AndGate("Gate 2", null);
        reevaluateObjects();
    }

    @Test
    public void testGetNextUnbuiltGateUnsuccessfull() {
        createEmptyLine();
        GateReport gateReport = finder.getNextUnbuiltGate(gateEvaluator
                .getLatestResults());
        assertNull(gateReport);
    }

    @Test
    public void testGetNullManipulatorWhenNoGateFound() {
        createEmptyLine();
        GateReport gateReport = finder.getNextUnbuiltGate(gateEvaluator
                .getLatestResults());
        ManualCheckManipulator manipulator = finder
                .findCheckForGivenHash("Hash1");
        assertFalse(manipulator.hasItem());
    }

    @Test
    public void testGetNextUnbuiltGateSuccessfull() {
        GateReport gateReport = finder.getNextUnbuiltGate(gateEvaluator
                .getLatestResults());
        assertEquals("Gate 2", gateReport.getGateName());
    }

    @Test
    public void testGetNextUnbuiltStepSuccessfull() {
        QualityLineReport latestResults = gateEvaluator.getLatestResults();
        GateStepReport checkReport = finder.getNextUnbuiltStep(latestResults
                .getGateReportFor(gate2));
        GateStepReport manualCheckReport = latestResults
                .getGateReportFor(gate2).getReportFor(mCheck1);
        assertEquals(manualCheckReport, checkReport);
        assertEquals(manualCheckReport.getStep(), mCheck1);
    }

    @Test
    public void testGetNonNullManipulatorForValidHash() {
        ManualCheckManipulator manipulator = finder
                .findCheckForGivenHash("Hash1");
        assertTrue(manipulator.hasItem());
        assertFalse(mCheck1.isApproved());
        manipulator.approve();
        assertTrue(mCheck1.isApproved());
    }

    @Test
    public void testGetNullManipulatorForInvalidHash() {
        ManualCheckManipulator manipulator = finder
                .findCheckForGivenHash("Hash2");
        assertFalse(manipulator.hasItem());
    }

    @Test
    public void testGetNullManipulatorForInvalidObject() {
        GateStep step = mock(GateStep.class);
        when(step.getDisplayName()).thenReturn("FakeStep");
        GateStepReport report = new GateStepReport(step);
        when(
                step.step(any(AbstractBuild.class), any(BuildListener.class),
                        any(Launcher.class))).thenReturn(report);
        report.setResult(Result.NOT_BUILT);
        LinkedList<GateStep> steps = new LinkedList<GateStep>();
        steps.add(step);
        gate2 = new AndGate("Gate 2", steps);
        reevaluateObjects();
        ManualCheckManipulator manipulator = finder
                .findCheckForGivenHash("Hash1");
        assertFalse(manipulator.hasItem());
    }

    @Test
    public void testGetNextUnbuiltStepUnsuccessfull() {
        createEmptyLine();
        gateEvaluator.evaluate(null, null, null);
        QualityLineReport latestResults = gateEvaluator.getLatestResults();
        GateStepReport checkReport = finder.getNextUnbuiltStep(latestResults
                .getGateReportFor(gate2));
        assertNull(checkReport);
    }

}
