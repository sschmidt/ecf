/*******************************************************************************
* Copyright (c) 2008 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.core.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;

public class ThreadExecutor implements IExecutor {

	protected Thread thread;

	public ThreadExecutor() {
		// nothing
	}

	public Thread getThread() {
		return thread;
	}

	protected String createThreadName(IProgressRunnable runnable) {
		return NLS.bind("ThreadExecutor for {0}", runnable.toString()); //$NON-NLS-1$
	}

	protected Thread createAndConfigureThread(Runnable runnable, String threadName) {
		Thread t = new Thread(runnable, threadName);
		t.setDaemon(true);
		return t;
	}

	public synchronized IFuture execute(IProgressRunnable runnable, IProgressMonitor monitor) throws IllegalThreadStateException {
		Assert.isNotNull(runnable);
		if (thread != null)
			throw new IllegalThreadStateException("Thread for this executor already created"); //$NON-NLS-1$
		// Now create SingleOperationFuture
		SingleOperationFuture sof = new SingleOperationFuture(monitor);
		// Create and set the thread for this operation
		this.thread = createAndConfigureThread(sof.setter(runnable), createThreadName(runnable));
		// start thread
		this.thread.start();
		return sof;
	}

	public synchronized boolean hasStarted() {
		return (thread != null);
	}

	public synchronized boolean isDone() {
		return (thread == null || !thread.isAlive());
	}
}