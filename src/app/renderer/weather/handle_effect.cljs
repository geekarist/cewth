(ns app.renderer.weather.handle-effect
  (:require [ajax.core :as ajx]
            [tick.core :as t]))

;; Effects

(defn request-current-time! [dispatch!]
  (dispatch! [:ev/take-current-date-response (t/zoned-date-time)]))

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