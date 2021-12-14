(ns app.renderer.weather.handle-effect
  (:require [ajax.core :as ajx]))

;; Effects

(defn search! [req dispatch]
  (ajx/ajax-request
   (assoc req
          :handler
          (fn [result]
            (dispatch [:ev/take-city-search-response result])))))

