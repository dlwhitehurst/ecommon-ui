/******************************************************************************
 * Copyright (c) 2014,2015 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the 
 * License.
 *
 * Contributors:
 *    dlwhitehurst 
 *
 *******************************************************************************
 */


package org.citechnical.ecommon.ui.utility;

import org.citechnical.ecommon.language.utility.BrokenString;

import java.awt.*;
import java.util.StringTokenizer;

/**
 * Class description goes here ...
 *
 * @author <a href="mailto:dlwhitehurst@gmail.com">David L. Whitehurst</a>
 *         created: 2/3/15
 *         time: 10:17 PM
 * @version CHANGEME
 */

public class UIStringUtility {

    public static final int AVERAGE_CHAR_PX_WIDTH = 8;
    public static final int CHAR_PX_PADDING = 3; // per 100 characters approx.
    public static final String HTML_TAG_START = "<html>";
    public static final String HTML_TAG_END = "</html>";
    public static final String LINE_BREAK = "<br>";
    public static final String SPACE = " ";

    /**
     * This method returns width of space for string (in pixels) available for display
     * @return int approxPresentableLengthPx e.g. 395
     */
    public static int getApproxPresentableLengthPx(Dimension widgetSize) {

        int retVal = -1;
        int presentableWidth = -1;

        // the integer 5 was a guess for me
        if (widgetSize != null) {
            presentableWidth = new Double(widgetSize.getWidth() - 5).intValue();
        }

        if (presentableWidth > 1) {
            retVal = presentableWidth;
        }

        return retVal;

    }

    /**
     * This method returns width of entire checkbox text (label, in pixels)
     * @return int approxOverallLengthPx e.g. 488
     */
    public static int getApproxOverallLengthPx(int stringLength) {

        int retVal = -1;
        //int strLen = this.getText().length();

        if (stringLength > 0) {
            retVal = stringLength * AVERAGE_CHAR_PX_WIDTH;
        }

        return retVal;

    }

    /**
     * This method returns the unique number of words per pixel length of
     * the specified string
     * @return the count of words in the given pixel length e.g. 11 words in 395 px len
     */
    public static int getWordCountPerPxLength(String workStr, int length) {
        int retVal = 0;
        StringTokenizer toker = new StringTokenizer(workStr,SPACE);
        String sum = new String("");
        while(toker.hasMoreTokens()) {
            String token = toker.nextToken();

            if ((sum.length() * AVERAGE_CHAR_PX_WIDTH) <= length) {
                sum = sum + token + " ";
                retVal++;
            }
        }

        return retVal;
    }
    /**
     * This method returns a BrokenString data structure with a specialized
     * unique string that fits the container needs (presentation row) and a
     * remaining piece of text that has yet to be analyzed for editing.
     * @param workStr
     * @param wordCount
     * @return the BrokenString data structure
     */
    public static BrokenString getBrokenStringOfWords(String workStr, int wordCount) {
        // start no space, end no space, on word, remember to add space
        // e.g "Hello my name is David Whitehurst and I was born in Virginia." - 11 words from method
        // method here should return - "Hello my name is David Whitehurst and I was born in" (first part)
        // "Virginia" (last part)

        BrokenString brokenString = new BrokenString();
        StringTokenizer toker = new StringTokenizer(workStr, SPACE);
        String firstPart = new String();
        int j = 0;
        for (int i = 0; i < wordCount; i++) {
            if (j == 0) {
                firstPart = firstPart + toker.nextToken();
                j++;
            } else {
                firstPart = firstPart + SPACE + toker.nextToken();
            }
        }

        // first part done
        brokenString.setFirstPart(firstPart);
        // calculate second part
        String secondPart = getStringRemainder(workStr,firstPart);

        brokenString.setSecondPart(secondPart);
        return brokenString;
    }

    /**
     * This method returns the remaining piece of a string when given
     * the entire phrase and the front piece
     * @param all - complete sentence, paragraph, phrase, or string
     * @param front - front portion to cut leaving remainder
     * @return string remainder
     */
    public static String getStringRemainder(String all, String front) {
        String remainder;
        String[] strArray = all.split(front, front.length());
        return strArray[1].toString();
    }

}
