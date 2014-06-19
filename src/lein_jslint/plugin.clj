(ns ^{:author "Vladislav Bauer"}
  lein-jslint.plugin
  (:require [lein-npm.plugin :as npm]
            [leiningen.jslint :as jslint]
            [leiningen.compile]
            [robert.hooke :as hooke]))


(def ^:private DEF_JSLINT_DEP "jslint")
(def ^:private DEF_JSLINT_VER ">=0.3.4")


; External API: Hooks

(defn compile-hook [task project & args]
  (let [res (apply task project args)]
    (jslint/jslint project)
    res))

(defn activate []
  (npm/hooks)
  (hooke/add-hook #'leiningen.compile/compile #'compile-hook))


; External API: Middlewares

(defn- jslint? [dep]
  (= (str (first dep)) DEF_JSLINT_DEP))

(defn- find-jslint-deps [deps]
  (keep-indexed #(when (jslint? %2) %1) deps))

(defn- ensure-jslint [deps version]
  (let [jslint-matches (find-jslint-deps deps)
        new-dep [DEF_JSLINT_DEP (or version DEF_JSLINT_VER)]]
    (if (empty? jslint-matches)
      (conj deps new-dep) deps)))

(defn middleware [project]
  (let [version (get-in project [:jslint :version])]
    (update-in project [:node-dependencies]
               #(vec (ensure-jslint % version)))))
