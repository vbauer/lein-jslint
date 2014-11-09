(ns ^{:author "Vladislav Bauer"}
  lein-jslint.core
  (:require [leiningen.npm :as npm]
            [leiningen.npm.process :as process]
            [leiningen.core.main :as main]
            [leiningen.compile]
            [cheshire.core :as json]
            [org.satta.glob :as glob]
            [clojure.java.io :as io]
            [clojure.string :as string]))


; Internal API: Common

(defn- to-coll [e]
  (if (nil? e) [] (if (sequential? e) e [e])))

(defn- find-files [patterns]
  (map str (flatten (map glob/glob (to-coll patterns)))))

(defn- create-tmp-file [file content]
  (doto (io/file file)
    (spit content)
    (.deleteOnExit)))

(defn joine [& data]
  (string/join "\r\n" data))

(defn- error [ex dbg]
  (if dbg (.printStackTrace ex))
  (println
   (joine "\r\n"
          (str "Can't execute JSLint: " (.getMessage ex))
          "Something is wrong:"
          " - installation: npm install jslint -g"
          " - configuration: https://github.com/vbauer/lein-jslint")))


; Internal API: Configuration

(def ^:public DEF_JSLINT_CMD "jslint")

(def ^:private DEF_JSLINT_DIR "node_modules/jslint/bin/")
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
(defn- debug [project] (opt project :debug false))
(defn- config-file [project] (opt project :config-file DEF_CONFIG_FILE))
(defn- include-files [project] (find-files (opt project :includes nil)))
(defn- exclude-files [project] (find-files (opt project :excludes nil)))


; Internal API: Runner

(defn- generate-config-file [project]
  (json/generate-string
   (merge (config project) DEF_CONFIG)))

(defn- sources-list [project]
  (let [sources (include-files project)
        excludes (apply hash-set (exclude-files project))]
    (remove (fn [x] (or (empty? x)
                        (contains? excludes x))) sources)))

(defn- invoke [project args]
  (let [root (:root project)
        local (str DEF_JSLINT_DIR DEF_JSLINT_CMD)
        cmd (if (.exists (io/file local)) local DEF_JSLINT_CMD)]
    (process/exec root (cons cmd args))))


; External API: Runner

(defn jslint [project & args]
  (try
    (npm/environmental-consistency project)
    (let [file (config-file project)
          content (generate-config-file project)
          sources (sources-list project)
          arguments (if (empty? args) [] (vec args))
          params (apply concat sources arguments)]
      (if-not (empty? sources)
        (npm/with-json-file file content project
                            (invoke project params))))
    (catch Throwable t
      (error t (debug project))
      (main/abort))))
