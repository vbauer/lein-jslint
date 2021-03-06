(ns ^{:author "Vladislav Bauer"}
  lein-jslint.core
  (:require [leiningen.npm :as npm]
            [leiningen.npm.process :as process]
            [leiningen.core.main :as main]
            [leiningen.compile]
            [me.raynes.fs :as fs]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as string]))


; Internal API: Common

(defn- abs-path [f] (.getAbsolutePath f))

(defn- clean-path [p]
  (if (not (nil? p))
    (if (.startsWith (System/getProperty "os.name") "Windows")
      (string/replace p "/" "\\")
      (string/replace p "\\" "/"))))

(defn- scan-files [patterns]
  (set (mapcat fs/glob (map clean-path patterns))))

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

(defn- root [project]
  (if-let [root (project :npm-root)]
    (if (keyword? root)
      (project root) ;; e.g. support using :target-path
      root)
    (project :root)))

; Internal API: JSON

(defn- json-file
  [filename project]
  (io/file (root project) filename))

(defn write-json-file
  [filename content project]
  (doto (json-file filename project)
    (spit content)
    (.deleteOnExit)))

(defn remove-json-file
  [filename project]
  (.delete (json-file filename project)))

(defmacro with-json-file
  [filename content project & forms]
  `(try
     (write-json-file ~filename ~content ~project)
     ~@forms
     (finally (remove-json-file ~filename ~project))))



; Internal API: Configuration

(def ^:public DEF_JSLINT_CMD "jslint")

(def ^:private DEF_JSLINT_DIR "node_modules/jslint/bin/")
(def ^:private DEF_CONFIG_FILE ".jslintrc")
(def ^:private DEF_CONFIG
  {:confusion    true
   :continue     true
   :regexp       true
   :unparam      true
   :vars         true})


(defn- opt [project k v] (get-in project [:jslint k] v))

(defn- config [project] (opt project :config {}))
(defn- debug [project] (opt project :debug false))
(defn- config-file [project] (opt project :config-file DEF_CONFIG_FILE))
(defn- include-files [project] (scan-files (opt project :includes nil)))
(defn- exclude-files [project] (scan-files (opt project :excludes nil)))


; Internal API: Runner

(defn- generate-config-file [project]
  (json/generate-string
   (merge (config project) DEF_CONFIG)))

(defn- source-list [project]
  (let [src (include-files project)
        ex (exclude-files project)
        sources (remove (fn [s] (some #(.compareTo % s) ex)) src)]
    (if (empty? sources)
      (throw (RuntimeException.
              "Source list is empty. Check parameters :sources & :excludes"))
      (map abs-path sources))))

(defn- invoke [project args]
  (let [root (:root project)
        local (str DEF_JSLINT_DIR DEF_JSLINT_CMD)
        cmd (if (.exists (fs/file local)) local DEF_JSLINT_CMD)]
    (process/exec root (cons cmd args))))


; External API: Runner

(defn jslint [project & args]
  (try
    (npm/environmental-consistency project)
    (let [file (config-file project)
          content (generate-config-file project)
          sources (source-list project)]
      (if-not (empty? sources)
        (with-json-file file content project
                            (invoke project sources))))
    (catch Throwable t
      (error t (debug project))
      (main/abort))))
