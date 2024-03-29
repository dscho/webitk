package org.scijava.webitk;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Job;

public class WebITKJobProperty extends JobProperty<Job<?, ?>> {
static {
	System.err.println("**** We got a job property");
}

	private boolean enabled;

	@DataBoundConstructor
	public WebITKJobProperty(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getEnabled() {
		return enabled;
	}

	@Override
	public void setOwner(Job<?, ?> owner) {
System.err.println("got owner " + owner);
	}

	@Extension
	public final static class DescriptorImpl extends JobPropertyDescriptor {

		@Override
		public String getDisplayName() {
			return "webITK";
		}

		@Override
		public boolean isApplicable(Class<? extends Job> jobType) {
System.err.println("**** I got a " + jobType);
			return true;
		}
	}
}
