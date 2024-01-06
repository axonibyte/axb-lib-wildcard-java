/*
 * Copyright (c) 2023 Axonibyte Innovations, LLC. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.axonibyte.lib.wildcard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A map of values, the keys of which can be compared against a {@link Pattern}.
 *
 * @author Caleb L. Power <cpower@axonibyte.com>
 */
public class PatternedMap<T> extends HashMap<String, T> {

  /**
   * For each value with a key matching the provided {@link Pattern}, attempts
   * to compute a mapping between said key and its current mapped value (or
   * {@code null} if there is no current mapping).
   *
   * @param pattern the {@link Pattern} against which keys will be matched
   * @param remappingFunction the function to be used to compute the value
   * @return a {@link Set} of values returned by the remapping function
   */
  public Set<T> compute(Pattern pattern, BiFunction<? super String, ? super T, ? extends T> remappingFunction) {
    Set<T> set = new HashSet<>();
    for(var key : keySet(pattern))
      set.add(compute(key, remappingFunction));
    return set;
  }

  /**
   * For each key matching the provided {@link Pattern} that is not already
   * associated with a value (or is mapped to {@code null}), attempts to compute
   * said key's value using the given mapping function and enters it into this
   * map unless it resolved to {@code null}.
   *
   * If the function returns {@code null} no mapping is recorded. If the
   * function itself throws an (unchecked) exception, the exception is rethrown
   * and no mapping is recorded.
   *
   * @param pattern the {@link Pattern} against which keys will be matched
   * @param mappingFunction the function to be used to compute the value
   * @return a {@link Set} of values associated with the matching keys (which
   *         may include {@code null} if one of the computed values is
   *         {@code null})
   */
  public Set<T> computeIfAbsent(Pattern pattern, Function<? super String, ? extends T> mappingFunction) {
    Set<T> set = new HashSet<>();
    for(var key : keySet(pattern))
      set.add(computeIfAbsent(key, mappingFunction));
    return set;
  }

  /**
   * For each key matching the provided {@link Pattern} that is already present
   * and not {@code null}, attempts to compute a new mapping given said key and
   * its currently-mapped value.
   *
   * If the function returns {@code null}, the mapping is removed. If the
   * function itself throws an (unchecked) exception, the exception is rethrown
   * and the current mapping is left unchanged.
   *
   * @param pattern the {@link Pattern} against which keys will be matched
   * @param remappingFunction the function to be used to compute a value
   * @return a {@link Set} of values associated with the matching keys
   */
  public Set<T> computeIfPresent(Pattern pattern, BiFunction<? super String, ? super T, ? extends T> remappingFunction) {
    Set<T> set = new HashSet<>();
    for(var key : keySet(pattern))
      set.add(computeIfPresent(key, remappingFunction));
    return set;
  }

  /**
   * Determines whether or not any key in this map matches the provided pattern.
   *
   * @param pattern the {@link Pattern} against which keys will be matched
   * @return {@code true} iff at least one key matches the provided pattern
   */
  public boolean containsKey(Pattern pattern) {
    for(var key : super.keySet())
      if(pattern.matches(key))
        return true;
    return false;
  }

  /**
   * Retrieves a set of all values associated with keys that match the provided
   * pattern.
   *
   * @param pettern the {@link Pattern} against which keys will be matched
   * @return a {@link Set} of all values with keys matching the pattern; if no
   *         keys match the pattern, an empty set will be returned
   */
  public Set<T> get(Pattern pattern) {
    Set<T> set = new HashSet<>();
    for(var entry : super.entrySet())
      if(pattern.matches(entry.getKey()))
        set.add(entry.getValue());
    return set;
  }

  /**
   * Retrieves a {@link PatternedSet} of all keys in this map.
   *
   * @return the {@link PatternedSet} of all known keys
   */
  public PatternedSet keySet() {
    return new PatternedSet(super.keySet());
  }

  /**
   * Retrieves a {@link PatternedSet} of all keys in this map that match the
   * provided {@link Pattern}.
   *
   * @param pattern the pattern against which keys will be matched
   * @return the {@link PatternedSet} of matching keys
   */
  public PatternedSet keySet(Pattern pattern) {
    PatternedSet set = new PatternedSet();
    for(var key : super.keySet())
      if(pattern.matches(key))
        set.add(key);
    return set;
  }

  /**
   * Removes all entries from this map with keys matching the provided pattern.
   *
   * @param pattern the pattern against which keys will be matched
   * @return a {@link Set} of removed values
   */
  public Set<T> remove(Pattern pattern) {
    Set<T> set = new HashSet<>();
    var itr = entrySet().iterator();
    while(itr.hasNext()) {
      var entry = itr.next();
      if(pattern.matches(entry.getKey())) {
        set.add(entry.getValue());
        itr.remove();
      }
    }
    return set;
  }

  /**
   * Removes all entries from this map with keys matching the provided pattern
   * and values matching the specified value.
   *
   * @param pattern the pattern against which keys will be matched
   * @param value the value against which values will be matched
   * @return {@code true} iff at least one entry fully matched both the provided
   *         key and provided value, and was subsequently removed from the map
   */
  public boolean remove(Pattern pattern, T value) {
    boolean removed = false;
    var itr = entrySet().iterator();
    while(itr.hasNext()) {
      var entry = itr.next();
      if(pattern.matches(entry.getKey())
         && (null == value && null == entry.getValue()
             || null != value && value.equals(entry.getValue()))) {
        itr.remove();
        removed = true;
      }
    }
    return removed;
  }

  /**
   * Replaces values of all entries in this map having keys that match the
   * provided pattern with the specified value.
   *
   * @param pattern the pattern against which keys will be matched
   * @param value the new value to replace the old values
   * @return the {@link Set} of the old values of all updated entries
   */
  public Set<T> replace(Pattern pattern, T value) {
    Set<T> set = new HashSet<>();
    var itr = entrySet().iterator();
    while(itr.hasNext()) {
      var entry = itr.next();
      if(pattern.matches(entry.getKey())) {
        set.add(entry.getValue());
        entry.setValue(value);
      }
    }
    return set;
  }

  /**
   * Replaces values of all entries in this map that both have keys matching the
   * provided pattern and values matching the specified old value with the new
   * value provided.
   *
   * @param pattern the pattern against which keys will be matched
   * @param oldValue the value which entries must have in order to be updated
   * @param newValue the value to be applied to entries that meet the
   *        aforementioned criteria
   * @return {@code true} if at least one value was updated
   */
  public boolean replace(Pattern pattern, T oldValue, T newValue) {
    boolean replaced = false;
    var itr = entrySet().iterator();
    while(itr.hasNext()) {
      var entry = itr.next();
      if(pattern.matches(entry.getKey())
         && (null == oldValue && null == entry.getValue()
             || null != oldValue && newValue.equals(entry.getValue()))) {
        replaced = true;
        entry.setValue(newValue);
      }
    }
    return replaced;
  }
  
}
