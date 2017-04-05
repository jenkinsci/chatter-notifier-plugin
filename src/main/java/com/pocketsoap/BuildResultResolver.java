package com.pocketsoap;

import hudson.model.AbstractBuild;
import hudson.model.Result;

/**
 * Created by jharringa on 4/4/17.
 *
 * This class provides additional build result statuses such as FIXED and STILL FAILING by understanding the
 * current build's context.
 */
public class BuildResultResolver {

    public static final String FIXED_STATUS = "FIXED";
    public static final String STILL_FAILING_STATUS = "STILL FAILING";

    /**
     * Return contextual build result of the current build.
     * If the last build failed and this build is successful, return FIXED
     * If the last build failed and this build failed, return FAILURE
     * Otherwise, we stick with the build's actual result
     *
     * @param build the current build
     * @return contextual build result
     */
    public static String getContextualResult(AbstractBuild<?,?> build) {
        AbstractBuild<?, ?> previousBuild = build.getPreviousBuild();
        if (previousBuild != null) {
            if (previousBuild.getResult() == Result.FAILURE) {
                if (build.getResult() == Result.SUCCESS) {
                    return FIXED_STATUS;
                } else if (build.getResult() == Result.FAILURE) {
                    return STILL_FAILING_STATUS;
                }
            }
        }
        return build.getResult().toString();
    }
}
