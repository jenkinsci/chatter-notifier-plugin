## Changelog
TODO: Move to GitHub releases to fill in history.
### v2.1.0

-   Add a build step which can also be used as a pipeline step to post
    custom text to Chatter.

### v2.0.5

-   Security release to address a potential issue where any user with
    Jenkins.READ to invoke the method against a custom
    credentials-capturing server. It allowed leaking credentials if they
    were not scoped properly. It also allowed any attacker with
    Jenkins.READ to easily get a list of credential IDs vulnerable to
    the attack.

### v2.0.4

-   Utilize proxy configuration (authenticated and unauthenticated from
    Manage Jenkins → Manage Plugins → Advanced (No Proxy Hosts remains
    unimplemented)

### v2.0.3

-   Give contextual build status (FIXED, STILL FAILING)

### v2.0.2

-   Hosted on jenkins-ci.org
-   Now requires at least version 1.22 of the Credentials Plugin
