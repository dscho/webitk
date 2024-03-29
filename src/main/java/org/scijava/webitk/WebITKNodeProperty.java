package org.scijava.webitk;

import hudson.Extension;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class WebITKNodeProperty extends NodeProperty<Node> {
	private boolean enabled;

	@DataBoundConstructor
	public WebITKNodeProperty(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Extension
	public static class DescriptorImpl extends NodePropertyDescriptor {

		@Override
		public String getDisplayName() {
			return "WebITK9";
		}

	}
}