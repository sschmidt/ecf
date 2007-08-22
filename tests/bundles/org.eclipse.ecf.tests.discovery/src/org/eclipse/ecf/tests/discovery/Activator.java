package org.eclipse.ecf.tests.discovery;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.discovery.service.IDiscoveryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.tests.discovery";

	// The shared instance
	private static Activator plugin;
	
	private ServiceTracker tracker;
	
	private ServiceRegistration discoveryRegistration;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		plugin = this;
		IContainer container = ContainerFactory.getDefault().createContainer(
		"ecf.discovery.jmdns");
		container.connect(null, null);
		discoveryRegistration = context.registerService(IDiscoveryService.class.getName(), container, null);
		tracker = new ServiceTracker(context,IDiscoveryService.class.getName(),null);
		tracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (tracker != null) {
			tracker.close();
			tracker = null;
		}
		if (discoveryRegistration != null) {
			discoveryRegistration.unregister();
			discoveryRegistration = null;
		}
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * 
	 */
	public IDiscoveryService getDiscoveryService() {
		return (IDiscoveryService) tracker.getService();
	}

}
