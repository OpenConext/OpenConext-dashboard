If you run into this Maven error message: "TypeError: can't convert String into Array", it means you have run into a bug in the sass-maven-plugin.

At the time of writing, the fix for said bug was not released yet. See: https://github.com/Jasig/sass-maven-plugin/issues/47

Resolution: make sure there is no trace at all of a GEM_PATH on your machine, see: http://stackoverflow.com/questions/3558656/how-can-i-remove-rvm-ruby-version-manager-from-my-system