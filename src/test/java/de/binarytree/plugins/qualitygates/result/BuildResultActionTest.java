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

import de.binarytree.plugins.qualitygates.AndGate;
import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.GateEvaluator;
import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.FailingCheck;
import de.binarytree.plugins.qualitygates.checks.ManualCheck;

public class BuildResultActionTest{


	private MockManualCheck mCheck1;

	private MockManualCheck mCheck2;

	private AndGate gate1;

	private AndGate gate2;

	private BuildResultAction action;

	private GateEvaluator gateEvaluator;

	class MockManualCheck extends ManualCheck{

		public MockManualCheck(String hash){
			this.hash = hash;

		}

		public String getCurrentUser(){
			return "Current User";
		}

		public void setHash(String hash){
			this.hash = hash;

		}

		@Override
		public DescriptorImpl getDescriptor(){
			return new ManualCheck.DescriptorImpl();
		}
	}

	@Before
	public void setUp() throws Exception{
		mCheck1 = new MockManualCheck("Hash1");
		mCheck2 = new MockManualCheck("Hash2");
		final Launcher launcher = mock(Launcher.class);
		LinkedList<Check> checks = new LinkedList<Check>();
		checks.add(mCheck1);
		checks.add(mCheck2);
		gate1 = new AndGate("Gate 1", null);
		gate2 = new AndGate("Gate 2", checks);

		gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
		gateEvaluator.evaluate(null, null, null);
		action = new BuildResultAction(gateEvaluator){

			@Override
			public Launcher getLauncher(BuildListener listener){
				return launcher;

			}
		};
		mCheck1.setHash("Hash1");
		mCheck2.setHash("Hash2");
	}

	private GateEvaluator getGateEvaluatorFromGates(Gate... gates){
		LinkedList<Gate> gateList = new LinkedList<Gate>();
		for(Gate gate: gates){
			gateList.add(gate);
		}
		gateEvaluator = new GateEvaluator(gateList);
		return gateEvaluator;
	}

	@Test
	public void testGetNextUnbuiltGateUnsuccessfullAsSecondGateFails(){
		Check check = new FailingCheck(){
			public DescriptorImpl getDescriptor(){
				return new FailingCheck.DescriptorImpl(); 
			}
		}; 
		List<Check> checks = new LinkedList<Check>(); 
		checks.add(check); 
		gate2 = new AndGate("Gate 2", checks);
		gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
		gateEvaluator.evaluate(null, null, null);
		GateReport gateReport = action.getNextUnbuiltGate(gateEvaluator.getLatestResults());
		assertNull(gateReport);
	}
	@Test
	public void testGetNextUnbuiltGateUnsuccessfull(){
		gate2 = new AndGate("Gate 2", null);
		gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
		GateReport gateReport = action.getNextUnbuiltGate(gateEvaluator.getLatestResults());
		assertNull(gateReport);
	}

	@Test
	public void testGetNextUnbuiltGateSuccessfull(){
		GateReport gateReport = action.getNextUnbuiltGate(gateEvaluator.getLatestResults());
		assertEquals("Gate 2", gateReport.getGateName());
	}

	@Test
	public void testGetNextUnbuiltCheckSuccessfull(){
		QualityLineReport latestResults = gateEvaluator.getLatestResults();
		CheckReport checkReport = action.getNextUnbuiltCheck(latestResults.getGateResultFor(gate2));
		CheckReport manualCheckReport = latestResults.getGateResultFor(gate2).getResultFor(mCheck1);
		assertEquals(manualCheckReport, checkReport);
		assertEquals(manualCheckReport.getCheck(), mCheck1);
	}

	@Test
	public void testGetNextUnbuiltCheckUnsuccessfull(){
		gate2 = new AndGate("Gate 2", null);
		gateEvaluator = getGateEvaluatorFromGates(gate1, gate2);
		gateEvaluator.evaluate(null, null, null);
		QualityLineReport latestResults = gateEvaluator.getLatestResults();
		CheckReport checkReport = action.getNextUnbuiltCheck(latestResults.getGateResultFor(gate2));
		assertNull(checkReport);
	}

	@Test
	public void testManualApprovalViaGetRequest() throws IOException{

		QualityLineReport latestResults = gateEvaluator.getLatestResults();
		CheckReport checkResultBefore = action.getNextUnbuiltCheck(latestResults.getGateResultFor(gate2));

		fakeStaplerRequest();
		
		CheckReport checkResultAfter = action.getNextUnbuiltCheck(latestResults.getGateResultFor(gate2));
		assertFalse(checkResultAfter.referencesSameCheckAs(checkResultBefore));
		assertFalse(checkResultAfter.references(mCheck1));
		assertTrue(checkResultAfter.references(mCheck2));
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
}
