package javaposse.jobdsl.dsl.helpers.view

import javaposse.jobdsl.dsl.Context

class AllStatusesColumnContext implements Context {
    String colorblindHint
    boolean onlyShowLastStatus
    String timeAgoTypeString
    int hideDays

    /**
     * Color-coding can cause accessibility concerns.  Choose a colorblind hints option if this will be useful to you
     * @param colorblindHint may be 'nohint' or 'underlinehint'
     */
    void colorblindHint(String colorblindHint) {
        this.colorblindHint = colorblindHint
    }

    /**
     * Show the status of the last build only.
     */
    void onlyShowLastStatus(boolean onlyShowLastStatus) {
        this.onlyShowLastStatus = onlyShowLastStatus
    }

    /**
     * Set time display option.
     * @param timeAgoTypeString may be 'DIFF', 'PREFER_DATES' or 'PREFER_DATE_TIME'
     */
    void timeAgoTypeString(String timeAgoTypeString) {
        this.timeAgoTypeString = timeAgoTypeString
    }

    /**
     * Hide additional statuses when older that @param hideDays
     */
    void hideDays(int hideDays) {
        this.hideDays = hideDays
    }
}
