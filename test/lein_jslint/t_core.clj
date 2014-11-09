(ns ^{:author "Vladislav Bauer"}
  lein-jslint.t-core
  (:use [midje.sweet :only [fact]]
        [midje.util :only [testable-privates]])
  (:require [lein-jslint.core]))


; Configurations

(testable-privates
  lein-jslint.core
    jslint)

(def ^:private DEF_CONFIG
  {:jslint
    {:debug true
     :includes "example/resources/*.js"}})


; Tests

(fact "Check JSLint processor"
  (nil? (jslint DEF_CONFIG)) => false
  (nil? (jslint DEF_CONFIG ["--ass"])) => false)
