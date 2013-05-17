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

import de.binarytree.plugins.qualitygates.QualityGate;
import de.binarytree.plugins.qualitygates.checks.Check;

public class GatesResultTest {
	private GatesResult gatesResult;
	private GateResult gateResult;
	private GateResult gateResultTwin;
	private GateResult gateResult1;
	private QualityGate gate;
	private QualityGate gate1;

	@Before
	public void setUp() {
		gatesResult = new GatesResult();
		gate = getGateMockForName("Gate Name");
		gateResult = new GateResult(gate);
		gateResultTwin = new GateResult(gate);
		gate1 = getGateMockForName("Gate Name");
		gateResult1 = new GateResult(gate1);
	}

	private QualityGate getGateMockForName(String name) {
		QualityGate gate = mock(QualityGate.class);
		when(gate.getName()).thenReturn(name);
		return gate;
	}

	@Test
	public void testNewGateResultHasCorrectGateName() {
		assertEquals(gate.getName(), gateResult.getGateName());
	}

	@Test
	public void testAddingAGateIncrementsNumberOfGates() {
		gatesResult.addGateResult(gateResult);
		assertEquals(1, gatesResult.getNumberOfGates());
		gatesResult.addGateResult(gateResult1);
		assertEquals(2, gatesResult.getNumberOfGates());
	}

	@Test
	public void testGetAddedGates() {
		gatesResult.addGateResult(gateResult);
		gatesResult.addGateResult(gateResult1);
		assertTrue(gatesResult.getGateResults().contains(gateResult));
		assertTrue(gatesResult.getGateResults().contains(gateResult1));
	}

	@Test
	public void testAddAnotherResultForExistingGateResult() {
		this.addResultAndItsTwinToGatesResult();
		assertTrue(gatesResult.getGateResults().contains(gateResultTwin));
	}

	@Test
	public void testAnotherResultRemovesExistingResultForSameGate() {
		this.addResultAndItsTwinToGatesResult();
		assertFalse(gatesResult.getGateResults().contains(gateResult));
	}

	private void addArbitraryGateResults(GatesResult gatesResult, int count) {
		for (int i = 0; i < count; i++) {
			gatesResult.addGateResult(new GateResult(this
					.getGateMockForName("Gate " + i)));
		}
	}

	@Test
	public void testAnotherResultForExistingResultDoesntIncrementGateCount() {
		this.addResultAndItsTwinToGatesResult();
		assertEquals(27, gatesResult.getNumberOfGates());
	}

	@Test
	public void testAnotherResultForExistingResultChangeResultOfGate() {
		this.addResultAndItsTwinToGatesResult();
		assertEquals(gatesResult.getResultFor(gate), Result.SUCCESS);
	}

	private void addResultAndItsTwinToGatesResult() {
		this.addArbitraryGateResults(gatesResult, 7);
		gateResult.setResult(Result.FAILURE);
		gatesResult.addGateResult(gateResult);
		this.addArbitraryGateResults(gatesResult, 19);
		gateResultTwin.setResult(Result.SUCCESS);
		gatesResult.addGateResult(gateResultTwin);
	}

	@Test
	public void testRetrieveResultForGate() {
		gateResult.setResult(Result.FAILURE);
		gatesResult.addGateResult(gateResult);
		assertEquals(Result.FAILURE, gatesResult.getResultFor(gate));
	}

	@Test
	public void testGetNOTBUILTForUnknownGate() {
		gatesResult.addGateResult(gateResult);
		assertEquals(Result.NOT_BUILT, gatesResult.getResultFor(gate1));
	}

	@Test
	public void testGetTerminationReasonWhenGateFails() {
		gateResult.setResult(Result.SUCCESS);
		gateResult1.setResult(Result.FAILURE);
		QualityGate gate3 = getGateMockForName("Gate Name");
		GateResult r3 = new GateResult(gate3);
		r3.setResult(Result.NOT_BUILT);

		CheckResult check0 = new CheckResult(getCheckMock("Check0"));
		CheckResult check1 = new CheckResult(getCheckMock("Check1"));
		CheckResult check2 = new CheckResult(getCheckMock("Check2"));
		CheckResult check3 = new CheckResult(getCheckMock("Check3")); //NOT_BUILT
		check0.setResult(Result.SUCCESS, "Zeroth reason");
		check1.setResult(Result.FAILURE, "First reason");
		check2.setResult(Result.FAILURE, "Second reason");

		gateResult1.addCheckResult(check1);
		gateResult1.addCheckResult(check2);
		
		r3.addCheckResult(check3);

		gatesResult.addGateResult(gateResult);
		gatesResult.addGateResult(gateResult1);
		gatesResult.addGateResult(r3);
		
		assertEquals(3, gatesResult.getNumberOfGates());
		assertEquals(1, gatesResult.getNumberOfSuccessfulGates());
		List<String> reasonOfTermination = gatesResult.getReasonsOfTermination();
		
		assertTrue(reasonOfTermination.contains("First reason"));
		assertTrue(reasonOfTermination.contains("Second reason"));
		assertFalse(reasonOfTermination.contains("Zeroth reason"));
	}

	@Test 
	public void testGetTerminationReaonsWhenAllGatesAreSuccessfull(){
		assertEquals(new LinkedList<String>(), gatesResult.getReasonsOfTermination()); 
		
	}
	
	public Check getCheckMock(String name) {
		Check check = mock(Check.class, Mockito.RETURNS_DEEP_STUBS);
		when(check.getDescriptor().getDisplayName()).thenReturn(name);
		return check;
	}
}
