package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hudson.model.Result;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.binarytree.plugins.qualitygates.Gate;
import de.binarytree.plugins.qualitygates.checks.Check;

public class GateResultTest {

	private GateReport gateReport;
	private Check check;
	private Check check2;
	private CheckReport checkResult1;
	private CheckReport checkResult2;

	@Before
	public void setUp() {
		QualityLineReport qualityLineReport = new QualityLineReport();
		Gate gate = mock(Gate.class);
		gateReport = new GateReport(gate);
		check = mock(Check.class, Mockito.RETURNS_DEEP_STUBS);
		check2 = mock(Check.class, Mockito.RETURNS_DEEP_STUBS);
		setCheckMockName(check, "Check Type One");
		setCheckMockName(check2, "Check Type Two");
		checkResult1 = new CheckReport(check);
		checkResult2 = new CheckReport(check2);
	}

	private void setCheckMockName(Check check, String name) {
		when(check.getDescriptor().getDisplayName()).thenReturn(name);
		when(check.getDescription()).thenReturn("Check String Representation");
	}

	@Test
	public void testNewCheckResultHasCorrectCheckName() {
		assertEquals(checkResult1.getCheckName(), check.getDescriptor()
				.getDisplayName());
		assertEquals(checkResult1.getDescription(), check.getDescription());
	}

	@Test
	public void testAddingAGateIncrementsNumberOfGates() {
		assertEquals(0, gateReport.getNumberOfChecks());
		gateReport.addCheckResult(checkResult1);
		assertEquals(1, gateReport.getNumberOfChecks());
		gateReport.addCheckResult(checkResult2);
		assertEquals(2, gateReport.getNumberOfChecks());
	}

	@Test
	public void testGetAddedChecks() {
		gateReport.addCheckResult(checkResult1);
		gateReport.addCheckResult(checkResult2);
		assertTrue(gateReport.getCheckResults().contains(checkResult1));
		assertTrue(gateReport.getCheckResults().contains(checkResult2));
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
		gateReport.addCheckResult(checkResult1);
		gateReport.addCheckResult(checkResult2);
		CheckReport foundResult = gateReport.getResultFor(check);
		assertEquals(checkResult1, foundResult);
	}
	@Test 
	public void testGetResultForUnavailableCheck(){
		Check check = mock(Check.class); 
		gateReport.addCheckResult(checkResult1);
		gateReport.addCheckResult(checkResult2);
		CheckReport foundResult = gateReport.getResultFor(check); 
		assertNull(foundResult); 
	}
}
