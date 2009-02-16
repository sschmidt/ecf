/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *    Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - Reworked completely
 *****************************************************************************/

package org.eclipse.ecf.tests.discovery;

import java.net.URI;
import java.util.Comparator;
import java.util.Properties;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.discovery.IDiscoveryContainerAdapter;
import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.ServiceProperties;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;
import org.eclipse.ecf.tests.discovery.listener.TestServiceListener;
import org.eclipse.ecf.tests.discovery.listener.TestServiceTypeListener;

public abstract class DiscoveryTest extends AbstractDiscoveryTest {

	protected IServiceInfo serviceInfo;
	protected IServiceInfo serviceInfo2;
	protected IServiceInfo serviceInfo3;

	protected String containerUnderTest;
	protected long waitTimeForProvider = 1000;
	protected Comparator comparator = new ServiceInfoComparator();
	private String protocol = PROTOCOL;
	private String scope = SCOPE;
	private String namingAuthority = NAMINGAUTHORITY;
	
	protected IContainer container = null;
	protected IDiscoveryContainerAdapter discoveryContainer = null;

	protected int eventsToExpect = 1;
	
	public DiscoveryTest(String name) {
		super();
		this.containerUnderTest = name;
	}
	
	protected IDiscoveryContainerAdapter getAdapter(Class clazz) {
		final IDiscoveryContainerAdapter adapter = (IDiscoveryContainerAdapter) container.getAdapter(clazz);
		assertNotNull("Adapter must not be null", adapter);
		return adapter;
	}
	
	protected IContainer getContainer(String containerUnderTest) throws ContainerCreateException {
		return ContainerFactory.getDefault().createContainer(containerUnderTest);
	}

	protected IServiceID createServiceID(String serviceType, String serviceName) throws Exception {
		return ServiceIDFactory.getDefault().createServiceID(discoveryContainer.getServicesNamespace(), serviceType, serviceName);
	}

	protected void registerService(IServiceInfo serviceInfo) throws Exception {
		assertNotNull(serviceInfo);
		assertNotNull(discoveryContainer);
		discoveryContainer.registerService(serviceInfo);
	}

	protected void unregisterService(IServiceInfo serviceInfo) throws Exception {
		assertNotNull(serviceInfo);
		assertNotNull(discoveryContainer);
		discoveryContainer.unregisterService(serviceInfo);
	}

	protected void setWaitTimeForProvider(long aWaitTimeForProvider) {
		this.waitTimeForProvider = aWaitTimeForProvider + (aWaitTimeForProvider * 1 / 2);
	}

	protected void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	protected void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	protected void setScope(String scope) {
		this.scope = scope;
	}

	protected void setNamingAuthority(String namingAuthority) {
		this.namingAuthority = namingAuthority;
	}

	protected String getServiceType() {
		return "_" + SERVICES[0] + "._" + SERVICES[1] + "._" + SERVICES[2] + "._" + protocol + "." + scope + "._" + namingAuthority;
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(containerUnderTest);
		assertTrue(containerUnderTest.startsWith("ecf.discovery."));

		container = getContainer(containerUnderTest);
		discoveryContainer = getAdapter(IDiscoveryContainerAdapter.class);

		assertNotNull(container);
		assertNotNull(discoveryContainer);

		final Properties props = new Properties();
		final URI uri = createDefaultURI();

		IServiceID serviceID = (IServiceID) IDFactory.getDefault().createID(discoveryContainer.getServicesNamespace(), new Object[] {getServiceType(), getHost()});
		assertNotNull(serviceID);
		final ServiceProperties serviceProperties = new ServiceProperties(props);
		serviceProperties.setPropertyString(DiscoveryTest.class.getName() + "servicePropertiesString", "serviceProperties");
		serviceProperties.setProperty(DiscoveryTest.class.getName() + "servicePropertiesIntegerMax", new Integer(Integer.MIN_VALUE));
		serviceProperties.setProperty(DiscoveryTest.class.getName() + "servicePropertiesIntegerMin", new Integer(Integer.MAX_VALUE));
		serviceProperties.setProperty(DiscoveryTest.class.getName() + "servicePropertiesBoolean", new Boolean(false));
		serviceProperties.setPropertyBytes(DiscoveryTest.class.getName() + "servicePropertiesByte", new byte[]{-127, -126, -125, 0, 1, 2, 3, 'a', 'b', 'c', 'd', 126, 127});
		serviceInfo = new ServiceInfo(uri, serviceID, 1, 1, serviceProperties);
		assertNotNull(serviceInfo);

		IServiceID serviceID2 = (IServiceID) IDFactory.getDefault().createID(discoveryContainer.getServicesNamespace(), new Object[] {"_service._ecf._tests2._fooProtocol.fooScope._fooNA", getHost()});
		assertNotNull(serviceID);
		final ServiceProperties serviceProperties2 = new ServiceProperties(props);
		serviceProperties2.setPropertyString("serviceProperties2", "serviceProperties2");
		serviceInfo2 = new ServiceInfo(uri, serviceID2, 2, 2, serviceProperties2);
		assertNotNull(serviceInfo2);

		IServiceID serviceID3 = (IServiceID) IDFactory.getDefault().createID(discoveryContainer.getServicesNamespace(), new Object[] {"_service._ecf._tests3._barProtocol.barScope._barNA", getHost()});
		assertNotNull(serviceID);
		final ServiceProperties serviceProperties3 = new ServiceProperties(props);
		serviceProperties3.setPropertyString("serviceProperties3", "serviceProperties3");
		serviceInfo3 = new ServiceInfo(uri, serviceID3, 3, 3, serviceProperties3);
		assertNotNull(serviceInfo3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		discoveryContainer.unregisterService(serviceInfo);
		discoveryContainer = null;
		container.disconnect();
		container.dispose();
		container = null;
	}

	protected void registerService() {
		try {
			discoveryContainer.registerService(serviceInfo);
		} catch (final ECFException e) {
			fail("IServiceInfo may be valid with this IDCA");
		}
	}

	protected void unregisterService() {
		try {
			discoveryContainer.unregisterService(serviceInfo);
		} catch (final ECFException e) {
			fail("unregistering of " + serviceInfo + " should just work");
		}
	}

	public void testConnect() {
		assertNull(container.getConnectedID());
		try {
			container.connect(null, null);
		} catch (final ContainerConnectException e) {
			fail("connect may not fail the first time");
		}
		assertNotNull(container.getConnectedID());
	}

	public void testConnectTwoTimes() {
		testConnect();
		try {
			container.connect(null, null);
		} catch (final ContainerConnectException e) {
			return;
		}
		fail("succeeding connects should fail");
	}

	public void testDisconnect() {
		testConnect();
		container.disconnect();
		assertNull(container.getConnectedID());
	}

	public void testReconnect() {
		testDisconnect();
		testConnect();
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)}.
	 */
	public void testGetServiceInfo() {
		testConnect();
		registerService();
		final IServiceInfo info = discoveryContainer.getServiceInfo(serviceInfo.getServiceID());
		assertTrue("IServiceInfo should match, expected:\n" + serviceInfo + " but:\n" + info, comparator.compare(info, serviceInfo) == 0);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceInfo(org.eclipse.ecf.discovery.identity.IServiceID)}.
	 */
	public void testGetServiceInfoWithNull() {
		try {
			discoveryContainer.getServiceInfo(null);
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null argument is not allowed in api");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServiceTypes()}.
	 */
	public void testGetServiceTypes() {
		testConnect();
		registerService();
		final IServiceTypeID[] serviceTypeIDs = discoveryContainer.getServiceTypes();
		assertTrue(serviceTypeIDs.length > 0);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices()}.
	 */
	public void testGetServices() {
		testConnect();
		registerService();
		final IServiceInfo[] services = discoveryContainer.getServices();
		assertTrue(services.length >= 1);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)}.
	 */
	public void testGetServicesIServiceTypeID() {
		testConnect();
		registerService();
		final IServiceInfo serviceInfos[] = discoveryContainer.getServices(serviceInfo.getServiceID().getServiceTypeID());
		assertTrue(serviceInfos.length > 0);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServices(org.eclipse.ecf.discovery.identity.IServiceTypeID)}.
	 */
	public void testGetServicesIServiceTypeIDWithNull() {
		try {
			discoveryContainer.getServices(null);
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null argument is not allowed in api");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#registerService(org.eclipse.ecf.discovery.IServiceInfo)}.
	 */
	public void testRegisterService() {
		testConnect();
		registerService();
		final IServiceInfo[] services = discoveryContainer.getServices();
		assertTrue(services.length >= 1);
		for (int i = 0; i < services.length; i++) {
			final IServiceInfo service = services[i];
			if (comparator.compare(service, serviceInfo) == 0) {
				return;
			}
		}
		fail("Self registered service not found");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#registerService(org.eclipse.ecf.discovery.IServiceInfo)}.
	 */
	public void testRegisterServiceWithNull() {
		testConnect();
		try {
			discoveryContainer.registerService(null);
		} catch (final ECFException e) {
			fail("null must cause AssertionFailedException");
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null must cause AssertionFailedException");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)}.
	 */
	public void testUnregisterService() {
		testRegisterService();
		unregisterService();
		final IServiceInfo[] services = discoveryContainer.getServices();
		for (int i = 0; i < services.length; i++) {
			final IServiceInfo service = services[i];
			if (comparator.compare(service, serviceInfo) == 0) {
				fail("Expected service to be not registered anymore");
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#unregisterService(org.eclipse.ecf.discovery.IServiceInfo)}.
	 */
	public void testUnregisterServiceWithNull() {
		testConnect();
		try {
			discoveryContainer.unregisterService(null);
		} catch (final ECFException e) {
			fail("null must cause AssertionFailedException");
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null must cause AssertionFailedException");
	}

	public void testDispose() {
		testConnect();
		container.dispose();
		assertNull(container.getConnectedID());
		try {
			container.connect(null, null);
		} catch (final ContainerConnectException e) {
			return;
		}
		fail("A disposed container must not be reusable");
	}

	protected void addServiceListener(TestServiceListener serviceListener) {
		discoveryContainer.addServiceListener(serviceListener);
		addListenerRegisterAndWait(serviceListener, serviceInfo);
		discoveryContainer.removeServiceListener(serviceListener);
		assertNotNull("Test listener didn't receive discovery", serviceListener.getEvent());
		assertEquals("Test listener received more than expected discovery event", eventsToExpect, serviceListener.getEventCount());
		assertTrue("Container mismatch", serviceListener.getEvent().getLocalContainerID().equals(container.getConnectedID()));
		assertTrue("IServiceInfo mismatch", comparator.compare(((IServiceEvent) serviceListener.getEvent()).getServiceInfo(), serviceInfo) == 0);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.IServiceListener)}.
	 */
	public void testAddServiceListenerIServiceListener() {
		testConnect();
		assertTrue("No Services must be registerd at this point", discoveryContainer.getServices().length == 0);
		final TestServiceListener tsl = new TestServiceListener(eventsToExpect);
		addServiceListener(tsl);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.IServiceListener)}.
	 */
	public void testAddServiceListenerIServiceListenerWithNull() {
		try {
			discoveryContainer.addServiceListener(null);
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null argument is not allowed in api");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)}.
	 */
	public void testAddServiceListenerIServiceTypeIDIServiceListener() {
		testConnect();
		assertTrue("No Services must be registerd at this point", discoveryContainer.getServices().length == 0);

		final TestServiceListener tsl = new TestServiceListener(eventsToExpect);
		discoveryContainer.addServiceListener(serviceInfo.getServiceID().getServiceTypeID(), tsl);
		addListenerRegisterAndWait(tsl, serviceInfo);
		discoveryContainer.removeServiceListener(serviceInfo.getServiceID().getServiceTypeID(), tsl);
		
		assertNotNull("Test listener didn't receive discovery", tsl.getEvent());
		assertEquals("Test listener received more than expected discovery event", eventsToExpect, tsl.getEventCount());
		assertTrue("Container mismatch", tsl.getEvent().getLocalContainerID().equals(container.getConnectedID()));
		assertTrue("IServiceInfo mismatch", comparator.compare(((IServiceEvent) tsl.getEvent()).getServiceInfo(), serviceInfo) == 0);
	}

	private void addListenerRegisterAndWait(TestServiceListener testServiceListener, IServiceInfo aServiceInfo) {
		synchronized (testServiceListener) {
			// register a service which we expect the test listener to get notified of
			registerService();
			try {
				testServiceListener.wait(waitTimeForProvider);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("Some discovery unrelated threading issues?");
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)}.
	 */
	public void testAddServiceListenerIServiceTypeIDIServiceListenerWithNull() {
		try {
			discoveryContainer.addServiceListener(null, null);
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null argument is not allowed in api");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)}.
	 */
	public void testAddServiceTypeListener() {
		testConnect();
		assertTrue("No Services must be registerd at this point", discoveryContainer.getServices().length == 0);

		final TestServiceTypeListener testTypeListener = new TestServiceTypeListener(eventsToExpect);
		discoveryContainer.addServiceTypeListener(testTypeListener);

		synchronized (testTypeListener) {
			// register a service which we expect the test listener to get notified of
			registerService();
			try {
				testTypeListener.wait(waitTimeForProvider);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("Some discovery unrelated threading issues?");
			}
		}
		
		discoveryContainer.removeServiceTypeListener(testTypeListener);
		
		assertNotNull("Test listener didn't receive discovery", testTypeListener.getEvent());
		assertEquals("Test listener received more than expected discovery event", eventsToExpect, testTypeListener.getEventCount());
		assertTrue("Container mismatch", testTypeListener.getEvent().getLocalContainerID().equals(container.getConnectedID()));
	}
	

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#addServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)}.
	 */
	public void testAddServiceTypeListenerWithNull() {
		try {
			discoveryContainer.addServiceTypeListener(null);
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null argument is not allowed in api");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.core.IContainer#getConnectNamespace()}.
	 */
	public void testGetConnectNamespace() {
		testConnect();
		assertNotNull(container.getConnectNamespace());
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.core.IContainer#getID()}.
	 */
	public void testGetID() {
		testConnect();
		assertNotNull(container.getID());
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#getServicesNamespace()}.
	 */
	public void testGetServicesNamespace() {
		testConnect();
		final Namespace namespace = discoveryContainer.getServicesNamespace();
		assertNotNull(namespace);
		final IServiceID serviceID = ServiceIDFactory.getDefault().createServiceID(namespace, serviceInfo.getServiceID().getServiceTypeID());
		assertNotNull("It must be possible to obtain a IServiceID", serviceID);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.IServiceListener)}.
	 */
	public void testRemoveServiceListenerIServiceListener() {
		testConnect();
		final TestServiceListener serviceListener = new TestServiceListener(eventsToExpect);
		addServiceListener(serviceListener);
		discoveryContainer.removeServiceListener(serviceListener);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.IServiceListener)}.
	 */
	public void testRemoveServiceListenerIServiceListenerWithNull() {
		try {
			discoveryContainer.removeServiceListener(null);
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null argument is not allowed in api");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)}.
	 */
	public void testRemoveServiceListenerIServiceTypeIDIServiceListener() {
		testConnect();
		final TestServiceListener serviceListener = new TestServiceListener(eventsToExpect);
		addServiceListener(serviceListener);
		discoveryContainer.removeServiceListener(serviceInfo.getServiceID().getServiceTypeID(), serviceListener);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceListener(org.eclipse.ecf.discovery.identity.IServiceTypeID, org.eclipse.ecf.discovery.IServiceListener)}.
	 */
	public void testRemoveServiceListenerIServiceTypeIDIServiceListenerWithNull() {
		try {
			discoveryContainer.removeServiceListener(null, null);
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null argument is not allowed in api");
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)}.
	 */
	public void testRemoveServiceTypeListener() {
		testConnect();
		assertTrue("No Services must be registerd at this point", discoveryContainer.getServices().length == 0);

		final TestServiceTypeListener testTypeListener = new TestServiceTypeListener(eventsToExpect);
		discoveryContainer.addServiceTypeListener(testTypeListener);

		synchronized (testTypeListener) {
			// register a service which we expect the test listener to get notified of
			registerService();
			try {
				testTypeListener.wait(waitTimeForProvider);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("Some discovery unrelated threading issues?");
			}
		}
		
		discoveryContainer.removeServiceTypeListener(testTypeListener);
		
		assertNotNull("Test listener didn't receive discovery", testTypeListener.getEvent());
		assertEquals("Test listener received more than expected discovery event", eventsToExpect, testTypeListener.getEventCount());
		assertTrue("Container mismatch", testTypeListener.getEvent().getLocalContainerID().equals(container.getConnectedID()));
		
		//TODO reregister and verify the listener doesn't receive any events any longer.
	}

	/**
	 * Test method for
	 * {@link org.eclipse.ecf.discovery.IDiscoveryContainerAdapter#removeServiceTypeListener(org.eclipse.ecf.discovery.IServiceTypeListener)}.
	 */
	public void testRemoveServiceTypeListenerWithNull() {
		try {
			discoveryContainer.removeServiceTypeListener(null);
		} catch (final AssertionFailedException e) {
			return;
		}
		fail("null argument is not allowed in api");
	}

}
