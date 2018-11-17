(defproject lein-jslint "0.1.9"
  :description "A Leiningen plugin for running JS code through JSLint."
  :url "https://github.com/vbauer/lein-jslint"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[lein-npm "0.6.2" :exclusions [org.clojure/clojure]]
                 [me.raynes/fs "1.4.6" :exclusions [org.clojure/clojure]]]

  :pedantic? :abort
  :eval-in-leiningen true
  :local-repo-classpath true)
