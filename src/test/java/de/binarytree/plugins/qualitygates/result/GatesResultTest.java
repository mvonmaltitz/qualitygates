package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.checks.GateStep;

public class GatesResultTest {
	private QualityLineReport qualityLineReport;
	private GateReport gateReport;
	private GateReport gateResultTwin;
	private GateReport gateResult1;
	private Gate gate;
	private Gate gate1;

	@Before
	public void setUp() {
		qualityLineReport = new QualityLineReport();
		gate = getGateMockForName("Gate Name");
		gateReport = new GateReport(gate);
		gateResultTwin = new GateReport(gate);
		gate1 = getGateMockForName("Gate Name");
		gateResult1 = new GateReport(gate1);
	}

	private Gate getGateMockForName(String name) {
		Gate gate = mock(Gate.class);
		when(gate.getName()).thenReturn(name);
		return gate;
	}

	@Test
	public void testNewGateResultHasCorrectGateName() {
		assertEquals(gate.getName(), gateReport.getGateName());
	}

	@Test
	public void testAddingAGateIncrementsNumberOfGates() {
		qualityLineReport.addGateReport(gateReport);
		assertEquals(1, qualityLineReport.getNumberOfGates());
		qualityLineReport.addGateReport(gateResult1);
		assertEquals(2, qualityLineReport.getNumberOfGates());
	}

	@Test
	public void testGetAddedGates() {
		qualityLineReport.addGateReport(gateReport);
		qualityLineReport.addGateReport(gateResult1);
		assertTrue(qualityLineReport.getGateReports().contains(gateReport));
		assertTrue(qualityLineReport.getGateReports().contains(gateResult1));
	}

	@Test
	public void testAddAnotherResultForExistingGateResult() {
		this.addResultAndItsTwinToGatesResult();
		assertTrue(qualityLineReport.getGateReports().contains(gateResultTwin));
	}

	@Test
	public void testAnotherResultRemovesExistingResultForSameGate() {
		this.addResultAndItsTwinToGatesResult();
		assertFalse(qualityLineReport.getGateReports().contains(gateReport));
	}

	private void addArbitraryGateResults(QualityLineReport qualityLineReport,
			int count) {
		for (int i = 0; i < count; i++) {
			qualityLineReport.addGateReport(new GateReport(this
					.getGateMockForName("Gate " + i)));
		}
	}

	@Test
	public void testAnotherResultForExistingResultDoesntIncrementGateCount() {
		this.addResultAndItsTwinToGatesResult();
		assertEquals(27, qualityLineReport.getNumberOfGates());
	}

	@Test
	public void testAnotherResultForExistingResultChangeResultOfGate() {
		this.addResultAndItsTwinToGatesResult();
		assertEquals(qualityLineReport.getResultFor(gate), Result.SUCCESS);
	}

	private void addResultAndItsTwinToGatesResult() {
		this.addArbitraryGateResults(qualityLineReport, 7);
		gateReport.setResult(Result.FAILURE);
		qualityLineReport.addGateReport(gateReport);
		this.addArbitraryGateResults(qualityLineReport, 19);
		gateResultTwin.setResult(Result.SUCCESS);
		qualityLineReport.addGateReport(gateResultTwin);
	}

	@Test
	public void testRetrieveResultForGate() {
		gateReport.setResult(Result.FAILURE);
		qualityLineReport.addGateReport(gateReport);
		assertEquals(Result.FAILURE, qualityLineReport.getResultFor(gate));
	}

	@Test
	public void testGetNOTBUILTForUnknownGate() {
		qualityLineReport.addGateReport(gateReport);
		assertEquals(Result.NOT_BUILT, qualityLineReport.getResultFor(gate1));
	}

	@Test
	public void testGetTerminationReasonWhenGateFails() {
		gateReport.setResult(Result.SUCCESS);
		gateResult1.setResult(Result.FAILURE);
		Gate gate3 = getGateMockForName("Gate Name");
		GateReport r3 = new GateReport(gate3);
		r3.setResult(Result.NOT_BUILT);

		GateStepReport check0 = new GateStepReport(getCheckMock("Check0"));
		GateStepReport check1 = new GateStepReport(getCheckMock("Check1"));
		GateStepReport check2 = new GateStepReport(getCheckMock("Check2"));
		GateStepReport check3 = new GateStepReport(getCheckMock("Check3")); // NOT_BUILT
		check0.setResult(Result.SUCCESS, "Zeroth reason");
		check1.setResult(Result.FAILURE, "First reason");
		check2.setResult(Result.FAILURE, "Second reason");

		gateResult1.addStepReport(check1);
		gateResult1.addStepReport(check2);

		r3.addStepReport(check3);

		qualityLineReport.addGateReport(gateReport);
		qualityLineReport.addGateReport(gateResult1);
		qualityLineReport.addGateReport(r3);

		assertEquals(3, qualityLineReport.getNumberOfGates());
		assertEquals(1, qualityLineReport.getNumberOfSuccessfulGates());
		List<String> reasonOfTermination = qualityLineReport
				.getReasonsOfTermination();

		assertTrue(reasonOfTermination.contains("First reason"));
		assertTrue(reasonOfTermination.contains("Second reason"));
		assertFalse(reasonOfTermination.contains("Zeroth reason"));
	}

	@Test
	public void testGetTerminationReaonsWhenAllGatesAreSuccessfull() {
		assertEquals(new LinkedList<String>(),
				qualityLineReport.getReasonsOfTermination());

	}

	public GateStep getCheckMock(String name) {
		GateStep check = mock(GateStep.class, Mockito.RETURNS_DEEP_STUBS);
		when(check.getDescriptor().getDisplayName()).thenReturn(name);
		return check;
	}
}
