(ns ^{:author "Vladislav Bauer"}
  leiningen.jslint
  (:require [leiningen.npm :as npm]
            [leiningen.npm.process :as process]
            [leiningen.help :as help]
            [leiningen.compile]
            [cheshire.core :as json]
            [robert.hooke :as hooke]
            [org.satta.glob :as glob]
            [clojure.java.io :as io]
            [clojure.string :as string]))

; Internal API: Common

(defn- find-files [patterns]
  (map str (flatten (map glob/glob patterns))))

(defn- create-tmp-file [file content]
  (doto (io/file file)
    (spit content)
    (.deleteOnExit)))


; Internal API: Configuration

(def ^:private DEF_CONFIG_FILE ".jslintrc")
(def ^:private DEF_CONFIG
  {:confusion    true
   :continue     true
   :css          true
   :es5          true
   :fragment     true
   :regexp       true
   :unparam      true
   :vars         true
   :validthis    true
   :strict_mode  true})


(defn- opt [project k v] (get-in project [:jslint k] v))

(defn- config [project] (opt project :config {}))
(defn- config-file [project] (opt project :config-file DEF_CONFIG_FILE))
(defn- include-files [project] (find-files (vec (opt project :includes nil))))


; Internal API: Runner

(defn- generate-config-file [project]
  (json/generate-string
   (merge (config project) DEF_CONFIG)))

(defn- invoke [project & args]
  (process/exec
   (project :root)
   (cons "jslint" args)))

(defn- proc [project]
  (npm/environmental-consistency project)
  (let [file (config-file project)
        content (generate-config-file project)
        includes (include-files project)]
    (npm/with-json-file file content project
                        (apply invoke project includes))))


; External API: Tasks

(defn jslint
  "Invoke the JSLint, Static analysis tool for JavaScript"
  [project & args]
  (if (= args ["help"])
    (println (help/help-for "jslint"))
    (proc project)))


; External API: Hooks

(defn check-hook [f & args]
  (apply f args)
  (proc (first args)))

(defn activate []
  (hooke/add-hook #'leiningen.compile/compile #'check-hook))
