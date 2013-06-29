package de.binarytree.plugins.qualitygates.checks;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.tasks.Shell;

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
        	failCheckAndlogExceptionInCheckReport(checkReport, e); 
        }

    }

    protected Shell getShell() {
        return new Shell(this.command);

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
