/* 
 * This file is part of the HyperGraphDB source distribution. This is copyrighted 
 * software. For permitted uses, licensing options and redistribution, please see  
 * the LicensingInformation file at the root level of the distribution.  
 * 
 * Copyright (c) 2005-2010 Kobrix Software, Inc.  All rights reserved. 
 */
package org.hypergraphdb.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * This memory warning system will call all registered listeners when
 * the method `notifyLowMemory` is called.
 * </p>
 */
public class MemoryWarningSystem 
{
	private static MemoryWarningSystem sInstance;
	private final Collection<Listener> listeners = new ArrayList<Listener>();
	
	public static interface Listener 
	{
		void memoryUsageLow(long usedMemory, long maxMemory);
	}

	public static void notifyLowMemory() {
		if (sInstance != null) {
			for (Listener listener : sInstance.listeners) {
				listener.memoryUsageLow(1, 1);
			}
		}
	}

	public MemoryWarningSystem() 
	{
		sInstance = this;
	}

	public boolean addListener(Listener listener) 
	{
		return listeners.add(listener);
	}

	public boolean removeListener(Listener listener) 
	{
		return listeners.remove(listener);
	}
}
