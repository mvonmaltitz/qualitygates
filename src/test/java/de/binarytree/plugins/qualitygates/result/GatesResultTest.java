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
import de.binarytree.plugins.qualitygates.checks.Check;

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
		qualityLineReport.addGateResult(gateReport);
		assertEquals(1, qualityLineReport.getNumberOfGates());
		qualityLineReport.addGateResult(gateResult1);
		assertEquals(2, qualityLineReport.getNumberOfGates());
	}

	@Test
	public void testGetAddedGates() {
		qualityLineReport.addGateResult(gateReport);
		qualityLineReport.addGateResult(gateResult1);
		assertTrue(qualityLineReport.getGateResults().contains(gateReport));
		assertTrue(qualityLineReport.getGateResults().contains(gateResult1));
	}

	@Test
	public void testAddAnotherResultForExistingGateResult() {
		this.addResultAndItsTwinToGatesResult();
		assertTrue(qualityLineReport.getGateResults().contains(gateResultTwin));
	}

	@Test
	public void testAnotherResultRemovesExistingResultForSameGate() {
		this.addResultAndItsTwinToGatesResult();
		assertFalse(qualityLineReport.getGateResults().contains(gateReport));
	}

	private void addArbitraryGateResults(QualityLineReport qualityLineReport, int count) {
		for (int i = 0; i < count; i++) {
			qualityLineReport.addGateResult(new GateReport(this
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
		qualityLineReport.addGateResult(gateReport);
		this.addArbitraryGateResults(qualityLineReport, 19);
		gateResultTwin.setResult(Result.SUCCESS);
		qualityLineReport.addGateResult(gateResultTwin);
	}

	@Test
	public void testRetrieveResultForGate() {
		gateReport.setResult(Result.FAILURE);
		qualityLineReport.addGateResult(gateReport);
		assertEquals(Result.FAILURE, qualityLineReport.getResultFor(gate));
	}

	@Test
	public void testGetNOTBUILTForUnknownGate() {
		qualityLineReport.addGateResult(gateReport);
		assertEquals(Result.NOT_BUILT, qualityLineReport.getResultFor(gate1));
	}

	@Test
	public void testGetTerminationReasonWhenGateFails() {
		gateReport.setResult(Result.SUCCESS);
		gateResult1.setResult(Result.FAILURE);
		Gate gate3 = getGateMockForName("Gate Name");
		GateReport r3 = new GateReport(gate3);
		r3.setResult(Result.NOT_BUILT);

		CheckReport check0 = new CheckReport(getCheckMock("Check0"));
		CheckReport check1 = new CheckReport(getCheckMock("Check1"));
		CheckReport check2 = new CheckReport(getCheckMock("Check2"));
		CheckReport check3 = new CheckReport(getCheckMock("Check3")); //NOT_BUILT
		check0.setResult(Result.SUCCESS, "Zeroth reason");
		check1.setResult(Result.FAILURE, "First reason");
		check2.setResult(Result.FAILURE, "Second reason");

		gateResult1.addCheckResult(check1);
		gateResult1.addCheckResult(check2);
		
		r3.addCheckResult(check3);

		qualityLineReport.addGateResult(gateReport);
		qualityLineReport.addGateResult(gateResult1);
		qualityLineReport.addGateResult(r3);
		
		assertEquals(3, qualityLineReport.getNumberOfGates());
		assertEquals(1, qualityLineReport.getNumberOfSuccessfulGates());
		List<String> reasonOfTermination = qualityLineReport.getReasonsOfTermination();
		
		assertTrue(reasonOfTermination.contains("First reason"));
		assertTrue(reasonOfTermination.contains("Second reason"));
		assertFalse(reasonOfTermination.contains("Zeroth reason"));
	}

	@Test 
	public void testGetTerminationReaonsWhenAllGatesAreSuccessfull(){
		assertEquals(new LinkedList<String>(), qualityLineReport.getReasonsOfTermination()); 
		
	}
	
	public Check getCheckMock(String name) {
		Check check = mock(Check.class, Mockito.RETURNS_DEEP_STUBS);
		when(check.getDescriptor().getDisplayName()).thenReturn(name);
		return check;
	}
}
