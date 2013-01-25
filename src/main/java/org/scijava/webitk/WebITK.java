package org.scijava.webitk;

import hudson.Extension;
import hudson.Functions;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Label;
import hudson.model.ParametersAction;
import hudson.model.RootAction;
import hudson.model.TopLevelItem;
import hudson.model.AbstractProject;
import hudson.model.Api;
import hudson.model.Cause;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Node;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.queue.SubTask;
import hudson.remoting.Callable;
import hudson.slaves.NodeProperty;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

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
				+ "/webitk/images/webitk.png";
	}

	public String getUrlName() {
		return "webitk";
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
			for (final Job<?, ?> job : jenkins.getAllItems(Job.class)) {
				writer.write("<li>" + job.getName() + " (" + job.getFullName() + ")</li>\n");
			}
			writer.write("</ul>\n");
			final TopLevelItem item = jenkins.getItemMap().get("blub123");
			writer.write("got " + item + "<br />\n");
			writer.write("Nodes<br /><ul>\n");
			for (final Node node : jenkins.getNodes()) {
				writer.write("<li>node " + node.getDisplayName());
				for (final NodeProperty<?> property : node.getNodeProperties()) {
					writer.write("<li>node property " + property.toString() + "; " + property.getClass());
					if (property instanceof WebITKNodeProperty) {
						writer.write("<li> yesss " + ((WebITKNodeProperty)property).getEnabled());
					} else {
						writer.write("<li> no " + WebITKNodeProperty.class + " != " + property.getClass() + ", loaders: " + WebITKNodeProperty.class.getClassLoader() + " != " + property.getClass().getClassLoader());
					}
				}
				WebITKNodeProperty nodeProperty = node.getNodeProperties().get(WebITKNodeProperty.class);
				if (nodeProperty != null) {
					writer.write("<li>itk " + nodeProperty.getEnabled());
				}
				else writer.write("<li>no itk property");
			}
			writer.write("</ul>");
			/*
			Node node = jenkins.getNode("master");
			writer.write("node " + node.getDisplayName());
			for (final NodeProperty<?> property : node.getNodeProperties()) {
				writer.write(" node property " + property.toString() + "; " + property.getClass());
			}
			*/
			writer.write("Computers<br /><ul>");
			for (Computer computer : jenkins.getComputers()) {
				writer.write("<li>computer " + computer.getDisplayName() + ", " + computer.getName() + "; " + getOSAndArch(computer));
				WebITKNodeProperty nodeProperty = computer.getNode().getNodeProperties().get(WebITKNodeProperty.class);
				if (nodeProperty != null) {
					writer.write("<li> itk " + nodeProperty.getEnabled());
				}
			}
			writer.write("</ul>");
			writer.close();
			return;
		}

		final String[] split = path.split("/");
		if (split.length > 3 && "".equals(split[0])) {
			String jobName = split[1];
System.err.println("job: " + jobName);
			final Jenkins jenkins = Jenkins.getInstance();
			final TopLevelItem item = jenkins.getItemMap().get(jobName);
			if (item != null && item instanceof AbstractProject) {
				AbstractProject<?, ?> project = (AbstractProject<?, ?>)item;
				Writer writer = response.getWriter();
				writer.write("job " + jobName + "; class " + project.getClass());
				project.scheduleBuild2(0, new Cause.RemoteCause(request.getRemoteHost(), "webITK: " + path), new LabelAssignmentAction() {
					
					public String getUrlName() {
						return null;
					}
					
					public String getIconFileName() {
						return null;
					}
					
					public String getDisplayName() {
						return null;
					}
					
					public Label getAssignedLabel(SubTask arg0) {
						/*
						Computer computer = jenkins.getComputer(split[2]);
						if (computer == null) {
							Computer[] list = jenkins.getComputers();
System.err.println("Could not find computer '" + split[2]);
for (Computer computer2 : list) System.err.println("computer: " + computer2.getName());
						}
						Node node = computer == null ? null : computer.getNode();
						if (node != null) {
							return node.getSelfLabel();
						}
						*/
						return jenkins.getLabel(split[2]);
					}
				});
				writer.close();
			}
			return;
		}
		URL url = getClass().getResource(path);
		LOG.info("Serving " + path + "; " + url);
		response.serveFile(request, url);
	}

	private static String getOSAndArch(final Computer computer) {
		try {
			return computer.getChannel().call(new Callable<String, RuntimeException>() {
				public String call() {
					String os = System.getProperty("os.name");
					if (os == null) os = "(null)";
					String arch = System.getProperty("os.arch");
					if (arch == null) arch = "(null)";
					return os + "/" + arch;
				}
			});
		} catch (Throwable t) {
t.printStackTrace();
			return null;
		}
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
