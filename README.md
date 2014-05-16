lein-jslint
===========

A Leiningen plugin for running javascript code through JSLint.

It helps to detect errors and potential problems in your JavaScript code and is based on [JSLint](https://github.com/reid/node-jslint).


Pre-requirements
================

Install [NodeJS](http://nodejs.org/) and [NPM](https://github.com/npm/npm) (package manager for Node) to install JSHint:

On Ubuntu:
```
sudo apt-get install nodejs
```
On Mac OS X:
```
brew install node
```


Installation
============


To enable lein-jslint for your project, put the following in the :plugins vector of your project.clj file:

![latest-version](https://clojars.org/lein-jslint/latest-version.svg)

[![Build Status](https://travis-ci.org/vbauer/lein-jslint.svg?branch=master)](https://travis-ci.org/vbauer/lein-jslint)
[![Dependencies Status](http://jarkeeper.com/vbauer/lein-jslint/status.png)](http://jarkeeper.com/vbauer/lein-jslint)

Install [JSLint](https://www.npmjs.org/package/jslint) to use lein-jslint plugin. It could be done in few ways:

- Use NPM to install JSLint globally:
```
npm install jslint -g
```
- You can also install JSLint in the current directory:
```
npm install jslint
```
- Use [lein-npm](https://github.com/bodil/lein-npm) plugin:
```
lein npm install
```
- Use just Leiningen:
```
lein deps
```

To enable this plugin in compile stage, use the following hook:
```clojure
:hooks [lein-jslint.plugin]
```


Configuration
=============

You can specify places, where JS files will be located with:
```clojure
:jslint {
  :includes ["resources/public/js/*.js"
             "resources/js/*.js"]
}
```

You can also specify JS files that should be excluded from checking:
```clojure
:jslint { :excludes ["resources/public/lib/*.js"] }
```

To specify *:includes* and *:excludes* options, it is possible to use <a href="http://en.wikipedia.org/wiki/Glob_(programming)">Glob Patterns</a>.

JSLint rules could be configured with *:config* parameter:
```clojure
; It specifies which JSLint options to turn on or off
:jslint {
  :config {:predef {"angular" "console" "$"}
           :nomen true
           :es5 true
           :eqeq true
           ...}}
```

You can use both variants to specify keys: string values or keywords.

All available parameters are described in the official documentation here: http://www.jslint.com/lint.html

Configuration example:
```clojure
:jslint {
  :includes ["resources/public/js/*.js"]

  ; This configuration is used by default
  :config {:confusion    true
           :continue     true
           :css          true
           :es5          true
           :fragment     true
           :regexp       true
           :unparam      true
           :vars         true
           :validthis    true
           :strict_mode  true}}
```
Another example of configuration file: [.jslintrc](https://gist.github.com/irae/2764095)


License
=======

Copyright © 2014 Vladislav Bauer

Distributed under the Eclipse Public License, the same as Clojure.
