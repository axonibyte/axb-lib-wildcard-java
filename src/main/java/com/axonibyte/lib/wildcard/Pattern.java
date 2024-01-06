/*
 * Copyright (c) 2023 Axonibyte Innovations, LLC. All rights reserved.
 *
 * The pattern matching algorithm itself was rewritten from work provied by
 * Kirk J. Krauss at https://github.com/kirkjkrauss/MatchingWildcards. That work
 * is also licensed under the Apache License, Version 2.0.
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

import java.util.Objects;

/**
 * Represents a pattern, potentially with wildcards, to be used to match against
 * future strings. Case insensitive.
 *
 * @author Caleb L. Power <cpower@axonibyte.com>
 */
public final class Pattern {

  private char[] strWild = null;
  private boolean caseSensitive;
  
  /**
   * Instantiates a pattern.
   *
   * @param pattern the pattern to match against candidates
   * @param caseSensitive {@code true} if matches should be case sensitive
   */
  public Pattern(String pattern, boolean caseSensitive) {
    Objects.requireNonNull(pattern);
    this.caseSensitive = caseSensitive;
    this.strWild = chars(pattern);
  }

  /**
   * Determines whether or not a candidate {@link String} matches this pattern.
   *
   * @param string the candidate {@link String}
   */
  public boolean matches(String string) {
    if(null == string) return false;
    char[] strTame = chars(string);

    int iWild = 0;     // index for both tame and wild strings in upper loop
    int iTame;         // index for tame string, set going into lower loop
    int iWildSequence; // index for prospective match after '*' (wild string)
    int iTameSequence; // index for prospective match (tame string)

    // find a first wildcard, if one exists, and the beginning of any
    // prospectively matching sequence after it
    do {
      
      // check for the end from the start; get out fast, if possible
      if('\0' == strTame[iWild]) {

        if('\0' != strWild[iWild]) {
          while('*' == strWild[iWild++])
            if('\0' == strWild[iWild])
              return true; // "ab" matches "ab*"
          
          return false; // "abcd" doesn't match "abc"
        } else return true; // "abc" matches "abc"
        
      } else if('*' == strWild[iWild]) {
        // got wild: set up for the second loop and skip on down there
        iTame = iWild;
        while('*' == strWild[++iWild])
          continue;

        if('\0' == strWild[iWild])
          return true; // "abc*" matches "abcd"

        // search or the next prospective match
        if('?' != strWild[iWild])
          while(strWild[iWild] != strTame[iTame])
            if('\0' == strTame[++iTame])
              return false; // "a*bc" doesn't match "ab"

        // keep fallback positins for retry in case of incomplete match
        iWildSequence = iWild;
        iTameSequence = iTame;
        break;
        
      } else if(strWild[iWild] != strTame[iWild] && '?' != strWild[iWild])
        return false; // "abc" doesn't match "abd"
      
      ++iWild; // everything's a match, so far
    } while(true);

    // find any further wilsecards and any further matching sequences
    do {
      if('*' == strWild[iWild]) {
        
        // got wild again
        while('*' == strWild[++iWild]) continue;

        if('\0' == strWild[iWild])
          return true; // "ab*c*" matches "abcd"

        if('\0' == strTame[iTame])
          return false; // "*bcd*" doesn't match "abc"

        // search for the next prospective match
        if('?' != strWild[iWild])
          while(strWild[iWild] != strTame[iTame])
            if('\0' == strTame[++iTame])
              return false; // "a*b*c" doesn't match "ab"

        // keep the new fallback positions
        iWildSequence = iWild;
        iTameSequence = iTame;
        
      } else if(strWild[iWild] != strTame[iTame] && '?' != strWild[iWild]) {

        // the equivalent portion of the upper loop is really simple
        if('\0' == strTame[iTame])
          return false; // "*bcd" doesn't match "abc"

        // a fine time for questions
        while('?' == strWild[iWildSequence]) {
          ++iWildSequence;
          ++iTameSequence;
        }

        iWild = iWildSequence;

        // fall back, but never so far again
        while(strWild[iWild] != strTame[++iTameSequence])
          if('\0' == strTame[iTameSequence])
            return false; // "*a*b" doesn't match "ac"

        iTame = iTameSequence;
      }

      // another check for the end, at the end
      if('\0' == strTame[iTame]) {
        if('\0' == strWild[iWild])
          return true; // "*bc" matches "abc"
        
        return false; // "*bc" doesn't match "abcd"
      }

      // everything's still a match
      ++iWild;
      ++iTame;
    } while(true);
  }

  // simulate a C-styled null-terminated character array as best as possible
  private char[] chars(String string) {
    int len = string.length();
    char[] arr = new char[len + 1];
    if(0 < len)
      System.arraycopy(
          (caseSensitive ? string : string.toLowerCase()).toCharArray(),
          0,
          arr,
          0,
          len);
    arr[len] = '\0';
    return arr;
  }
  
}
