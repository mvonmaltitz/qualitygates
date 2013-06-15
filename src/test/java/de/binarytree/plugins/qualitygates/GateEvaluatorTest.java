package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.result.GateReport;
import de.binarytree.plugins.qualitygates.result.QualityLineReport;

public class GateEvaluatorTest {

	private int gateCounter;
	private LinkedList<Gate> gateList;
	private BuildListener listener;
	private Launcher launcher;
	private AbstractBuild build;

	@Before
	public void setUp() throws Exception {
		gateCounter = 0;
		gateList = new LinkedList<Gate>();
		build = mock(AbstractBuild.class);
		launcher = mock(Launcher.class);
		listener = mock(BuildListener.class);
	}

	@Test
	public void testOneGateSucessfull() {
		gateList.add(this.getGate(Result.SUCCESS));
		QualityLineReport result = this.evaluate();
		assertEquals(1, result.getGateResults().size());
		assertEquals(Result.SUCCESS, result.getGateResults().get(0).getResult());
	}

	@Test
	public void testExecutionStopsAfterUnbuildableGate() {
		this.addGateSequence(new Result[] { Result.SUCCESS,
				Result.NOT_BUILT, Result.SUCCESS, Result.SUCCESS }); 
		
		QualityLineReport result = this.evaluate();
		
		assertEquals(4, result.getGateResults().size());
		this.assertSequence(result, new Result[] { Result.SUCCESS,
				Result.NOT_BUILT, Result.NOT_BUILT, Result.NOT_BUILT });
	}
	@Test
	public void testExecutionStopsAfterFailure() {
		this.addGateSequence(new Result[] { Result.SUCCESS,
				Result.FAILURE, Result.SUCCESS, Result.SUCCESS }); 
		
		QualityLineReport result = this.evaluate();
		
		assertEquals(4, result.getGateResults().size());
		this.assertSequence(result, new Result[] { Result.SUCCESS,
				Result.FAILURE, Result.NOT_BUILT, Result.NOT_BUILT });
	}

	@Test 
	public void testReevaluationStopsAfterAlreadyReportedFailure(){
		
		this.addGateSequence(new Result[] { Result.SUCCESS,
				Result.FAILURE, Result.SUCCESS, Result.SUCCESS }); 
		GateEvaluator gateEvaluator = new GateEvaluator(this.gateList); 
		QualityLineReport result = gateEvaluator.evaluate(build, launcher, listener); 
		this.assertSequence(result, new Result[] { Result.SUCCESS,
				Result.FAILURE, Result.NOT_BUILT, Result.NOT_BUILT });
		result = gateEvaluator.evaluate(build, launcher, listener); 
		this.assertSequence(result, new Result[] { Result.SUCCESS,
				Result.FAILURE, Result.NOT_BUILT, Result.NOT_BUILT });
		
	}
	   
		
	public void addGateSequence(Result... results) {
		for (int i = 0; i < results.length; i++) {
			gateList.add(this.getGate(results[i]));
		}
	}

	public void assertSequence(QualityLineReport qualityLineReport, Result... results) {
		if (qualityLineReport.getGateResults().size() != results.length) {
			fail("Different length of gates and expected results");
		}
		for (int i = 0; i < results.length; i++) {
			assertEquals(results[i], qualityLineReport.getGateResults().get(i)
					.getResult());
		}

	}

	public QualityLineReport evaluate() {
		return new GateEvaluator(gateList).evaluate(build, launcher, listener);
	}

	public Gate getGate(final Result result) {
		String name = "Gate " + gateCounter++;

		LinkedList<Check> checkList = new LinkedList<Check>();
		return new Gate(name) {
			@Override
			public void doCheck(AbstractBuild build, Launcher launcher,
					BuildListener listener, GateReport gateReport) {
				gateReport.setResult(result);
			}

		};

	}

}
