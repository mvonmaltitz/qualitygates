package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;

import de.binarytree.plugins.qualitygates.GateEvaluator;
import de.binarytree.plugins.qualitygates.QualityGate;
import de.binarytree.plugins.qualitygates.QualityGateImpl;
import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.ManualCheck;

public class BuildResultActionTest {

	private GateResult gateResult1;
	private GateResult gateResult2;
	private ManualCheck mCheck1;
	private ManualCheck mCheck2;
	private QualityGateImpl gate1;
	private QualityGateImpl gate2;
	private BuildResultAction action;

	private GatesResult gatesResult;
	private CheckResult mCheckResult2;
	private CheckResult mCheckResult1;
	private GateEvaluator gateEvaluator;

	class MockManualCheck extends ManualCheck {

		public String getCurrentUser(){
			return "Current User"; 
		}
		public DescriptorImpl getDescriptor() {
			return new ManualCheck.DescriptorImpl();
		}
	}

	@Before
	public void setUp() throws Exception {
		mCheck1 = new MockManualCheck();
		mCheck2 = new MockManualCheck();
		LinkedList<Check> checks = new LinkedList<Check>();
		checks.add(mCheck1);
		checks.add(mCheck2);
		gate1 = new QualityGateImpl("Gate 1", null);
		gate2 = new QualityGateImpl("Gate 2", checks);

		gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
		gateEvaluator.evaluate(null, null, null);
		action = new BuildResultAction(gateEvaluator);
	}

	private GateEvaluator getGateEvaluatorFromGates(QualityGate... gates) {
		LinkedList<QualityGate> gateList = new LinkedList<QualityGate>();
		for (QualityGate gate : gates) {
			gateList.add(gate);
		}
		gateEvaluator = new GateEvaluator(gateList);
		return gateEvaluator;
	}

	@Test
	public void testGetNextUnbuiltGateUnsuccessfull() {
		gate2 = new QualityGateImpl("Gate 2", null);
		gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
		GateResult gateResult = action.getNextUnbuiltGate(gateEvaluator
				.getLatestResults());
		assertNull(gateResult);
	}

	@Test
	public void testGetNextUnbuiltGateSuccessfull() {
		GateResult gateResult = action.getNextUnbuiltGate(gateEvaluator
				.getLatestResults());
		assertEquals("Gate 2", gateResult.getGateName());
	}

	@Test
	public void testGetNextUnbuiltCheckSuccessfull() {
		GatesResult latestResults = gateEvaluator.getLatestResults(); 
		CheckResult checkResult = action.getNextUnbuiltCheck(latestResults.getGateResultFor(gate2));
		CheckResult manualCheckResult = latestResults.getGateResultFor(gate2).getResultFor(mCheck1); 
		assertEquals(manualCheckResult, checkResult);
		assertEquals(manualCheckResult.getCheck(), mCheck1);
	}

	@Test
	public void testGetNextUnbuiltCheckUnsuccessfull() {
		gate2 = new QualityGateImpl("Gate 2", null);
		gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
		gateEvaluator.evaluate(null, null, null);
		GatesResult latestResults = gateEvaluator.getLatestResults(); 
		CheckResult checkResult = action.getNextUnbuiltCheck(latestResults.getGateResultFor(gate2));
		assertNull(checkResult);
	}

	@Test
	public void testManualApprovalViaGetRequest() throws IOException {

		GatesResult latestResults = gateEvaluator.getLatestResults(); 
		CheckResult checkResultBefore = action.getNextUnbuiltCheck(latestResults.getGateResultFor(gate2));
		
		StaplerRequest req = mock(StaplerRequest.class);
		AbstractBuild build = mock(AbstractBuild.class);

		when(req.findAncestorObject(AbstractBuild.class)).thenReturn(build);
		action.doApprove(req, null);
		CheckResult checkResultAfter = action.getNextUnbuiltCheck(latestResults.getGateResultFor(gate2));
		assertFalse(checkResultAfter.referencesSameCheckAs(checkResultBefore));
		assertFalse(checkResultAfter.references(mCheck1));
		assertTrue(checkResultAfter.references(mCheck2));
	}
}
