/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.xmpp.container;

import java.io.IOException;
import org.eclipse.ecf.core.identity.ID;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;

/**
 * @author slewis
 *
 */
public interface IIMMessageSender {
    
    public void sendMessage(ID target, String message) throws IOException;
    public void sendPresenceUpdate(ID target, Presence presence) throws IOException;
	
    public Roster getRoster() throws IOException;
	public void sendRosterAdd(String user, String name, String [] groups) throws IOException;
	public void sendRosterRemove(String user) throws IOException;
}
