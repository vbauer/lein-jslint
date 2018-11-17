(defproject example "0.1.0-SNAPSHOT"
  :description "Simple example of using lein-jslint"
  :url "https://github.com/vbauer/lein-jslint"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}


  ; List of plugins
  :plugins [[lein-jslint "0.2.0"]]

  ; List of hooks
  ; It's used for running JSLint during compile phase
  :hooks [lein-jslint.plugin]

  ; JSLint configuration
  :jslint {:includes ["resources/*.js"]
           :debug true})
