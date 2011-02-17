/**
 * 
 */
package com.pocketsoap;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.test.AbstractTestResultAction;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.pocketsoap.salesforce.soap.ChatterClient;

/**
 * @author superfell
 * 
 */
public class ChatterNotifier extends Notifier {
	
	private final String username, password, recordId, server;

	@DataBoundConstructor
	public ChatterNotifier(String username, String password, String recordId, String server) {
		this.username = username;
		this.password = password;
		this.recordId = recordId;
		this.server = server;
	}

	/**
	 * We’ll use this from the <tt>config.jelly</tt>.
	 */
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getRecordId() {
		return recordId;
	}
	public String getServer() {
		return server;
	}
	
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean needsToRunAfterFinalized() {
		return true;
	}

	/**
	 * This method should have the logic of the plugin. Access the configuration
	 * and execute the the actions.
	 */
	@Override
	public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) {
        PrintStream ps = listener.getLogger();

        String title = "Build: " + build.getProject().getName() + " " + build.getDisplayName() + " is " + build.getResult().toString();
        String rootUrl = Hudson.getInstance().getRootUrl();
        String url = rootUrl == null ? null : rootUrl + build.getUrl();
        
        ps.println("# " + title);
        ps.println("# " + url);
 
        String testHealth = null;
        AbstractTestResultAction<?> tr = build.getTestResultAction();
        if (tr != null) {
        	StringBuilder th = new StringBuilder(tr.getBuildHealth().getDescription());
        	if (tr.getFailedTests().size() > 0) {
        		th.append("\nFailures");
        		for(CaseResult cr : tr.getFailedTests())
        			th.append("\n").append(cr.getFullName());
        	}
        	testHealth = th.toString();
        }
        if (testHealth != null)
        	ps.print(testHealth);
        
        ps.println("###");
        
        try {
        	new ChatterClient(username, password, server).postBuild(recordId, title, url, testHealth);
        } catch (Exception ex) {
        	ps.print("error : " + ex.getMessage());
        }
        return true;
    }

	public Action getProjectAction(AbstractProject<?, ?> project) {
		return null;
	}

	/**
	 * Descriptor for {@link TSLPublisher}. Used as a singleton. The class is
	 * marked as public so that it can be accessed from views.
	 * 
	 * <p>
	 * See <tt>global.jelly</tt> for the actual HTML fragment for the
	 * configuration screen.
	 */
	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useFrench;

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            if(value.length()==0)
                return FormValidation.error("Please set a name");
            if(value.length()<4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Chatter Results";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         */
        public boolean useFrench() {
            return useFrench;
        }
	}
}
