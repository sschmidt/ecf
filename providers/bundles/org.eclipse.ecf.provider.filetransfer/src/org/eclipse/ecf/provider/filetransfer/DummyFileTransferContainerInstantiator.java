package org.eclipse.ecf.provider.filetransfer;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.provider.IContainerInstantiator;

public class DummyFileTransferContainerInstantiator implements
		IContainerInstantiator {

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] args) throws ContainerCreateException {
		throw new ContainerCreateException("can't create an instance of dummy");
	}

	public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
		return null;
	}

	public Class[][] getSupportedParameterTypes(
			ContainerTypeDescription description) {
		return null;
	}

}
