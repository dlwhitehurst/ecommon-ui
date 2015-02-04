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


package org.citechnical.ecommon.ui.swing.component;

import org.citechnical.ecommon.language.utility.BrokenString;
import org.citechnical.ecommon.ui.utility.UIStringUtility;

import javax.swing.*;
import java.awt.*;

/**
 * This class is used to provide a Swing checkbox where its label or text can
 * wrap within its container instead of providing an ellipsis (...) or being
 * truncated by the container's layout manager.
 *
 * @author <a href="mailto:dlwhitehurst@gmail.com">David L. Whitehurst</a>
 *         created: 2/2/15
 *         time: 10:04 PM
 * @version $Id$
 */

public class CITCheckBox extends JCheckBox {
    //private final int AVERAGE_CHAR_PX_WIDTH = 8;
    //private final int CHAR_PX_PADDING = 3; // per 100 characters approx.
    private final String HTML_TAG_START = "<html>";
    private final String HTML_TAG_END = "</html>";
    private final String LINE_BREAK = "<br>";
    private final String SPACE = " ";

    // original text
    private String rawText = null;

    // formatted text string
    private String formattedString = null;

    // private object for technical adjustments
    private BrokenString adjustedStringsObj = null;


    // adjusted data structure in play
    private boolean adjusted = false;

    // allow no uninitialized (default) construction
    private CITCheckBox() {}

    // constructor requires text
    public CITCheckBox(String label) {
        super();
        setRawText(label); // secure original text
        setText(label);
    }

    /**
     * This method will return an "adjusted" or "compensated" BrokenString
     * object if the adjusted boolean is true. Otherwise, the return will
     * be null.
     * @return adjustedStringsObj
     */
    private BrokenString getAdjustedStrings() {
        return adjustedStringsObj;
    }

    /**
     * This method is the workhorse or utility method that creates the single
     * formatted text label for the checkbox.
     * @param sb the StringBuilder object
     */
    private void buildPresentationString(StringBuilder sb, Dimension componentSize) {

        int wordCount = 0;
        String workStr = getRawText();
        BrokenString brokenString = null;

        sb.append(HTML_TAG_START);

        while (workStr != null) {

            if (isAdjusted()) {
                brokenString = getAdjustedStrings();
            } else {
                wordCount = UIStringUtility.getWordCountPerPxLength(workStr, UIStringUtility.getApproxPresentableLengthPx(componentSize));
                brokenString = UIStringUtility.getBrokenStringOfWords(workStr, wordCount);
            }

            if (brokenString != null) {
                if (brokenString.getFirstPart() != null) {
                    // count characters in first part
                    int charCountFirstPart = brokenString.getFirstPart().length();

                    // multiply count * AVERAGE_CHAR_PX_WIDTH
                    int lengthFirstPartPx = charCountFirstPart * UIStringUtility.AVERAGE_CHAR_PX_WIDTH;
                    int padding = (lengthFirstPartPx/100) * UIStringUtility.CHAR_PX_PADDING;
                    lengthFirstPartPx = lengthFirstPartPx - padding;

                    // check to be sure it's less than approxPresentableLengthPx
                    if (lengthFirstPartPx <= UIStringUtility.getApproxPresentableLengthPx(componentSize)) {
                        // if okay (equal or less) sb.append() and sb.append(<br>)
                        sb.append(brokenString.getFirstPart());
                        sb.append(LINE_BREAK);
                        sb.append(SPACE); // note: removed from begin of second part

                        if (!brokenString.getSecondPart().equals("")) {
                            // use second part and repeat process
                            workStr = brokenString.getSecondPart();
                        } else {
                            // end no second part
                            workStr = null;
                        }
                        // always reset
                        this.adjustedStringsObj = null;
                        this.setAdjusted(false);
                    } else {
                        // if greater approxPresentableLength (adjust) and re-make first and second parts in broken string
                        this.adjustedStringsObj = brokenString.reduceFirstPartByOneWord();
                        this.setAdjusted(true);
                        workStr = getRawText();
                    }
                }
            }
        }
        // remove the last line break and space before the end html tag (refactor)
        sb.append(HTML_TAG_END);
        String tmp = sb.toString();
        String better = tmp.replace(LINE_BREAK + SPACE + HTML_TAG_END, HTML_TAG_END);
        setFormattedString(better);

    }

    /**
     * This method is invoked when component listener calls for update when this
     * component's size changes.
     */
    public final void updateTextPresentation() {

        // get number of presentable rows
        int presentablePxLengths = UIStringUtility.getApproxOverallLengthPx(this.getText().length()) / UIStringUtility.getApproxPresentableLengthPx(this.getSize());

        // working data structure
        StringBuilder sb = new StringBuilder();

        // no wrapping required (less than a full presentation row)
        if (presentablePxLengths == 0) {
            return;
        }

        // at least 1 full presentation row
        if ( presentablePxLengths >= 1) {
            // build fully presentable piece using words, break on words (create formattedString)
            buildPresentationString(sb, this.getSize());
        }

        // set new display text
        this.setText(getFormattedString());

        // refresh
        this.revalidate();
        this.repaint();
    }

    /**
     * This method gets the final raw text label or original text when the checkbox was created.
     * @return
     */
    private String getRawText() {
        return rawText;
    }

    /**
     * This method is used one time to set the original checkbox label text.
     * @param rawText
     */
    private void setRawText(String rawText) {
        this.rawText = rawText;
    }

    /**
     * This method is used by the updateTextPresentation method to "set" the checkbox text after an
     * event has occurred.
     * @return the formattedString
     */
    private String getFormattedString() {
        return formattedString;
    }

    /**
     * This method is used by the buildPresentationString method to provide a uniquely formatted
     * string that has the potential to change dynamically.
     * @param formattedString
     */
    private void setFormattedString(String formattedString) {
        this.formattedString = formattedString;
    }

    /**
     * This method is used to determine at any time if the split-text-row process is going
     * smoothly. If the texts are verified and need to be adjusted in length, this boolean
     * will be set and also the broken-line data structure (adjustedStringsObj) is modified
     * and provided here too.
     * @return
     */
    private boolean isAdjusted() {
        return adjusted;
    }

    /**
     * Setter
     * @param adjusted
     */
    private void setAdjusted(boolean adjusted) {
        this.adjusted = adjusted;
    }
}
