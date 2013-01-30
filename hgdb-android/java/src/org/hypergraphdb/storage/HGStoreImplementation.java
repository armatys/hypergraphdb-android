package org.hypergraphdb.storage;

import java.util.Comparator;
import org.hypergraphdb.HGConfiguration;
import org.hypergraphdb.HGIndex;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HGRandomAccessResult;
import org.hypergraphdb.HGStore;
import org.hypergraphdb.transaction.HGTransactionFactory;

public interface HGStoreImplementation
{
    Object getConfiguration();
    void startup(HGStore store, HGConfiguration configuration);
    void shutdown();
    
    HGTransactionFactory getTransactionFactory();
    
    HGPersistentHandle store(HGPersistentHandle handle, HGPersistentHandle [] link);
    HGPersistentHandle [] getLink(HGPersistentHandle handle);
    void removeLink(HGPersistentHandle handle);
    boolean containsLink(HGPersistentHandle handle);
    
    HGPersistentHandle store(HGPersistentHandle handle, byte [] data);
    byte [] getData(HGPersistentHandle handle);
    void removeData(HGPersistentHandle handle);         
    boolean containsData(HGPersistentHandle handle);
    
    HGRandomAccessResult<HGPersistentHandle> getIncidenceResultSet(HGPersistentHandle handle);
    void removeIncidenceSet(HGPersistentHandle handle);
    long getIncidenceSetCardinality(HGPersistentHandle handle);
    void addIncidenceLink(HGPersistentHandle handle, HGPersistentHandle newLink);
    void removeIncidenceLink(HGPersistentHandle handle, HGPersistentHandle oldLink);
    
    
    <KeyType, ValueType> HGIndex<KeyType, ValueType> getIndex(String name, 
                                                              ByteArrayConverter<KeyType> keyConverter, 
                                                              ByteArrayConverter<ValueType> valueConverter,
                                                              Comparator<?> comparator,
                                                              boolean isBidirectional,
                                                              boolean createIfNecessary);
    void removeIndex(String name);    
}