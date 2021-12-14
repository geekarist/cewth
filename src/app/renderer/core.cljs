(ns app.renderer.core
  (:require
   [app.renderer.weather.program :as wp]
   [app.renderer.rgrt :as rgrt]))

(enable-console-print!)

(defn ^:dev/after-load start! []
  (rgrt/run-program wp/init
                    wp/update-fn
                    wp/view-fn
                    wp/handle-effect!))

(comment
  start!)