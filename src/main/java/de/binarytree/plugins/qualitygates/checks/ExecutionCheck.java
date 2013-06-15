package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.Proc;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.tasks.Shell;

import java.io.IOException;
import java.util.Arrays;

import org.kohsuke.stapler.DataBoundConstructor;

import de.binarytree.plugins.qualitygates.result.CheckReport;

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
    public void doCheck(AbstractBuild build, BuildListener listener, Launcher launcher, CheckReport checkReport) {
        Shell shell = this.getShell();
        try {
            boolean success = shell.perform(build, launcher, listener);
            if (success) {
                checkReport.setResult(Result.SUCCESS);
            } else {
                checkReport.setResult(Result.FAILURE, shell.getCommand()
                        + " could not be performed. Check log for details.");
            }
        } catch (InterruptedException e) {
            checkReport.setResult(Result.FAILURE, Arrays.toString(e.getStackTrace()));
        }

    }

    protected Shell getShell() {
        return new Shell(this.command);

    }

    // @Override
    // public void doCheck(AbstractBuild build, BuildListener listener, Launcher launcher, CheckResult checkResult) {
    // ProcStarter procStarter = prepareCommand(launcher, build);
    // try {
    // executeProcStarter(launcher, checkResult, procStarter);
    // } catch (IOException e) {
    // exceptionToCheckResult(checkResult, e);
    // } catch (InterruptedException e) {
    // exceptionToCheckResult(checkResult, e);
    // }
    //
    // }

    private void executeProcStarter(Launcher launcher, CheckReport checkReport, ProcStarter procStarter)
            throws IOException, InterruptedException {
        Proc proc = launcher.launch(procStarter);
        int exitCode = proc.join();
        if (exitCode == 0) {
            checkReport.setResult(Result.SUCCESS);
        } else {
            checkReport.setResult(Result.FAILURE, "Execution has not been successful. Error code is " + exitCode
                    + ". See log for more information");
        }
    }

    private ProcStarter prepareCommand(Launcher launcher, AbstractBuild build) {
        ProcStarter procStarter = launcher.new ProcStarter();
        procStarter = procStarter.cmdAsSingleString(this.command);
        procStarter.envs(build.getBuildVariables());
        return procStarter;
    }

    private void exceptionToCheckResult(CheckReport checkReport, Exception e) {
        checkReport.setResult(Result.FAILURE, e.getMessage());
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
