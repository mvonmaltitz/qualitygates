package de.binarytree.plugins.qualitygates.checks;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import hudson.Launcher;
import hudson.Launcher.LocalLauncher;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.util.StreamTaskListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.binarytree.plugins.qualitygates.result.CheckResult;

public class ExecutionCheckTest {

	private String command;
	private ExecutionCheck check;
	private CheckResult checkResult;
	private LocalLauncher launcher;
	private ByteArrayOutputStream stream;

	class MockExecutionCheck extends ExecutionCheck {

		public MockExecutionCheck(String command) {
			super(command);
		}

		public DescriptorImpl getDescriptor() {
			return new DescriptorImpl();
		}
	}

	@Before
	public void setUp() throws Exception {
		stream = new ByteArrayOutputStream();
		TaskListener taskListener = new StreamTaskListener(stream);
		launcher = new Launcher.LocalLauncher(taskListener);
		command = "cat /tmp/testdatei";
		check = new MockExecutionCheck(command); 
		checkResult = new CheckResult(check);
	}

	@Test
	public void testSettingAndGettingParameter() {
		assertEquals(command, check.getCommand());
	}

	@Test
	public void testExecutionOfCommand() throws IOException,
			InterruptedException {
		check.doCheck(null, null, launcher, checkResult);
		String logOutput = new String(stream.toByteArray());
		assertEquals(Result.SUCCESS, checkResult.getResult());
		assertTrue(logOutput.contains(command));
	}
	
	@Test
	public void testExecutionOfUnknowCommand(){
		String unknownCommand = "asdlkjfalsdfjaldfjald";
		check = new MockExecutionCheck(unknownCommand); 
		check.doCheck(null, null, launcher, checkResult);
		String logOutput = new String(stream.toByteArray());
		assertEquals(Result.FAILURE, checkResult.getResult());
		assertTrue(logOutput.contains(unknownCommand));
		System.out.println(checkResult.getReason()); 
	}
	@Test
	public void testExecutionOfNonZeroCommand(){
		String command = "cat /aljf/aljf/asdfj/asdfjkdfjdkfj.txt";
		check = new MockExecutionCheck(command); 
		check.doCheck(null, null, launcher, checkResult);
		String logOutput = new String(stream.toByteArray());
		assertEquals(Result.FAILURE, checkResult.getResult());
		assertTrue(logOutput.contains(command));
		System.out.println(checkResult.getReason()); 
	}
	
}
