/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Creative Sphere - initial API and implementation
 *
 */
package org.abstracthorizon.danube.http.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link MultiStringMap} implementation using {@link java.util.HashMap}
 * This implementation uses map and is storing multiple elements in a list.
 * If only one element remains in the list
 * then it is replaced with that element only.
 *
 * @author Daniel Sendula
 */
public class MultiStringHashMap implements MultiStringMap {

    /** Backing storage. */
    protected Map<String, Object> map;

    /**
     * Constructor.
     */
    public MultiStringHashMap() {
        map = new HashMap<String, Object>();
    }

    /**
     * Constructor.
     * @param initialCapacity initial capacity of a backing map
     */
    public MultiStringHashMap(int initialCapacity) {
        map = new HashMap<String, Object>(initialCapacity);
    }

    /**
     * Constructor.
     * @param initialCapacity initial capacity of a backing map
     * @param loadFactor load factory of a backing map
     */
    public MultiStringHashMap(int initialCapacity, float loadFactor) {
        map = new HashMap<String, Object>(initialCapacity, loadFactor);
    }

    /**
     * <p>
     *   Adds new element to the map. If no entries with given id exist than this is going to be the first.
     *   If there are already values under given id then this is going to be added as a new one.
     * </p>
     * <p>
     *   This implementation if there is already one value in the backing map converts it to a list and adds both (old and new values) to it.
     * </p>
     * @param id key
     * @param value value to be added
     * @throws IllegalStateException if existing element of the backing storage is not a string or a list
     */
    @SuppressWarnings("unchecked")
    public void add(String id, String value) {
        Object old = map.get(id);
        if (old == null) {
            map.put(id, value);
        } else if (old instanceof String) {
            ArrayList<String> list = new ArrayList<String>();
            list.add((String)old);
            list.add(value);
        } else if (old instanceof List) {
            ((List<String>)old).add(value);
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * <p>Adds all elements from the given array.</p>
     * <p>This implementation is using {@link MultiStringHashMap#addAll(String, Collection)} to add values</p>
     *
     * @param id key
     * @param values array of values
     * @see MultiStringMap#add(String, String)
     */
    public void addAll(String id, String[] values) {
        addAll(id, Arrays.asList(values));
    }

    /**
     * <p>Adds all elements from the given collection</p>
     * <p>This implementation if there is already one value in the backing map converts it to a list and adds both (old and new values) to it.</p>
     *
     * @param id key
     * @param values collection which elements are to be added
     * @throws IllegalStateException if existing element of the backing storage is not a string or a list
     * @see MultiStringMap#add(String, String)
     */
    @SuppressWarnings("unchecked")
    public void addAll(String id, Collection<String> values) {
        Object old = map.get(id);
        if (old == null) {
            ArrayList<String> list = new ArrayList<String>();
            list.addAll(values);
            map.put(id, list);
        } else if (old instanceof String) {
            ArrayList<String> list = new ArrayList<String>();
            list.add((String)old);
            list.addAll(values);
            map.put(id, list);
        } else if (old instanceof List) {
            ((List<String>)old).addAll(values);
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * Replaces existing element(s), if there are any under the given key, with the given value.
     * @param id key
     * @param value value to be put to the map
     */
    public void putOnly(String id, String value) {
        map.put(id, value);
    }

    /**
     * <p>Replaces existing element(s), if there are any under the given key, with the given values.</p>
     * <p>
     *   If array's length is greater then one it stores given array of strings as a list, if arrayls.
     *   If array's length is exactly one then value of it is stored as a string while
     *   if array's length is zero then it is removed from the backing storage.
     * </p>
     *
     * @param id key
     * @param values values to be put to the map
     * @see MultiStringMap#putOnly(String, String)
     */
    public void putAll(String id, String[] values) {
        if (values.length > 1) {
            putAll(id, Arrays.asList(values));
        } else if (values.length == 1) {
            putOnly(id, values[0]);
        } else {
            removeAll(id);
        }
    }

    /**
     * <p>
     *   Replaces existing element(s), if there are any under the given key, with the given values.
     * </p>
     * <p>
     *   If array's length is greater then one it stores given array of strings as a list, if arrayls.
     *   If array's length is exactly one then value of it is stored as a string while
     *   if array's length is zero then it is removed from the backing storage.
     * </p>
     * <p>
     *   Note: This implementation is not ensuring that all collection elements are of {@link java.lang.String} type.
     * </p>
     *
     * @param id key
     * @param values collection of values to be put to the map
     * @see MultiStringMap#putOnly(String, String)
     */
    public void putAll(String id, Collection<String> values) {
        ArrayList<String> list = null;
        if (values instanceof ArrayList) {
            list = (ArrayList<String>)values;
            map.put(id, values);
        } else {
            list = new ArrayList<String>();
            list.addAll(values);
            map.put(id, values);
        }
        int size = list.size();
        if (size == 0) {
            removeAll(id);
        } else if (size == 1) {
            putOnly(id, (String)list.get(0));
        } else {
            map.put(id, list);
        }
    }

    /**
     * Removes all elements from the given key
     * @param id key
     */
    @SuppressWarnings("unchecked")
    public Collection<String> removeAll(String id) {
        Object old = map.remove(id);
        if (old instanceof String) {
            String[] res = new String[1];
            res[0] = (String)old;
            return Collections.unmodifiableList(Arrays.asList(res));
        } else if (old instanceof List) {
            return Collections.unmodifiableList((List<String>)old);
        } else if (old == null) {
            return null;
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * <p>
     *   Removes first element from the given key. If there was only one element then there will be no more elements under
     *   the given key
     * </p>
     * <p>
     *   Implementation ensures that if list is reduced to only one element then that element is stored instead of the list itself.
     * </p>
     * @param id key
     * @throws IllegalStateException if the element of the backing storage is not string nor list or there are no elements to be removed
     */
    @SuppressWarnings("unchecked")
    public String removeFirst(String id) {
        Object old = map.get(id);
        if (old instanceof String) {
            return (String)map.remove(id);
        } else if (old instanceof List) {
            List<String> list = (List<String>)old;
            String oldString = list.remove(0);
            if (list.size() == 1) {
                map.put(id, list.get(0));
            }
            return oldString;
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * <p>
     *   Removes n-th element from the given key.
     * </p>
     * <p>
     *   Implementation ensures that if list is reduced to only one element then that element is stored instead of the list itself.
     * </p>
     * @param id key
     * @param index index of the element to be removed
     * @throws IndexOutOfBoundsException if there are no elements under the given key
     * @throws IllegalStateException if the element of the backing storage is not string nor list or there are no elements to be removed
     */
    @SuppressWarnings("unchecked")
    public String remove(String id, int index) {
        Object old = map.get(id);
        if (old instanceof String) {
            if (index == 0) {
                return (String)map.remove(id);
            } else {
                throw new IndexOutOfBoundsException("Has one element but index is " + index);
            }
        } else if (old instanceof List) {
            List<String> list = (List<String>)old;
            String oldString = list.remove(index);
            if (list.size() == 1) {
                map.put(id, list.get(0));
            }
            return oldString;
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * Returns <code>true</code> if there is at least one entry
     * @param id key
     * @return <code>true</code> if there is at least one entry
     */
    public boolean containsKey(String id) {
        return map.containsKey(id);
    }

    /**
     * Returns number of entries for given key
     * @param id key
     * @return number of entries for given key
     */
    @SuppressWarnings("unchecked")
    public int getEntrySize(String id) {
        Object old = map.get(id);
        if (old instanceof String) {
            return 1;
        } else if (old instanceof List) {
            List<String> list = (List<String>)old;
            return list.size();
        } else if (old == null) {
            return 0;
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * Retrieves element from the given key. Key must have only one element (or none) for this method to work.
     * @param id key of the asked element
     * @return element
     * @throws IllegalStateException if there are more then one element under this key or nor string or list is stored in the backing storage
     */
    @SuppressWarnings("unchecked")
    public String getOnly(String id) {
        Object old = map.get(id);
        if (old instanceof String) {
            return (String)map.get(id);
        } else if (old instanceof List) {
            List<String> list = (List<String>)old;
            int size = list.size();
            if (size == 0) {
                return null;
            } else if (size == 1) {
                return list.get(0);
            } else {
                // TODO need better exception here
                throw new IllegalStateException("Asked key has more then one element");
            }
        } else if (old == null) {
            return null;
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * Retrieves element from the given key. Key must have only one element (or none) for this method to work.
     * @param id key of the asked element
     * @return element
     * @throws IllegalStateException if there are more then one element under this key or nor string or list is stored in the backing storage
     */
    @SuppressWarnings("unchecked")
    public String getFirst(String id) {
        Object old = map.get(id);
        if (old instanceof String) {
            return (String)map.get(id);
        } else if (old instanceof List) {
            List<String> list = (List<String>)old;
            int size = list.size();
            if (size == 0) {
                return null;
            } else {
                return list.get(0);
            }
        } else if (old == null) {
            return null;
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * Returns array of all elements under asked key. If there are no elements then it returns an empty array.
     * @param id key of the asked elements
     * @return an array of strings
     * @throws IllegalStateException if the element of the backing storage is not string nor list or there are no elements to be removed
     */
    @SuppressWarnings("unchecked")
    public String[] getAsArray(String id) {
        Object old = map.get(id);
        if (old == null) {
            return new String[0];
        } else if (old instanceof String) {
            return new String[]{(String)old};
        } else if (old instanceof List) {
            List<String> list = (List<String>)old;
            String[] res = new String[list.size()];
            res = list.toArray(res);
            return res;
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * Returns list of all elements under asked key. If there are no elements then it returns an empty list.
     * @param id key of the asked elements
     * @return a list of strings
     * @throws IllegalStateException if the element of the backing storage is not string nor list or there are no elements to be removed
     */
    @SuppressWarnings("unchecked")
    public List<String> getAsList(String id) {
        Object old = map.get(id);
        if (old == null) {
            return Collections.emptyList();
        } else if (old instanceof String) {
            return Arrays.asList((String)old);
        } else if (old instanceof List) {
            return (List<String>)old;
        } else {
            throw new IllegalStateException("Found wrong type as the maps value; " + old.getClass());
        }
    }

    /**
     * Clears the map
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns key set
     * @return key set
     */
    public Set<String> keySet() {
        return map.keySet();
    }

    /**
     * Returns a map that contains all elements. Where there are more elements per key then implementation is responsiple of
     * returning something sensible like an array of strings or a collection (or a list) whose elements are strings
     * @return a map
     */
    public Map<String, Object> getAsMap() {
        return map;
    }

    /**
     * Returns list of all entries. Entries with the same key will repeat if there are more elements stored under the same key.
     * @return list of all entries
     * @throws IllegalStateException if the element of the backing storage is not string nor list or there are no elements to be removed
     */
    @SuppressWarnings("unchecked")
    public Collection<Map.Entry<String, String>> getAllEntries() {
        ArrayList<Map.Entry<String, String>> all = new ArrayList<Map.Entry<String, String>>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object obj = entry.getValue();
            if (obj instanceof String) {
                InternalMapEntry<String, String> newEntry = new InternalMapEntry<String, String>(entry.getKey(), (String)entry.getValue());
                all.add(newEntry);
                // all.add(entry);
            } else if (obj instanceof List) {
                List<String> list = (List<String>)obj;
                String key = entry.getKey();
                for (String element : list) {
                    InternalMapEntry<String, String> newEntry = new InternalMapEntry<String, String>(key, element);
                    all.add(newEntry);
                }
            } else {
                throw new IllegalStateException("Found wrong type as the maps value; " + obj.getClass());
            }
        }

        return (Collection<Map.Entry<String, String>>)all;
    }

    /**
     * Number of keys in the map. It won't return total number of elements but just number of differnet keys in the map.
     * @return number of keys in the map
     */
    public int size() {
        return map.size();
    }

    /**
     * Map entry for internal use
     */
    protected class InternalMapEntry<K, V> implements Map.Entry<K, V> {

        /** Key */
        protected K key;
        /** Value */
        protected V value;

        /**
         * Constructor
         * @param key key
         * @param value value
         */
        public InternalMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns key
         * @return key
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns value
         * @return value
         */
        public V getValue() {
            return value;
        }

        /**
         * Sets value
         * @param value value
         * @return old value
         */
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

    }

}
