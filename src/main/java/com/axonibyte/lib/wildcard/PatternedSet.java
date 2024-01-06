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

import java.util.HashSet;
import java.util.Set;

/**
 * A set of strings that can be queried with a {@link Pattern}.
 *
 * @author Caleb L. Power <cpower@axonibyte.com>
 */
public class PatternedSet extends HashSet<String> {

  /**
   * Instantiates a {@link PatternedSet}.
   */
  public PatternedSet() {
    super();
  }

  /**
   * Instantiates a {@link PatternedSet} from some other set.
   *
   * @param set the original set
   */
  public PatternedSet(Set<String> set) {
    super(set);
  }

  /**
   * Determines if this set contains some element that matches the provided
   * pattern.
   *
   * @param pattern the {@link Pattern} against which elements of this set will
   *        be matched
   * @return {@code true} if at least one element in this set successfully
   *         matched the provided pattern
   */
  public boolean contains(Pattern pattern) {
    if(super.contains(pattern.toString())) return true;

    for(String val : this)
      if(pattern.matches(val))
        return true;

    return false;
  }

  /**
   * Removes all elements from this set that match the provided pattern.
   *
   * @param pattern the {@link Pattern} against which elements of this set will
   *        be matched
   * @return {@code true} if at least one element in this set matched the
   *         pattern and was subsequently removed
   */
  public boolean remove(Pattern pattern) {
    if(super.remove(pattern.toString())) return true;

    boolean removed = false;
    var itr = iterator();
    while(itr.hasNext()) {
      String val = itr.next();
      if(pattern.matches(val)) {
        itr.remove();
        removed = true;
      }
    }

    return removed;
  }
  
}
