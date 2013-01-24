package org.scijava.webitk;

import hudson.Plugin;
import hudson.model.Action;
import hudson.model.AbstractProject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class WebITKJobAction implements Action {

	//private final static Logger LOG = Logger.getLogger(WebITKJobAction.class.getName());

	private final AbstractProject<?, ?> project;

	public WebITKJobAction(final AbstractProject<?, ?> project) {
System.err.println("*** created action for " + project);
		this.project = project;
	}

	public String getDisplayName() {
		return "webITK";
	}

	public String getIconFileName() {
		return "images/webitk.png";
	}

	public String getUrlName() {
		return "webitk";
	}

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

		if ("/".equals(path)) {
			//LOG.info("Serving info " + path);

			//final Jenkins jenkins = Jenkins.getInstance();

			BufferedWriter writer = new BufferedWriter(response.getWriter());
			writer.write("<h1>WebITK</h1>\n");
			/*
			writer.write("List\n<ul>\n");
			for (final Job<?, ?> job : jenkins.getAllItems(Job.class)) {
				writer.write("<li>" + job.getName() + " (" + job.getFullName() + ")</li>\n");
			}
			writer.write("</ul>\n");
			final TopLevelItem item = jenkins.getItemMap().get("blub123");
			writer.write("got " + item + "<br />\n");
			*/
			writer.write(project.getName());
			writer.close();
			return;
		}

		URL url = getClass().getResource(path);
		//LOG.info("Serving " + path + "; " + url);
		response.serveFile(request, url);
	}

}
