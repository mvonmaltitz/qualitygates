package de.binarytree.plugins.qualitygates;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import hudson.model.AbstractBuild;
import hudson.model.Result;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.Check;

public class QualityGateTest {

	private Result SUCCESS = Result.SUCCESS; 
	private Result FAILURE = Result.FAILURE; 
	private Result ABORTED = Result.ABORTED; 
	private Result UNSTABLE = Result.UNSTABLE; 
	private Result NOT_BUILT = Result.NOT_BUILT; 
	
	private QualityGateImpl gate;
	private AbstractBuild build;

	@Before
	public void setUp() throws Exception {
		gate = new QualityGateImpl(); 
		build = mock(AbstractBuild.class); 
	}

	@Test
	public void testAddCheck() {
		gate.addCheck(this.getCheckMockWithResult(SUCCESS)); 
	    assertEquals(1, gate.getNumberOfChecks()); 
	}
	
	@Test
	public void testAddTwoChecks(){
		gate.addCheck(this.getCheckMockWithResult(SUCCESS)); 
		gate.addCheck(this.getCheckMockWithResult(SUCCESS)); 
	    assertEquals(2, gate.getNumberOfChecks()); 
	}

	@Test
	public void testGateIsSuccessfullWhenChecksSuccessfull(){
		gate.addCheck(this.getCheckMockWithResult(SUCCESS)); 
		gate.addCheck(this.getCheckMockWithResult(SUCCESS)); 
		this.performGateCheckAndExpect(Result.SUCCESS); 
	}
	
	@Test
	public void testGateAbortedWhenOneCheckAborted(){
		gate.addCheck(this.getCheckMockWithResult(ABORTED)); 
		gate.addCheck(this.getCheckMockWithResult(SUCCESS)); 
		this.performGateCheckAndExpect(Result.ABORTED); 
	}
	@Test
	public void testGateAbortedWhenOneCheckAbortedAndAnotherFailed(){
		gate.addCheck(this.getCheckMockWithResult(ABORTED)); 
		gate.addCheck(this.getCheckMockWithResult(FAILURE)); 
		this.performGateCheckAndExpect(ABORTED); 
	}
	
	@Test
	public void testGateFailsWhenOneChecksFails(){
		gate.addCheck(this.getCheckMockWithResult(Result.SUCCESS)); 
		gate.addCheck(this.getCheckMockWithResult(FAILURE)); 
		this.performGateCheckAndExpect(Result.FAILURE); 
	}
	
	public void performGateCheckAndExpect(Result expectedResult){
		Result result =  gate.doCheck(build);
		assertEquals(expectedResult, result); 
	}
		
	public Check getCheckMockWithResult(Result result){
		Check check = mock(Check.class); 
		when(check.doCheck(any(AbstractBuild.class))).thenReturn(result); 
		return check; 
	}
}
