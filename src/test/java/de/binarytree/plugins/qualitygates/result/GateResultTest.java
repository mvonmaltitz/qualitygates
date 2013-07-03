package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.checks.GateStep;

public class GateResultTest {

	private GateReport gateReport;
	private GateStep check;
	private GateStep check2;
	private GateStepReport checkResult1;
	private GateStepReport checkResult2;

	@Before
	public void setUp() {
		QualityLineReport qualityLineReport = new QualityLineReport();
		Gate gate = mock(Gate.class);
		gateReport = new GateReport(gate);
		check = mock(GateStep.class, Mockito.RETURNS_DEEP_STUBS);
		check2 = mock(GateStep.class, Mockito.RETURNS_DEEP_STUBS);
		setCheckMockName(check, "Check Type One");
		setCheckMockName(check2, "Check Type Two");
		checkResult1 = new GateStepReport(check);
		checkResult2 = new GateStepReport(check2);
	}

	private void setCheckMockName(GateStep check, String name) {
		when(check.getDescriptor().getDisplayName()).thenReturn(name);
		when(check.getDescription()).thenReturn("Check String Representation");
	}

	@Test
	public void testNewCheckResultHasCorrectCheckName() {
		assertEquals(checkResult1.getStepName(), check.getDescriptor()
				.getDisplayName());
		assertEquals(checkResult1.getDescription(), check.getDescription());
	}

	@Test
	public void testAddingAGateIncrementsNumberOfGates() {
		assertEquals(0, gateReport.getNumberOfSteps());
		gateReport.addStepReport(checkResult1);
		assertEquals(1, gateReport.getNumberOfSteps());
		gateReport.addStepReport(checkResult2);
		assertEquals(2, gateReport.getNumberOfSteps());
	}

	@Test
	public void testGetAddedChecks() {
		gateReport.addStepReport(checkResult1);
		gateReport.addStepReport(checkResult2);
		assertTrue(gateReport.getStepReports().contains(checkResult1));
		assertTrue(gateReport.getStepReports().contains(checkResult2));
	}

	@Test
	public void testSettingAndGettingOfResultOfGate() {
		Result[] results = new Result[] { Result.SUCCESS, Result.FAILURE,
				Result.NOT_BUILT, Result.UNSTABLE };
		for (Result result : results) {
			gateReport.setResult(result);
			assertEquals(result, gateReport.getResult());
		}
	}

	@Test
	public void testDefaultResultOfGateResult() {
		assertEquals(Result.NOT_BUILT, gateReport.getResult());
	}

	@Test
	public void testGetResultForAvailableCheck() {
		gateReport.addStepReport(checkResult1);
		gateReport.addStepReport(checkResult2);
		GateStepReport foundResult = gateReport.getReportFor(check);
		assertEquals(checkResult1, foundResult);
	}

	@Test
	public void testGetResultForUnavailableCheck() {
		GateStep check = mock(GateStep.class);
		gateReport.addStepReport(checkResult1);
		gateReport.addStepReport(checkResult2);
		GateStepReport foundResult = gateReport.getReportFor(check);
		assertNull(foundResult);
	}
}
