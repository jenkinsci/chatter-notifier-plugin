# Chatter plugin for Hudson/Jenkins

This is a plugin for Hudson/Jenkins that will post build build results to a Chatter feed. You can configure it to post to a specific User (e.g. a build user), a specific group (e.g. a group that owns the build), or a specific record (perhaps you have a custom object that tracks build configs).


# Build

The plugin is built with Maven, same as Hudson itself, simply clone to repo and run mvn install, the generated plugin ChatterPlugin.hdi will be in the target directory.


# Install

Goto the manage hudson page, manage plugins, and then the advanced tab, and pick the option to upload a new plugin. When the upload is finished, you'll need to restart your Hudson server.


# Configure

With the plug-in installed, and the server restarted, the build configuration page will now have an extra option "Chatter Results", if you select this, then you can 
populate the 4 fields as need (username, password and optional serverUrl to login to, and the recordId to post the results to, leave this blank to post to the users
wall, or enter a record Id to post to that specific record (or group)).

![build feed](http://www.pocketsoap.com/weblog/hc.png)