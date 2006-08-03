/****************************************************************************
* Copyright (c) 2004 Composent, Inc. and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Composent, Inc. - initial API and implementation
*****************************************************************************/

package org.eclipse.ecf.remoteservice;

import org.eclipse.ecf.core.util.ECFException;

/**
 * Interface providing runtime access to a remote service.  An instance
 * implementing this interface will be returned from the IRemoteServiceContainer.getRemoteService(IRemoteServiceReference)
 * and may then be used to communicate with a remote service.  The methods on this interface support accessing
 * the remote service in several ways:<br/>
 * <ul>
 * <li>callSynch -- A synchronous invocation that will block the calling thread until complete (or timeout) and return the
 * result from the remote or throw exception if remote invocation fails or throws exception</li>
 * <li>callAsynch -- An asynchronous invocation that will not block the caller thread but rather notify the given listener
 * asynchronously when complete (via timeout, exception, or successful completion)</li>
 * <li>fireAsynch -- An asynchronous invocation that will simply execute the remote method asynchronously, but will not provide any
 * response or remote method failure information</li>
 * <li>getProxy -- Access to a local proxy for the remote service that will expose the appropriate interface to the caller, and synchronously
 * call the remote methods when invoked.  For example:</li>
 * <pre>
 * IRemoteServiceReference [] references = serviceContainer.getRemoteServiceReferences(null,"java.lang.Runnable",null);
 * IRemoteService remoteService = references[0].getRemoteService(references[0]);
 * Runnable runnable = (Runnable) remoteService.getProxy();
 * runnable.run();
 * </pre>
 * 
 */
public interface IRemoteService {
	/**
	 * Call remote method specified by call parameter synchronously.
	 * @param call the remote call to make
	 * @return Object the result of the call.  Will be null if remote provides null as result.
	 * @throws ECFException thrown if disconnect occurs, caller not currently connected, or remote throws Exception
	 */
	public Object callSynch(IRemoteCall call) throws ECFException;
	/**
	 * Call remote method specified by call parameter asynchronously, and notify specified listener when call starts 
	 * and completes.
	 * @param call the remote call to make
	 * @param listener the listener to notify when call starts and is completed.  The listener will be notified
	 * via the two event types IRemoteCallStartEvent and IRemoteCallCompleteEvent.
	 * 
	 * @see org.eclipse.ecf.remoteservice.events.IRemoteCallStartEvent
	 * @see org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent
	 */
	public void callAsynch(IRemoteCall call, IRemoteCallListener listener);
	/**
	 * Fire remote method specified by call parameter.  The remote method will be invoked as a result of
	 * asynchronous message send, but no failure/exception information will be returned, and no result
	 * will be returned
	 * @param call the remote call to make
	 * @throws ECFException if caller not currently connected
	 */
	public void fireAsynch(IRemoteCall call) throws ECFException;
	/**
	 * Get local proxy for remote interface.  The local proxy may then be used to make remote method calls transparently
	 * by invoking the local proxy method
	 * @return Object that implements the interface specified in the IRemoteServiceReference instance used to retrieve the
	 * IRemoteService object.  The result may then be cast to the appropriate type.  The getProxy() contract guarantees that
	 * if a non-null Object is returned, that it will implement the interface specified for the IRemoteServiceReference.
	 * @throws ECFException if not currently connected to remote service
	 */
	public Object getProxy() throws ECFException;
}
