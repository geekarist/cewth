(ns app.renderer.core
  (:require
   [app.renderer.weather :as weather]
   [app.renderer.rgrt :as rgrt]))

(enable-console-print!)

(defn ^:dev/after-load start! []
  (rgrt/run-program weather/init
                    weather/update-fn
                    weather/view-fn
                    weather/handle-effect!))

(comment
  start!)