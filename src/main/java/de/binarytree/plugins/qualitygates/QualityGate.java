package de.binarytree.plugins.qualitygates;

import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.binarytree.plugins.qualitygates.checks.Check;
import de.binarytree.plugins.qualitygates.checks.CheckDescriptor;

public abstract class QualityGate implements Describable<QualityGate>, ExtensionPoint {

	protected String name;
	protected List<Check> checks = new LinkedList<Check>();

	public QualityGate(String name, Collection<Check> checks) {
		this.name = name;
		if (checks != null) {
			this.checks.addAll(checks);
		}
	}

	public DescriptorExtensionList<Check,CheckDescriptor> getDescriptors(){
		return Check.all(); 
	}
	public List<Check> getChecks(){
		return this.checks; 
	}
	public int getNumberOfChecks() {
		return this.checks.size();
	}

	public abstract Result doCheck(AbstractBuild build); 
	
	public QualityGateDescriptor getDescriptor(){
		return (QualityGateDescriptor) Hudson.getInstance().getDescriptor(getClass()); 
	}

	public String getName() {
		return this.name;
	}

	public static DescriptorExtensionList<QualityGate, QualityGateDescriptor> all(){
		return Hudson.getInstance().<QualityGate, QualityGateDescriptor>getDescriptorList(QualityGate.class); 
	}
}
