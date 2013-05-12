package de.binarytree.plugins.qualitygates.checks;

import org.kohsuke.stapler.StaplerRequest;

import net.sf.json.JSONObject;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;

public abstract class SonarCheck extends Check {

	@Override
	public abstract Result doCheck(AbstractBuild build);

	public static abstract class SonarCheckDescriptor extends CheckDescriptor {
		
		private String host; 
		private int port; 
		
		
		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			host = formData.getString("host"); 
			port = formData.getInt("port"); 
			save(); 
			return super.configure(req, formData); 
		}
		
		public int getPort(){
			return port; 
		}
			
		public String getHost(){
			return host; 
		}
	}

}
