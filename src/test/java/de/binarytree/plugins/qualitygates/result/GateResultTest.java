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

	@Before
	public void setUp() {
		GatesResult gatesResult = new GatesResult();
		QualityGate gate = mock(QualityGate.class);
		gateResult = gatesResult.addResultFor(gate);
		check = mock(Check.class, Mockito.RETURNS_DEEP_STUBS);
		when(check.getDescriptor().getDisplayName()).thenReturn("Check Type");
		when(check.toString()).thenReturn("Check String Representation");
	}

	@Test
	public void testNewCheckResultHasCorrectCheckName() {
		CheckResult checkResult = gateResult.addResultFor(check);
		assertEquals(checkResult.getCheckName(), check.getDescriptor()
				.getDisplayName());
		assertEquals(checkResult.getDescription(), check.toString());
	}

	@Test
	public void testAddingAGateIncrementsNumberOfGates() {
		assertEquals(0, gateResult.getNumberOfChecks());
		gateResult.addResultFor(check);
		assertEquals(1, gateResult.getNumberOfChecks());
		gateResult.addResultFor(check);
		assertEquals(2, gateResult.getNumberOfChecks());
	}
	

	@Test 
	public void testSettingAndGettingOfResultOfGate(){
		Result[] results = new Result[]{Result.SUCCESS, Result.FAILURE, Result.NOT_BUILT, Result.UNSTABLE};
		for(Result result : results){
		gateResult.setResult(result); 
	    assertEquals(result, gateResult.getResult()); 	
		}
	}
}
