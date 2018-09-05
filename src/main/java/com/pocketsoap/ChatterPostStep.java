package com.pocketsoap;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.pocketsoap.salesforce.soap.ChatterClient;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

/**
 * This class allows the user to post to Chatter as a build step in
 * either FreeStyle Jobs or Pipeline jobs.
 *
 * @author justinharringa
 */
public class ChatterPostStep extends Builder implements SimpleBuildStep {
    // Optional
    private String recordId, server, buildUrlTitle;
    // Required
    private final String credentialsId, body;

    @DataBoundConstructor
    public ChatterPostStep(@Nonnull String credentialsId, @Nonnull String body) {
        this.credentialsId = credentialsId;
        this.body = body;
    }

    @Nonnull
    public String getCredentialsId() {
        return credentialsId;
    }

    @Nonnull
    public String getBuildUrlTitle() {
        return buildUrlTitle;
    }

    @DataBoundSetter
    public void setBuildUrlTitle(String buildUrlTitle) {
        this.buildUrlTitle = buildUrlTitle;
    }

    @Nonnull
    public String getBody() {
        return body;
    }

    public String getRecordId() {
        return recordId;
    }

    @DataBoundSetter
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getServer() {
        return server;
    }

    @DataBoundSetter
    public void setServer(String server) {
        this.server = server;
    }


    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        PrintStream ps = listener.getLogger();
        String resultsUrl = JenkinsUtils.getRunUrl(run);

        try {
            UsernamePasswordCredentials c = ChatterNotifier.getCredentialsById(credentialsId);
            String buildUrlTitle = StringUtils.isEmpty(this.buildUrlTitle) ? run.toString() : this.buildUrlTitle;
            new ChatterClient(c.getUsername(), Secret.toString(c.getPassword()), server)
                    .postText(recordId, buildUrlTitle, body, resultsUrl);
            ps.print(String.format("Posting to Chatter with body=%s at recordId=%s%n", body, recordId));
        } catch (Exception ex) {
            ps.print(String.format("error posting to chatter : %s%n", ex.getMessage()));
        }

    }

    @Symbol("chatterPost")
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item item) {
            return JenkinsUtils.getChatterNotifierDescriptor().doFillCredentialsIdItems(item);
        }

        public FormValidation doCheckCredentialsId(@QueryParameter String value) {
            return JenkinsUtils.getChatterNotifierDescriptor().doCheckCredentialsId(value);
        }

        public FormValidation doCheckRecordId(@QueryParameter String value) {
            return JenkinsUtils.getChatterNotifierDescriptor().doCheckRecordId(value);
        }

        public FormValidation doCheckServer(@QueryParameter String value) {
            return JenkinsUtils.getChatterNotifierDescriptor().doCheckServer(value);
        }

        @RequirePOST
        public FormValidation doTestConnection(
                @QueryParameter("username") String username,
                @QueryParameter("password") String password,
                @QueryParameter("recordId") String recordId,
                @QueryParameter("server") String server,
                @QueryParameter("credentialsId") String credentialsId,
                @AncestorInPath Item item) {
            return JenkinsUtils.getChatterNotifierDescriptor()
                    .doTestConnection(username, password, recordId, server, credentialsId, item);
        }

        /**
         * Returns true if this task is applicable to the given project.
         *
         * @param jobType check this job type to see if this step is applicable
         * @return true to allow user to configure this post-promotion task for the given project.
         * @see AbstractProject.AbstractProjectDescriptor#isApplicable(Descriptor)
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         * Human readable name of this kind of configurable object.
         * Should be overridden for most descriptors, if the display name is visible somehow.
         * As a fallback it uses {@link Class#getSimpleName} on {@link #clazz}, so for example {@code MyThing} from {@code some.pkg.MyThing.DescriptorImpl}.
         * Historically some implementations returned null as a way of hiding the descriptor from the UI,
         * but this is generally managed by an explicit method such as {@code isEnabled} or {@code isApplicable}.
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return "Post to Chatter";
        }
    }
}
