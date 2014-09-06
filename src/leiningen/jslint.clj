(ns ^{:author "Vladislav Bauer"}
  leiningen.jslint
  (:require [leiningen.help :as help]
            [lein-jslint.core :as core]))

; External API: Tasks

(defn jslint
  "Invoke the JSLint, Static analysis tool for JavaScript"
  [project & args]
  (if (= args ["help"])
    (println (help/help-for core/DEF_JSLINT_CMD))
    (core/jslint project args)))
