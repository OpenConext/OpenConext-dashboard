
# Running and logging in
To run locally, use `mvn jetty:run`.

When you browse to the [application homepage (http://localhost:8082/selfservice)) you will be prompted for a login.

A list of available log-ins can be found in the `Users` enum [here](src/main/java/nl/surfnet/coin/selfservice/util/OpenConextOAuthClientMock.java).

# Gotcha's

If you run into this Maven error message: "TypeError: can't convert String into Array", it means you have run into a bug in the sass-maven-plugin.

At the time of writing, the fix for said bug was not released yet. See: https://github.com/Jasig/sass-maven-plugin/issues/47

Resolution: make sure there is no trace at all of a GEM_PATH on your machine, see: http://stackoverflow.com/questions/3558656/how-can-i-remove-rvm-ruby-version-manager-from-my-system