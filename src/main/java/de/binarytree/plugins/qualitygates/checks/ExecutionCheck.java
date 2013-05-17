package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.Proc;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.CheckResult;

public class ExecutionCheck extends Check {
	private String command;

	@DataBoundConstructor
	public ExecutionCheck(String command) {
		this.command = command;
	}

	public String getCommand() {
		return this.command;
	}

	@Override
	public void doCheck(AbstractBuild build, BuildListener listener,
			Launcher launcher, CheckResult checkResult) {
		ProcStarter procStarter = prepareCommand(launcher);
		try {
			executeProcStarter(launcher, checkResult, procStarter);
		} catch (IOException e) {
			exceptionToCheckResult(checkResult, e);
		} catch (InterruptedException e) {
			exceptionToCheckResult(checkResult, e);
		}

	}

	private void executeProcStarter(Launcher launcher, CheckResult checkResult,
			ProcStarter procStarter) throws IOException, InterruptedException {
		Proc proc = launcher.launch(procStarter);
		int exitCode = proc.join();
		if (exitCode == 0) {
			checkResult.setResult(Result.SUCCESS);
		} else {
			checkResult.setResult(Result.FAILURE,
					"Execution has not been successful. Error code is " + exitCode + ". See log for more information");
		}
	}

	private ProcStarter prepareCommand(Launcher launcher) {
		ProcStarter procStarter = launcher.new ProcStarter();
		procStarter = procStarter.cmdAsSingleString(this.command);
		return procStarter;
	}

	private void exceptionToCheckResult(CheckResult checkResult, Exception e) {
		checkResult.setResult(Result.FAILURE, e.getMessage());
	}
	@Override
	public String getDescription() {
		return "$ " + this.command;  
	}
	
	@Extension
	public static class DescriptorImpl extends CheckDescriptor {
		@Override
		public String getDisplayName() {
			return "Execution of shell script";
		}
	}


}
