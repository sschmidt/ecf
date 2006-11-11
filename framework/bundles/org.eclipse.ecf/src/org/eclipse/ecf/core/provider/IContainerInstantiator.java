/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.provider;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.IContainer;

/**
 * Interface that must be implemented by extensions of the containerFactory
 * extension point
 * 
 */
public interface IContainerInstantiator {
	/**
	 * Create instance of IContainer. This is the interface that container
	 * provider implementations must implement for the containerFactory
	 * extension point. The caller may optionally specify both argument types
	 * and arguments that will be passed into this method (and therefore to the
	 * provider implementation implementing this method). For example:
	 * <p>
	 * </p>
	 * <p>
	 * <b> ContainerFactory.getDefault().createContainer("foocontainer",new
	 * Object { "hello" });</b>
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * @param description
	 *            the ContainerTypeDescription associated with the registered
	 *            container provider implementation
	 * @param parameters
	 *            parameters specified by the caller. May be null if no
	 *            parameters are passed in by caller to
	 *            ContainerFactory.getDefault().createContainer(...)
	 * @return IContainer instance. The provider implementation must return a
	 *         valid object implementing IContainer OR throw a
	 *         ContainerCreateException. Null will not be returned.
	 * @throws ContainerCreateException
	 */
	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException;

	/**
	 * Get array of supported adapters for the given container type description.
	 * Providers can implement this method to allow clients to inspect the
	 * adapter types implemented by the container described by the given
	 * description.  
	 * 
	 * Note that the returned types do not guarantee that
	 * a subsequent call to {@link IContainer#getAdapter(Class)} with the same type name
	 * as a returned value will return a non-null result. 
	 * <code>IContainer.getAdapter</code> may still return <code>null</code>.
	 * 
	 * This method should not return
	 * null, but rather an empty String[] if no adapters are exposed.
	 * 
	 * @param description
	 *            the ContainerTypeDescription to report adapters for
	 * @return String[] of supported adapter types.  If no
	 *         adapters supported, will return null
	 */
	public String[] getSupportedAdapterTypes(ContainerTypeDescription description);

	/**
	 * Get array of parameter types for given container type description.
	 * Providers can implement this method to allow clients to inspect the
	 * available set of parameter types understood for calls to
	 * {@link #createInstance(ContainerTypeDescription, Object[])}. Each of the
	 * rows of the returned array specifies a Class[] of parameter types. These
	 * parameter types correspond to the types of Object[] that can be passed
	 * into the second parameter of
	 * {@link #createInstance(ContainerTypeDescription, Object[])}. For
	 * example, if this method returns a Class [] = {{ String.class,
	 * String.class }} this indicates that a call to
	 * createInstance(description,new String[] { "hello", "there" }) will be
	 * understood by the provider implementation.
	 * 
	 * @param description
	 *            the ContainerTypeDescription to return parameter types for
	 * @return Class[][] array of Class[]s. Each row in the table corresponds to
	 *         a Class[] that describes the types of Objects in Object[] for
	 *         second parameter to
	 *         {@link #createInstance(ContainerTypeDescription, Object[])}.  Null may be returned.
	 */
	public Class[][] getSupportedParameterTypes(
			ContainerTypeDescription description);

}