(ns app.renderer.core
  (:require
   [app.renderer.weather.program :as wp]
   [app.renderer.rgrt :as rgrt]
   ["moment" :as moment]))

(enable-console-print!)

(defn ^:dev/after-load start! []
  (rgrt/run-program wp/init
                    wp/update-fn
                    wp/view-fn
                    wp/handle-effect!))

(comment
  (start!)
  (moment)
  (-> (moment)
      (.format "dddd"))
  (.format (moment) "dddd"))