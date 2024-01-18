/* =======================================================================
   Licensed under the Apache License,Version2.0(the"License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,software
   distributed under the License is distributed on an"AS IS"BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==========================================================================*/
package com.feature.tui.widget.indexablerv;

import java.util.regex.Pattern;

/**
 * Created by YoKey on 16/3/20.
 */
public class PinyinUtil {
    private static final String PATTERN_POLYPHONE = "^#[a-zA-Z]+#.+";
    private static final String PATTERN_LETTER = "^[a-zA-Z].*+";

    /**
     * Chinese character -> Pinyin
     */
    public static String getPingYin(String inputString) {
        if (inputString == null) return "";
        return com.feature.tui.util.tingpinyin.Pinyin.toPinyin(inputString.charAt(0));
    }

    /**
     * Are start with a letter
     *
     * @return if return false, index should be #
     */
    static boolean matchingLetter(String inputString) {
        return Pattern.matches(PATTERN_LETTER, inputString);
    }

    static boolean matchingPolyphone(String inputString) {
        return Pattern.matches(PATTERN_POLYPHONE, inputString);
    }

    static String gePolyphoneInitial(String inputString) {
        return inputString.substring(1, 2);
    }

    static String getPolyphoneRealPinyin(String inputString) {
        String[] splits = inputString.split("#");
        return splits[1];
    }

    static String getPolyphoneRealHanzi(String inputString) {
        String[] splits = inputString.split("#");
        return splits[2];
    }
}
