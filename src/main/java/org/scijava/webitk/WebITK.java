package org.scijava.webitk;

import hudson.Extension;
import hudson.Functions;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.ItemGroup;
import hudson.model.RootAction;
import hudson.model.TopLevelItem;
import hudson.model.AbstractItem;
import hudson.model.Api;
import hudson.model.Descriptor;
import hudson.model.Job;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
@Extension
public class WebITK extends Plugin implements RootAction, Serializable, Describable<WebITK> {

	private final static Logger LOG = Logger.getLogger(WebITK.class.getName());

static { LOG.info("Hello, this is webitk"); }

	public Api getApi() {
		return new Api(this);
	}

@Exported
public String say(String parameter) {
	return "Hello " + parameter;
}

	public String getDisplayName() {
		return "webITK";
	}

	public String getIconFileName() {
		return Functions.getResourcePath()
				+ "/plugin/webitk/images/webitk.png";
	}

	public String getUrlName() {
		return "plugin/webitk";
	}

	@Override
	public void start() {
		LOG.info("Starting webITK plugin");
	}

	@Override
	public void doDynamic(final StaplerRequest request, final StaplerResponse response)
			throws IOException, ServletException {
		String path = request.getRestOfPath();
		if (path.length() == 0)
			path = "/";

		if (path.indexOf("..") != -1 || path.length() < 1) {
			// don't serve anything other than files in the sub directory.
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		DescriptorImpl descriptor = getDescriptor();
		boolean enabled = descriptor.getEnabled();

		if ("/".equals(path)) {
			LOG.info("Serving info " + path);

			final Jenkins jenkins = Jenkins.getInstance();

			BufferedWriter writer = new BufferedWriter(response.getWriter());
			writer.write("<h1>WebITK</h1>\n");
			writer.write("Enabled: " + enabled + "<br/>\n");
			writer.write("List\n<ul>\n");
			for (final Job job : jenkins.getAllItems(Job.class)) {
				writer.write("<li>" + job.getName() + " (" + job.getFullName() + ")</li>\n");
			}
			writer.write("</ul>\n");
			final TopLevelItem item = jenkins.getItemMap().get("blub123");
			writer.write("got " + item + "<br />\n");
			writer.close();
			return;
		}

		URL url = getClass().getResource(path);
		LOG.info("Serving " + path + "; " + url);
		response.serveFile(request, url);
	}

	public DescriptorImpl getDescriptor() {
DescriptorImpl desc = (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(getClass());
LOG.info("Hello!!! " + desc.getEnabled());
return desc;
		//return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(getClass());
	}

	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public final static class DescriptorImpl extends Descriptor<WebITK> {

		private boolean enabled;

		@Override
		public String getDisplayName() {
			return "WebITK configuration";
		}

		@Override
		public boolean configure(StaplerRequest request, JSONObject formData) throws FormException {
			enabled = formData.getBoolean("enabled");
			save();
			return super.configure(request, formData);
		}

		public boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

}
