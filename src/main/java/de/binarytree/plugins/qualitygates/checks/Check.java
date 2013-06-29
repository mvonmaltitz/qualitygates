package de.binarytree.plugins.qualitygates.checks;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;

import java.util.Arrays;

import de.binarytree.plugins.qualitygates.result.CheckReport;

public abstract class Check implements Describable<Check>, ExtensionPoint {

    public CheckReport check(AbstractBuild build, BuildListener listener, Launcher launcher) {
        CheckReport checkReport = this.document();
        try {
            this.doCheck(build, listener, launcher, checkReport);
        } catch (Exception e) {
        	failCheckAndlogExceptionInCheckReport(checkReport, e); 
        }
        return checkReport;
    }

    
    public CheckReport document() {
        CheckReport checkReport = new CheckReport(this);
        return checkReport;
    }


    protected void failCheckAndlogExceptionInCheckReport(CheckReport checkReport, Exception e) {
        checkReport.setResult(Result.FAILURE, e.getMessage() + Arrays.toString(e.getStackTrace()));
    }
    
    public abstract void doCheck(AbstractBuild build, BuildListener listener, Launcher launcher, CheckReport checkReport);

    public abstract String getDescription();

    @Override
    public String toString() {
        return "Check [" + this.getDescriptor().getDisplayName() + "]";
    }

    public CheckDescriptor getDescriptor() {
        return (CheckDescriptor) Hudson.getInstance().getDescriptor(getClass());
    }

    public static DescriptorExtensionList<Check, CheckDescriptor> all() {
        return Hudson.getInstance().<Check, CheckDescriptor> getDescriptorList(Check.class);
    }

}
