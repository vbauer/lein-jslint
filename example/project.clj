(defproject example "0.1.0-SNAPSHOT"
  :description "Simple example of using lein-jslint"
  :url "https://github.com/vbauer/lein-jslint"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins [[lein-jslint "0.1.3"]]

  :jslint {:includes "resources/*.js"})
