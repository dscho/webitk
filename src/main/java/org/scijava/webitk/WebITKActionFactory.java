package org.scijava.webitk;

import java.util.Collection;
import java.util.Collections;

import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import hudson.model.AbstractProject;

public class WebITKActionFactory extends TransientProjectActionFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Collection<? extends Action> createFor(AbstractProject project) {
		return Collections.singleton(new WebITKJobAction(project));
	}

}
