package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;

import java.util.ArrayList;
import java.util.List;

import de.binarytree.plugins.qualitygates.checks.Check;

public abstract class QualityGate implements Describable<QualityGate>, ExtensionPoint {

	public QualityGateDescriptor getDescriptor(){
		return (QualityGateDescriptor) Hudson.getInstance().getDescriptor(getClass()); 
	}

	public static DescriptorExtensionList<QualityGate, QualityGateDescriptor> all(){
		return Hudson.getInstance().<QualityGate, QualityGateDescriptor>getDescriptorList(QualityGate.class); 
	}
}
