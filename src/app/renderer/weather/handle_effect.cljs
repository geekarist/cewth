(ns app.renderer.weather.handle-effect
  (:require [ajax.core :as ajx]))

;; Effects

(defn search! [req dispatch!]
  (ajx/ajax-request
   (assoc req
          :handler
          (fn [response]
            (dispatch! [:ev/take-city-search-response response])))))

(defn current-conditions! [req dispatch!]
  (ajx/ajax-request
   (assoc req
          :handler
          (fn [response]
            (dispatch! [:ev/take-current-conditions-response response])))))