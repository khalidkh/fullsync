/*
 * Created on Nov 18, 2004
 */
package net.sourceforge.fullsync.remoteinterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.Profile;

/**
 * @author Michele Aiello
 */
public class RemoteProfileManager {

	private RemoteInterface remoteInterface;

	public RemoteProfileManager(String host, int port) throws MalformedURLException, RemoteException, NotBoundException {
		remoteInterface = (RemoteInterface) Naming.lookup("rmi://"+host+":"+port+"/FullSync");
	}
	
	public Profile getProfile(String name) {
		try {
			Profile remoteprofile = remoteInterface.getProfile(name);
			return remoteprofile;
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Profile[] getProfiles() {
		try {
			Profile[] remoteprofiles = remoteInterface.getProfiles();
			return remoteprofiles;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save(Profile[] profiles) {
		try {
			remoteInterface.save(profiles);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}