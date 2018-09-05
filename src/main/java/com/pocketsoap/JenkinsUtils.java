package com.pocketsoap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.model.Run;
import jenkins.model.Jenkins;

import javax.annotation.Nonnull;

/**
 * Simple helper class to centralize some Jenkins info.
 *
 * I don't really like Utils classes but there was very
 * little to centralize, so here we are. :)
 *
 * @author justinharringa
 */
public class JenkinsUtils {
    @SuppressFBWarnings(value="NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification="Jenkins.getInstance() is not null")
    private static Jenkins getJenkinsInstance() {
        return Jenkins.getInstance();
    }

    public static String getRunUrl(@Nonnull Run<?, ?> run) {
        String rootUrl = getJenkinsInstance().getRootUrl();
        return rootUrl == null ? null : rootUrl + run.getUrl();
    }

    public static ChatterNotifier.DescriptorImpl getChatterNotifierDescriptor() {
        Jenkins jenkins = getJenkinsInstance();
        return jenkins.getDescriptorByType(ChatterNotifier.DescriptorImpl.class);
    }
}
