(ns ^{:author "Vladislav Bauer"}
  lein-jslint.t-core
  (:require [lein-jslint.core :as core]
            [clojure.test :as t]))


; Configurations

(def ^:private DEF_CONFIG
  {:jslint
    {:debug true
     :includes ["example/resources/*.js"]}})

(def ^:private not-nil? (complement nil?))

; Tests

(t/deftest check-jslint-processor
  (t/is (not-nil? (core/jslint DEF_CONFIG)))
  (t/is (not-nil? (core/jslint DEF_CONFIG ["--ass"]))))
