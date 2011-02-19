// Copyright (c) 2011 Simon Fell
//
// Permission is hereby granted, free of charge, to any person obtaining a 
// copy of this software and associated documentation files (the "Software"), 
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense, 
// and/or sell copies of the Software, and to permit persons to whom the 
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included 
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
// THE SOFTWARE.
//

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
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

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
	 * We'll use this from the <tt>config.jelly</tt>.
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
	
	// we'll run after being finalized, and not look at previous results
	// so we don't need any locking here, this'll let us be used safely
	// from a concurrent build.
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
        
        try {
        	new ChatterClient(username, password, server).postBuild(recordId, title, url, testHealth);
        } catch (Exception ex) {
        	ps.print("error posting to chatter : " + ex.getMessage());
        }
        return true;
    }

	public Action getProjectAction(AbstractProject<?, ?> project) {
		return null;
	}

	/**
	 * Descriptor for {@link ChatterNotifier}. Used as a singleton. The class is
	 * marked as public so that it can be accessed from views.
	 * 
	 * <p>
	 * See <tt>global.jelly</tt> for the actual HTML fragment for the
	 * configuration screen.
	 */
	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		@Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /** This human readable name is used in the configuration screen. */
		@Override
		public String getDisplayName() {
            return "Chatter Results";
        }
		
		public FormValidation doCheckUsername(@QueryParameter String value) {
            if(value.length()==0)
                return FormValidation.error("Please enter a username");
            if(value.indexOf('@') == -1)
                return FormValidation.warning("Username's usually have an @ in them, e.g. hudson@example.org");
            return FormValidation.ok();
        }
		
		public FormValidation doCheckPassword(@QueryParameter String value) {
            if(value.length()==0)
                return FormValidation.error("Please enter a password");
            return FormValidation.ok();
		}
		
		public FormValidation doCheckRecordId(@QueryParameter String value) {
			int l = value.length();
			if (l == 0 || l == 15 || l == 18)
				return FormValidation.ok();
			return FormValidation.error("recordId should be blank, or should be a valid Salesforce.com recordId, e.g. 0F930000000PCJP");
		}
		
		public FormValidation doCheckServer(@QueryParameter String value) {
			if (value.length() == 0)
				return FormValidation.ok();
			
			try {
				URL u = new URL(value);
				String h = u.getHost().toLowerCase();
				if (!(h.endsWith(".salesforce.com") || h.endsWith(".force.com")))
					return FormValidation.warning("Are you sure this is the correct URL, this doesn't appear to be a Salesforce.com server");
				if (u.getProtocol().equalsIgnoreCase("http"))
					return FormValidation.warning("Are you sure you want to use HTTP, its recommended to use HTTPS");
			} catch (MalformedURLException e) {
				return FormValidation.error("please enter a valid URL, e.g. https://test.salesforce.com");
			}
			return FormValidation.ok();
		}
	}
}
