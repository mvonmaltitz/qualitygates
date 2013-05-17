package de.binarytree.plugins.qualitygates;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*; 

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.result.GateResult;

public class QualityGateTest {

	private QualityGate gate;
	private LinkedList<Check> checkList;

	@Before
	public void setUp() throws Exception {
		Check check1 = mock(Check.class); 
		Check check2 = mock(Check.class); 
		checkList = new LinkedList<Check>(); 
		checkList.add(check1); 
		checkList.add(check2); 
		gate = new QualityGate("Name", checkList){
			@Override
			public void doCheck(AbstractBuild build, Launcher launcher,
					BuildListener listener, GateResult gateResult) {
				// TODO Auto-generated method stub
				
			}

			
		}; 
		
	}

	@Test
	public void testGetDocumentation(){
	 GateResult gateResult = gate.document(); 	
	 assertEquals(Result.NOT_BUILT, gateResult.getResult()); 
	 assertEquals(2, gateResult.getCheckResults().size()); 
	}
	@Test
	public void testNameParam() {
		assertEquals("Name", gate.getName()); 
	}
	
	@Test
	public void testGivenCollectionIsNotDirectlySet() {
		assertNotSame(gate.getChecks(), checkList);  
	}
	
	@Test
	public void testChecksContainOnlyChecksOfCollectionDuringConstruction(){
		assertEquals(gate.getChecks(), checkList); 
	}

}
