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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is interface defining a map that can, for a value, have
 * one or more strings. It is used for request or response headers
 * or request parameters.
 *
 *
 * @author Daniel Sendula
 */
public interface MultiStringMap {

    /**
     * Adds new element to the map. If no entries with given id exist than this is going to be the first.
     * If there are already values under given id then this is going to be added as a new one
     * @param id key
     * @param value value to be added
     */
    void add(String id, String value);

    /**
     * Adds all elements from the given array.
     * @param id key
     * @param values array of values
     * @see MultiStringMap#add(String, String)
     */
    void addAll(String id, String[] values);

    /**
     * Adds all elements from the given collection
     * @param id key
     * @param values collection which elements are to be added
     * @see MultiStringMap#add(String, String)
     */
    void addAll(String id, Collection<String> values);

    /**
     * Replaces existing element(s), if there are any under the given key, with the given value.
     * @param id key
     * @param value value to be put to the map
     */
    void putOnly(String id, String value);

    /**
     * Replaces existing element(s), if there are any under the given key, with the given values.
     * @param id key
     * @param values values to be put to the map
     * @see MultiStringMap#putOnly(String, String)
     */
    void putAll(String id, String[] values);

    /**
     * Replaces existing element(s), if there are any under the given key, with the given values.
     * @param id key
     * @param values collection of values to be put to the map
     * @see MultiStringMap#putOnly(String, String)
     */
    void putAll(String id, Collection<String> values);

    /**
     * Removes all elements from the given key
     * @param id key
     */
    Collection<String> removeAll(String id);

    /**
     * Removes first element from the given key. If there was only one element then there will be no more elements under
     * the given key
     * @param id key
     */
    String removeFirst(String id);

    /**
     * Removes n-th element from the given key.
     * @param id key
     * @param index index of the element to be removed
     * @throws IndexOutOfBoundsException if there are no elements under the given key
     */
    String remove(String id, int index);

    /**
     * Returns <code>true</code> if there is at least one entry
     * @param id key
     * @return <code>true</code> if there is at least one entry
     */
    boolean containsKey(String id);

    /**
     * Returns number of entries for given key
     * @param id key
     * @return number of entries for given key
     */
    int getEntrySize(String id);

    /**
     * Retrieves element from the given key. Key must have only one element (or none) for this method to work.
     * @param id key of the asked element
     * @return element
     * @throws IllegalStateException if there are more then one element under this key
     */
    String getOnly(String id);

    /**
     * Retrieves first element from the given key.
     * @param id key of the asked element
     * @return element or <code>null</code> if there are no elements given
     */
    String getFirst(String id);

    /**
     * Returns array of all elements under asked key. If there are no elements then it returns an empty array.
     * @param id key of the asked elements
     * @return an array of strings
     */
    String[] getAsArray(String id);

    /**
     * Returns list of all elements under asked key. If there are no elements then it returns an empty list.
     * @param id key of the asked elements
     * @return a list of strings
     */
    List<String> getAsList(String id);

    /**
     * Clears the map
     */
    void clear();

    /**
     * Returns list of all entries. Entries with the same key will repeat if there are more elements stored under the same key.
     * @return list of all entries
     */
    Collection<Map.Entry<String, String>> getAllEntries();

    /**
     * Returns key set
     * @return key set
     */
    Set<String> keySet();

    /**
     * Returns a map that contains all elements. Where there are more elements per key then implementation is responsiple of
     * returning something sensible like an array of strings or a collection (or a list) whose elements are strings
     * @return a map
     */
    Map<String, Object> getAsMap();

    /**
     * Number of keys in the map. It won't return total number of elements but just number of differnet keys in the map.
     * @return number of keys in the map
     */
    int size();

}
