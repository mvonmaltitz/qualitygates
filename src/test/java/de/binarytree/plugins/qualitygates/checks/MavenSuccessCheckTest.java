package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import org.junit.Before;
import org.junit.Test;


public class MavenSuccessCheckTest {

	private MavenSuccessCheck check;
	private AbstractBuild build;
	@Before
	public void setUp(){
		build = mock(AbstractBuild.class, RETURNS_DEEP_STUBS); 
	    check = new MavenSuccessCheck(); 	
	}
	
	@Test
	public void testBuildSuccess(){
	    this.testBuildResult(Result.SUCCESS); 	
	}
	@Test
	public void testBuildIsNull(){
		build = null; 
	    Result result = check.doCheck(build); 
	    assertEquals(Result.FAILURE, result);  
	}
	@Test
	public void testBuildUnstable(){
	    this.testBuildResult(Result.UNSTABLE); 	
	}
	@Test
	public void testBuildFailed(){
	    this.testBuildResult(Result.FAILURE); 	
	}
	
	public void testBuildResult(Result desiredResult) {
		when(build.getResult()).thenReturn(desiredResult); 
	    Result result = check.doCheck(build); 
	    assertEquals(desiredResult, result);  
	}

}
