package org.scijava.webitk;

import hudson.Extension;
import hudson.Functions;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.RootAction;
import hudson.model.Descriptor;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

@Extension
public class WebITK extends Plugin implements RootAction, Serializable, Describable<WebITK> {

	private final static Logger LOG = Logger.getLogger(WebITK.class.getName());

	static {
		LOG.info("Hello, this is webitk");
	}

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends Descriptor<WebITK> {

		@Override
		public String getDisplayName() {
			return "webITK";
		}
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

		URL url = getClass().getResource(path);
		LOG.info("Serving " + path + "; " + url);
		response.serveFile(request, url);
	}

	@SuppressWarnings("unchecked")
	public Descriptor<WebITK> getDescriptor() {
		return Jenkins.getInstance().getDescriptorOrDie(getClass());
	}
}
