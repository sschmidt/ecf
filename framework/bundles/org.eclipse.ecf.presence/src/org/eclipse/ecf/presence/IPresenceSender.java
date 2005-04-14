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
package org.eclipse.ecf.presence;

import org.eclipse.ecf.core.identity.ID;

public interface IPresenceSender {

	public void sendPresenceUpdate(ID fromID, ID toID, IPresence presence);
	public void sendRosterAdd(ID fromID, String user, String name, String [] groups);
	public void sendRosterRemove(ID fromID, ID userID);
}
