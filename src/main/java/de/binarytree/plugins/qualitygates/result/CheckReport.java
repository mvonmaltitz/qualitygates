package de.binarytree.plugins.qualitygates.result;

import hudson.model.Result;
import de.binarytree.plugins.qualitygates.checks.Check;

public class CheckReport {

	private String checkName;
	private String description;
	private Result result = Result.NOT_BUILT;
	private String reason;
	private Check check;

	public CheckReport(Check check) {
		this.check = check; 
		this.checkName = check.getDescriptor().getDisplayName();
		this.description = check.getDescription();
	}

	public String getDescription() {
		return this.description;
	}

	public String getCheckName() {
		return this.checkName;
	}

	public void setResult(Result result) {
		if (Result.FAILURE.equals(result) || Result.UNSTABLE.equals(result)) {
			throw new IllegalArgumentException("Negative results need a reason");
		}
		this.result = result;
	}

	public void setResult(Result result, String reason) {
		// It's okay to have positive results and a reason, even if not
		// necessary
		this.result = result;
		this.reason = reason;
	}

	public Result getResult() {
		return this.result;
	}

	public String getReason() {
		return this.reason; 
	}

	public boolean referencesSameCheckAs(CheckReport b) {
		return this.check == b.check; 
	}

	public boolean references(Check check) {
		return this.check.equals(check); 
	}
	public Check getCheck(){
		return this.check; 
	}
}