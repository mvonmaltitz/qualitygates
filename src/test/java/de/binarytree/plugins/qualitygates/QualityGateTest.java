package de.binarytree.plugins.qualitygates;

import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.util.LinkedList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*; 

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.checks.Check;

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
			public Result doCheck(AbstractBuild build) {
				return null;
			}
			
		}; 
		
	}

	@Test
	public void testNameParam() {
		assertEquals("Name", gate.getName()); 
	}
	
	@Test
	public void testGivenCollectionIsNotDirectlySet() {
		assertFalse(gate.getChecks() == checkList); 
	}
	
	@Test
	public void testChecksContainOnlyChecksOfCollectionDuringConstruction(){
		assertEquals(gate.getChecks(), checkList); 
	}

}
