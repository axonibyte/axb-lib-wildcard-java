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

import org.testng.Assert;
import org.testng.annotations.Test;

public class PatternTest {

  @Test public void testMatches_success() {

    String[] patterns = new String[] {
      "abcdefgh",
      "abcd*fgh",
      "abcd**gh",
      "??cdefgh",
      "ab?defg*",
      "** defg?",
      "ab*d????",
      "",
    };

    String[] candidates = new String[] {
      "abcdefgh",
      " defgh",
      "abcdxxxxfgh",
      "abcdfgh",
      "xycdefgh",
      "xycde*gh",
      "ab*d????",
      "   defg ",
      "foobarbaz defgq",
      ""
    };

    boolean[][] expected = new boolean[][] {
      { true,  false, false, false, false, false, false, false, false, false },
      { true,  false,  true,  true, false, false, false, false, false, false },
      { true,  false,  true,  true, false, false, false, false, false, false },
      { true,  false, false, false,  true, false, false, false, false, false },
      { true,  false, false, false, false, false, false, false, false, false },
      { false,  true, false, false, false, false, false,  true,  true, false },
      { true,  false, false, false, false, false,  true, false, false, false },
      { false, false, false, false, false, false, false, false, false,  true }
    };

    for(int i = 0; i < patterns.length; i++) {
      Pattern pattern = new Pattern(patterns[i]);
      for(int j = 0; j < candidates.length; j++)
        Assert.assertEquals(
            pattern.matches(candidates[j]),
            expected[i][j],
            String.format(
                "match at i=%1$d (%2$s), j=%3$d (%4$s) %5$s where it should have %6$s.",
                i,
                patterns[i],
                j,
                candidates[j],
                expected[i][j] ? "failed" : "matched",
                expected[i][j] ? "matched" : "failed"));
    }
  }
  
}
