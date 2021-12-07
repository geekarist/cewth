(ns dev.core
  (:require [clojure.java.io :as io]))

(defn build-normlz-css
  {:shadow.build/stage :flush}
  [build-state & _args]
  (let [input-file (io/file "node_modules" "normalize.css" "normalize.css")
        output-file (io/file "resources" "public" "css" "normalize.css")
        outdated? (not (= input-file output-file))]
    (print "Normalize.css outdated?" outdated?)
    (if outdated?
      (do (println " do update CSS file")
          (io/copy input-file output-file))
      (println " don't update CSS file")))
  build-state)

(comment
  build-normlz-css)