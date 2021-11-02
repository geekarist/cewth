(ns dev.core)

(defn hello-hook
  {:shadow.build/stage :flush}
  [build-state & _args]
  (println "Hello")
  build-state)