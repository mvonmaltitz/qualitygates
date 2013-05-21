package de.binarytree.plugins.qualitygates.result;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.QualityGate;
import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.CheckDescriptor;

public class CheckResultTest {

	private Check check = new Check(){
		
		public CheckDescriptor getDescriptor(){
			return new DescriptorImpl(); 
		}
		class DescriptorImpl extends CheckDescriptor{

			@Override
			public String getDisplayName() {
				return "Check Display Name";
			}
			
		}
		@Override
		public void doCheck(AbstractBuild build, BuildListener listener,
				Launcher launcher, CheckResult checkResult) {
		}
		@Override
		public String getDescription() {
			return "Check Description";
		}
		
	};
	private CheckResult checkResult;

	@Before
	public void setUp() {
		checkResult = new CheckResult(check); 
	}

	@Test
	public void testSettingAndGettingOfValidReasonlessResultOfCheck() {
		Result[] results = new Result[] { Result.SUCCESS,
				Result.NOT_BUILT };
		for (Result result : results) {
			checkResult.setResult(result);
			assertEquals(result, checkResult.getResult());
		}
	}
	
	@Test
	public void testSettingAndGettingOfInvalidReasonlessResultOfCheck() {
		Result[] results = new Result[] { Result.FAILURE,
				Result.UNSTABLE };
		for (Result result : results) {
			try{
			checkResult.setResult(result);
			fail("Negative results without reason should be disallowed."); 
			}catch(IllegalArgumentException e){
				assertEquals(Result.NOT_BUILT, checkResult.getResult()); 
				assertNull(checkResult.getReason()); 
			}
		}
	}

	@Test
	public void testSettingAndGettingOfValidReasonfullResultOfCheck() {
		String reason = "It didn't function"; 
		Result[] results = new Result[] { Result.FAILURE,
				Result.UNSTABLE };
		for (Result result : results) {
			checkResult.setResult(result, reason );
			assertEquals(result, checkResult.getResult()); 
			assertEquals(reason, checkResult.getReason()); 
		}
	}
	
	@Test
	public void testGetCheckResultDocumentation(){
	 CheckResult checkResult = check.document(); 	
	 assertEquals(Result.NOT_BUILT, checkResult.getResult()); 
		
	}

}