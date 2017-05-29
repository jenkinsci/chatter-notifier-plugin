package com.pocketsoap;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.util.ListBoxModel;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Basic Chatter Notifier tests for Jenkins
 * @author sortiz
 */
public class ChatterNotifierTest {
	
	@Rule
	public JenkinsRule jenkins = new JenkinsRule();
	
	/**
	 * Verify the getCredentialsById returns the right credentials by id
	 * @throws IOException
	 */
	@Test public void testCredentialsExist() throws IOException {
		//Insert some credentials
		UsernamePasswordCredentialsImpl c = createFakeCredentials();
		insertCredentials(c);
		
		//Verify the getCredentialsById returns the right credentials
		UsernamePasswordCredentials foundCreds = ChatterNotifier.getCredentialsById(c.getId());
		Assert.assertEquals(c.getUsername(), foundCreds.getUsername());
		Assert.assertEquals(c.getPassword(), foundCreds.getPassword());
	}
	
	@Test public void testCredentialsDoNotExist() throws IOException {
		//Insert some credentials
		UsernamePasswordCredentialsImpl c = createFakeCredentials();
		insertCredentials(c);
		
		UsernamePasswordCredentials foundCreds = ChatterNotifier.getCredentialsById("idontexist");
		Assert.assertNull(foundCreds);
	}
	
	@Test public void testListBoxReturnsUsernamePassword() {
		//Create a few dummy creds
		UsernamePasswordCredentialsImpl userPass1 = createFakeCredentials();
		//TODO: Add non-usernamepassword creds
		
		//Add to jenkins
		insertCredentials(userPass1);
		
		ChatterNotifier.DescriptorImpl descriptor = new ChatterNotifier.DescriptorImpl();
		ListBoxModel listBox = descriptor.doFillCredentialsIdItems();
		Assert.assertTrue(listBox.size() > 0);	//Theres at least 1 empty item in there
	}
	
	/**
	 * Insert credentials into Jenkins
	 * @param c A list of Credentials
	 */
	private void insertCredentials(Credentials... c) {
		SystemCredentialsProvider.getInstance().setDomainCredentialsMap(Collections.singletonMap(Domain.global(), Arrays.<Credentials>asList(c)));
	}
	
	/**
	 * Create fake credentials
	 * @return A Username Password Credential
	 */
	private UsernamePasswordCredentialsImpl createFakeCredentials() {
		final String credentialsId = "test.credentials" + System.nanoTime();
		final String username = "myuser@salesforce.com";
		final String password = "s3cr3ts";
		
		return new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, credentialsId, "sample", username, password);
	}
}
