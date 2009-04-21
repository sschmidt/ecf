/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.discovery;

import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.osgi.services.discovery.ECFServiceEndpointDescription;
import org.eclipse.ecf.osgi.services.discovery.IServicePublication;

public class ECFServiceEndpointDescriptionImpl extends
		ECFServiceEndpointDescription {

	private final ID endpointId;
	private final IServiceID serviceId;
	private ID targetId = null;

	public ECFServiceEndpointDescriptionImpl(IServiceInfo serviceInfo) {
		super(((ServiceProperties) serviceInfo.getServiceProperties())
				.asProperties());
		this.serviceId = serviceInfo.getServiceID();

		// create the endpoint id
		IServiceProperties serviceProperties = serviceInfo
				.getServiceProperties();
		final byte[] endpointBytes = serviceProperties
				.getPropertyBytes(IServicePublication.PROP_KEY_ENDPOINT_CONTAINERID);
		if (endpointBytes == null)
			throw new IDCreateException(
					"ServiceEndpointDescription endpointBytes cannot be null");
		final String endpointStr = new String(endpointBytes);
		final String namespaceStr = serviceProperties
				.getPropertyString(IServicePublication.PROP_KEY_ENDPOINT_CONTAINERID_NAMESPACE);
		if (namespaceStr == null) {
			throw new IDCreateException(
					"ServiceEndpointDescription namespaceStr cannot be null");
		}
		endpointId = IDFactory.getDefault().createID(namespaceStr, endpointStr);

		// create the target id, if it's there
		final byte[] targetBytes = serviceProperties
				.getPropertyBytes(IServicePublication.PROP_KEY_TARGET_CONTAINERID);
		// If this is null, we're ok with it
		if (targetBytes != null) {
			final String targetStr = new String(endpointBytes);
			String targetNamespaceStr = serviceProperties
					.getPropertyString(IServicePublication.PROP_KEY_TARGET_CONTAINERID_NAMESPACE);
			if (targetNamespaceStr == null)
				targetNamespaceStr = namespaceStr;
			targetId = IDFactory.getDefault().createID(targetNamespaceStr,
					targetStr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.osgi.services.discovery.ECFServiceEndpointDescription
	 * #getECFEndpointID()
	 */
	public ID getECFEndpointID() throws IDCreateException {
		return endpointId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.osgi.services.discovery.ECFServiceEndpointDescription
	 * #getECFTargetID()
	 */
	public ID getConnectTargetID() {
		return targetId;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serviceId == null) ? 0 : serviceId.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ECFServiceEndpointDescriptionImpl other = (ECFServiceEndpointDescriptionImpl) obj;
		if (serviceId == null) {
			if (other.serviceId != null)
				return false;
		} else if (!serviceId.equals(other.serviceId))
			return false;
		return true;
	}

	public IServiceID getServiceID() {
		return serviceId;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("ServiceEndpointDescriptionImpl["); //$NON-NLS-1$
		sb.append(";providedinterfaces=").append(getProvidedInterfaces()); //$NON-NLS-1$
		sb.append(";location=").append(getLocation()); //$NON-NLS-1$
		sb.append(";serviceid=").append(getServiceID()); //$NON-NLS-1$
		sb.append(";osgiEndpointID=").append(getEndpointID()); //$NON-NLS-1$
		sb.append(";ecfEndpointID=").append(getECFEndpointID()); //$NON-NLS-1$
		sb.append(";ecfTargetID=").append(getConnectTargetID()); //$NON-NLS-1$
		sb.append(";ecfFilter=").append(getRemoteServicesFilter()); //$NON-NLS-1$
		sb.append(";props=").append(getProperties()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
		return sb.toString();
	}

}
