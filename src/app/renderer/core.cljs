(ns app.renderer.core
  (:require
   [app.renderer.weather :as weather]
   [app.renderer.rgrt :as rgrt]))

(enable-console-print!)

(defn ^:dev/after-load start! []
  (rgrt/make-program weather/init
                     weather/update-fn
                     weather/view-fn
                     weather/handle))

(comment
  start!)