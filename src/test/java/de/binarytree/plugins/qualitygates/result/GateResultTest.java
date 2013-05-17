package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hudson.model.Result;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.binarytree.plugins.qualitygates.QualityGate;
import de.binarytree.plugins.qualitygates.checks.Check;

public class GateResultTest {

	private GateResult gateResult;
	private Check check;
	private Check check2;
	private CheckResult checkResult1;
	private CheckResult checkResult2;

	@Before
	public void setUp() {
		GatesResult gatesResult = new GatesResult();
		QualityGate gate = mock(QualityGate.class);
		gateResult = new GateResult(gate);
		check = mock(Check.class, Mockito.RETURNS_DEEP_STUBS);
		check2 = mock(Check.class, Mockito.RETURNS_DEEP_STUBS);
		setCheckMockName(check, "Check Type One");
		setCheckMockName(check2, "Check Type Two");
		checkResult1 = new CheckResult(check);
		checkResult2 = new CheckResult(check2);
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
		assertEquals(0, gateResult.getNumberOfChecks());
		gateResult.addCheckResult(checkResult1);
		assertEquals(1, gateResult.getNumberOfChecks());
		gateResult.addCheckResult(checkResult2);
		assertEquals(2, gateResult.getNumberOfChecks());
	}

	@Test
	public void testGetAddedChecks() {
		gateResult.addCheckResult(checkResult1);
		gateResult.addCheckResult(checkResult2);
		assertTrue(gateResult.getCheckResults().contains(checkResult1));
		assertTrue(gateResult.getCheckResults().contains(checkResult2));
	}

	@Test
	public void testSettingAndGettingOfResultOfGate() {
		Result[] results = new Result[] { Result.SUCCESS, Result.FAILURE,
				Result.NOT_BUILT, Result.UNSTABLE };
		for (Result result : results) {
			gateResult.setResult(result);
			assertEquals(result, gateResult.getResult());
		}
	}

	@Test
	public void testDefaultResultOfGateResult() {
		assertEquals(Result.NOT_BUILT, gateResult.getResult());
	}
}
