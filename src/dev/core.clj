(ns dev.core
  (:require [clojure.java.io :as io]))

(defn build-normlz-css
  {:shadow.build/stage :flush}
  [build-state & _args]
  (println (map #(str %)
                (.list (io/file "resources" "public"))))
  (io/copy (io/file "node_modules" "normalize.css" "normalize.css")
           (io/file "resources" "public" "css" "normalize.css"))
  build-state)

(comment
  build-normlz-css)