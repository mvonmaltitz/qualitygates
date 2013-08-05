package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.PrintStream;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.result.GateStepReport;
import de.binarytree.plugins.qualitygates.result.GateReport;

public class AndGateTest {

    private Result SUCCESS = Result.SUCCESS;
    private Result FAILURE = Result.FAILURE;
    private Result ABORTED = Result.ABORTED;
    private Result UNSTABLE = Result.UNSTABLE;
    private Result NOT_BUILT = Result.NOT_BUILT;

    private AndGate gate;
    private AbstractBuild<?, ?> build;
    private LinkedList<GateStep> checkList;
    private BuildListener listener;
    private GateStepDescriptor descriptor;

    @Before
    public void setUp() throws Exception {
        PrintStream stream = mock(PrintStream.class);

        build = mock(AbstractBuild.class);
        listener = mock(BuildListener.class);
        when(listener.getLogger()).thenReturn(stream);
        checkList = new LinkedList<GateStep>();
        descriptor = mock(GateStepDescriptor.class);
        when(descriptor.getDisplayName()).thenReturn("Mock Check");
    }

    @Test
    public void testGetDisplayName() {
        QualityGateDescriptor descriptor = new AndGate.DescriptorImpl();
        assertTrue(descriptor.getDisplayName().contains("Gate"));
        assertTrue(descriptor.getDisplayName().contains("AND"));
    }

    @Test
    public void testAddCheck() {
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        gate = new AndGate("Eins", checkList);
        assertEquals(1, gate.getNumberOfSteps());
    }

    @Test
    public void testAddTwoChecks() {
        buildGateWithTwoSuccessfullChecks();
        assertEquals(2, gate.getNumberOfSteps());
    }

    

    @Test
    public void testGateIsSuccessfullWhenChecksSuccessfull() {
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        gate = new AndGate("Eins", checkList);
        this.performGateCheckAndExpect(SUCCESS);
    }

    @Test
    public void testGateAbortedWhenOneCheckAborted() {
        checkList.add(this.getCheckMockWithResult(ABORTED));
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        gate = new AndGate("Eins", checkList);
        this.performGateCheckAndExpect(ABORTED);
    }

    @Test
    public void testGateAbortedWhenOneCheckAbortedAndAnotherFailed() {
        checkList.add(this.getCheckMockWithResult(ABORTED));
        checkList.add(this.getCheckMockWithResult(FAILURE));
        gate = new AndGate("Eins", checkList);
        this.performGateCheckAndExpect(ABORTED);
    }

    @Test
    public void testGateFailsWhenOneChecksFails() {
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        checkList.add(this.getCheckMockWithResult(FAILURE));
        gate = new AndGate("Eins", checkList);
        this.performGateCheckAndExpect(FAILURE);
    }

    @Test
    public void testInitializeGateWithNullCollection() {
        AndGate gate = new AndGate("NullGate", null);
        assertEquals(0, gate.getNumberOfSteps());
    }

    @Test
    public void testEmptyGateIsUnstable() {
        gate = new AndGate("NullGate", null);
        this.performGateCheckAndExpect(UNSTABLE);

    }

    public void performGateCheckAndExpect(Result expectedResult) {
        GateReport report = gate.evaluate(build, null, listener);
        assertEquals(expectedResult, report.getResult());
    }

    public GateStep getCheckMockWithResult(Result result) {
        GateStep check = mock(GateStep.class);
        when(check.getDescriptor()).thenReturn(descriptor);
        GateStepReport checkReport = new GateStepReport(check);
        checkReport.setResult(result, "Mock Result");
        when(
                check.step(any(AbstractBuild.class), any(BuildListener.class),
                        any(Launcher.class))).thenReturn(checkReport);
        when(check.createEmptyGateStepReport()).thenReturn(checkReport);
        return check;
    }

    @Test
    public void testGivenCollectionIsNotDirectlySet() {
        buildGateWithTwoSuccessfullChecks();
        assertNotSame(gate.getSteps(), checkList);
        assertEquals(gate.getSteps(), checkList);
    }

    @Test
    public void testGetDocumentation() {
        buildGateWithTwoSuccessfullChecks();
        GateReport gateReport = gate.createEmptyGateReport();
        assertEquals(Result.NOT_BUILT, gateReport.getResult());
        assertEquals(2, gateReport.getStepReports().size());
    }

    private void buildGateWithTwoSuccessfullChecks() {
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        checkList.add(this.getCheckMockWithResult(SUCCESS));
        gate = new AndGate("Eins", checkList);
    }

}
