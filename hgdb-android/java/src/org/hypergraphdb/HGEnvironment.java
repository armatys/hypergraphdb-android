/* 
 * This file is part of the HyperGraphDB source distribution. This is copyrighted 
 * software. For permitted uses, licensing options and redistribution, please see  
 * the LicensingInformation file at the root level of the distribution.  
 * 
 * Copyright (c) 2005-2010 Kobrix Software, Inc.  All rights reserved. 
 */
package org.hypergraphdb;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hypergraphdb.util.MemoryWarningSystem;

/**
 * <p>
 * This class provides some facilities to manage several open HyperGraph databases
 * within a single virtual machine. This is useful when one needs to access a
 * currently open database by its location.   
 * </p>
 * 
 * <p>
 * The class essentially implements a static map of currently open databases. The
 * general name <code>HGEnvironment</code> reflects the intent to eventually
 * put JVM-wide operations here.  
 * </p>
 * 
 * @author Borislav Iordanov
 */
public class HGEnvironment 
{		
	private static Map<String, HyperGraph> dbs = new HashMap<String, HyperGraph>();
	private static Map<String, HGConfiguration> configs = new HashMap<String, HGConfiguration>();
	private static MemoryWarningSystem memWarning = null;	
	
	synchronized static void set(String location, HyperGraph graph)
	{
		dbs.put(normalize(location), graph);
	}
	
	synchronized static void remove(String location)
	{
		dbs.remove(normalize(location));
	}
	
	static String normalize(String location)
	{
	    File f = new File(location);
	    try
	    {
	        return f.getCanonicalPath();
	    }
	    catch (java.io.IOException ex)
	    {
	        ex.printStackTrace(System.err);
	        return f.getAbsolutePath();
	    }
	}
	
	/**
	 * <p>
	 * Return the singleton {@link MemoryWarningSystem} attached to this
	 * JVM.
	 * </p> 
	 */
	public static synchronized MemoryWarningSystem getMemoryWarningSystem() 
	{
		if (memWarning == null)
		{
			memWarning = new MemoryWarningSystem();
		}
		return memWarning;
	}
	
	/**
	 * <p>
	 * Retrieve an already opened or open a HyperGraph instance. Note that a new
	 * database instance will potentially be created via <code>new HyperGraph(location)</code>
	 * if it doesn't already exist.
	 * </p>
	 * 
	 * @param location The location of the HyperGraph instance.
	 * @return The HyperGraph database instance.
	 */
	public synchronized static HyperGraph get(String location) 
	{ 
		location = normalize(location);
		HyperGraph hg = dbs.get(location);
		if (hg == null)
		{
			hg = new HyperGraph();
			hg.setConfig(getConfiguration(location));
			hg.open(location);
			dbs.put(location, hg);
		}
		else if (!hg.isOpen())
		{
			if (configs.containsKey(location))
				hg.setConfig(configs.get(location));
			hg.open(location);
		}
		return hg;
	}
	
	/**
	 * <p>Retrieve the HyperGraphDB instance at the specified location and open it
	 * (if not already opened) with the given configuration. If the instance has
	 * already been opened, the configuration parameter is ignored.
	 * 
	 * @param location The filesystem path of the database instance.
	 * @param config The set of configuration parameters.
	 * @return
	 */
	public synchronized static HyperGraph get(String location, HGConfiguration config) 
	{ 
		location = normalize(location);
		configs.put(location, config);
		return get(location);
	}
	
	/**
	 * <p>
	 * Same as <code>get</code>, but will return <code>null</code> if there is
	 * no database at that location.
	 * </p>
	 */
	public synchronized static HyperGraph getExistingOnly(String location)
	{
		location = normalize(location);
		HyperGraph hg = dbs.get(location);
		if (hg == null)
		{
			if (exists(location))
				hg = new HyperGraph(location);
		}
		else if (!hg.isOpen())
		{
			if (configs.containsKey(location))
				hg.setConfig(configs.get(location));
			hg.open(location);
		}		
		return hg;
	}
	
	/**
	 * <p>
	 * Return <code>true</code> if there is a HyperGraph database at the given location 
	 * and <code>false</code> otherwise.
	 * </p> 
	 */	
	public synchronized static boolean exists(String location)
	{
		// This filename is pretty unique to HGDB, so if a directory has it, chances
		// are it's a HGDB database.
		String [] testfiles = new String[] {"hgstore_idx_HGATOMTYPE", "je.lck" };
		File dir = new File(location);
		if (!dir.isDirectory())
			return false;
		List<String> all = Arrays.asList(dir.list());
		for (String testfile : testfiles)
			if (new File(dir, testfile).exists() || all.contains(testfile))
				return true;
		return false;
	}
	
	/**
	 * <p>
	 * Return <code>true</code> if the database at the given location is already
	 * open and <code>false</code> otherwise.
	 * </p> 
	 */
	public synchronized static boolean isOpen(String location)
	{
		HyperGraph graph = dbs.get(location);
		return graph != null && graph.isOpen();		
	}
	
	/**
	 * <p>
	 * Configure a HyperGraphDB instance before it is actually opened. If the instance
	 * at that location is already opened, the new configuration will only take effect
	 * if you close and re-open the instance. 
	 * </p>
	 * 
	 * @param location The filesystem path to the database instance.
	 * @param config A <code>HGConfiguration</code> with the desired parameters set.
	 */
	public synchronized static void configure(String location, HGConfiguration config)
	{
		configs.put(location, config);
	}
	
	/**
	 * <p>
	 * Retrieve the configuration of a HyperGraphDB instance. If no configuration was
	 * previously defined, a new one will be created.
	 * </p>
	 * 
	 * @param location The filesystem path to the HyperGraphDB instance.
	 */
	public synchronized static HGConfiguration getConfiguration(String location)
	{
		location = normalize(location);
		HGConfiguration conf = configs.get(location);
		if (conf == null)
		{
			HyperGraph hg = dbs.get(location);
			if (hg != null)
				conf = hg.getConfig();
			else
				conf = new HGConfiguration();
			configs.put(location, conf);
		}
		return conf;
	}
	
	/**
	 * 
	 * <p>
	 * Close all currently open <code>HyperGraph</code> instances. This is generally
	 * done by a HyperGraphDB internal shutdown hook registered with the JVM. But if
	 * you need more control over the shutdown sequence, this method will gracefully
	 * do so. 
	 * </p>
	 *
	 */
	public synchronized static void closeAll()
	{
		for (HyperGraph graph : dbs.values())
		{
			if (graph.isOpen())
				try { graph.close(); } 
				catch (Throwable t) 
				{ 
					System.err.println("Problem closing HyperGraphDB instance at " + 
									   graph.getLocation() + ", stack trace follows...");
					t.printStackTrace(System.err);						
				}
		}		
		dbs.clear();
	}
	
	/**
	 * 
	 * <p>
	 * Disable the HyperGraph JVM shutdown hook. The role of the shutdown hook is the close
	 * all open databases gracefully. If you disable, no such process is triggered upon
	 * JVM shutdown. If you have your own shutdown hook that must take upon that task, you can
	 * call the <code>closeAll</code> method.
	 * </p>
	 *
	 */
	public static void disableShutdownHook()
	{
		Runtime.getRuntime().removeShutdownHook(shutdownHook);
	}
	
	// Try to make sure all HyperGraphs are properly closed during shutdown.
	private static class OnShutdown implements Runnable
	{
		public void run()
		{
			closeAll();
		}
	}	
	
	private static Thread shutdownHook = new Thread(new OnShutdown());
	
	static
	{
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
}
