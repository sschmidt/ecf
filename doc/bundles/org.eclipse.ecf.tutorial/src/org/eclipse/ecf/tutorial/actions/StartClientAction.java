package org.eclipse.ecf.tutorial.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.tutorial.Activator;
import org.eclipse.ecf.tutorial.ExampleClient4;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class StartClientAction implements IWorkbenchWindowActionDelegate {
	IWorkbenchWindow window = null;
	public void run(IAction action) {
		ClientConnectJob job = new ClientConnectJob("ECF connect job");
		job.schedule();
	}
	public void selectionChanged(IAction action, ISelection selection) {
	}
	public void dispose() {
	}
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	public class ClientConnectJob extends Job {
		public ClientConnectJob(String name) {
			super(name);
		}
		public IStatus run(IProgressMonitor pm) {
			try {
				createAndConnectClient();
				return new Status(IStatus.OK, Activator.getDefault()
						.getBundle().getSymbolicName(), 15000, "Connected",
						null);
			} catch (Exception e) {
				return new Status(IStatus.ERROR, Activator.getDefault()
						.getBundle().getSymbolicName(), 15555,
						"Could not connect\n\n" + e.getMessage()
								+ "\nSee stack trace in Error Log", e);
			}
		}
	}
	
	protected void createAndConnectClient() throws ECFException {
		ExampleClient4 client = new ExampleClient4();
		client.createAndConnect();
	}
}
