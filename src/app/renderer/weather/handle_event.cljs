(ns app.renderer.weather.handle-event
  (:require [ajax.core :as ajx]
            [clojure.string :as str]
            [goog.string :as gstr]
            [goog.string.format]
            [tick.core :as t]))

(defn change-query
  "Change query field:
   - State change: add query value
   - No effect"
  [state query]

  (let [new-state 
        (assoc state 
               :state/query query
               :state/failed nil
               :state/icon-src nil
               :state/location nil
               :state/temperature-unit nil
               :state/temperature-val nil
               :state/updated-at nil
               :state/weather-text nil)
        
        new-effect nil]
    [new-state new-effect]))

(defn send-city-search-req
  "Convert query to city search request:
   - State change: clear query if blank, or create city search request
   - Effect: trigger search only if query is not blank and kbd key is Enter"
  [state kbd-key]

  (let [new-state
        (if (str/blank? (state :state/query))
          (assoc state
                 :state/location nil
                 :state/failed nil)
          state)

        new-effect
        (if (and
             (not (str/blank? (state :state/query)))
             (= kbd-key "Enter"))
          [:fx/search {:uri (str "http://localhost:3000"
                                 "/dataservice.accuweather.com"
                                 "/locations/v1/cities/search")
                       :method :get
                       :params {:q (state :state/query)
                                :api-key "TODO"}
                       :format (ajx/json-request-format)
                       :response-format (ajx/json-response-format)}]
          nil)]

    [new-state new-effect]))

(defn take-city-search-response
  "Take search response:
   - State change: convert response to result (extract location and status)
   - No effect"
  [state [ok? city-search-resp :as _event-arg]]

  (let [new-state
        (if (empty? city-search-resp)
          (assoc state
                 :state/location "Not found"
                 :state/failed   (not ok?))
          (assoc state
                 :state/location (-> city-search-resp
                                     (first)
                                     (get "LocalizedName"))
                 :state/failed   (not ok?)))

        new-effect
        (if (seq city-search-resp)
          [:fx/current-conditions
           {:uri (str "http://localhost:3000"
                      "/dataservice.accuweather.com"
                      "/currentconditions/v1/"
                      (-> city-search-resp
                          (first)
                          (get "Key")))
            :method :get
            :params {:api-key "TODO"}
            :format (ajx/json-request-format)
            :response-format (ajx/json-response-format)}]
          nil)]
    
    [new-state new-effect]))

(defn take-current-conditions-response
  [state [_ok? current-conditions-resp :as _event-arg]]

  (let [new-state
        (assoc state
               :state/temperature-val (-> current-conditions-resp
                                          (first)
                                          (get "Temperature")
                                          (get "Metric")
                                          (get "Value"))
               :state/temperature-unit (-> current-conditions-resp
                                           (first)
                                           (get "Temperature")
                                           (get "Metric")
                                           (get "Unit"))
               :state/weather-text (-> current-conditions-resp
                                       (first)
                                       (get "WeatherText")))

        new-effect [:fx/request-current-time]]

    [new-state new-effect]))

(defn- parse-timestamp [zoned-date-time]
  (t/format :iso-zoned-date-time zoned-date-time))
  
(defn take-current-date-response [state zoned-date-time]

  (let [new-state (assoc state
                         :state/updated-at
                         (gstr/format "Updated at %s"
                                      (parse-timestamp zoned-date-time)))
        new-effect nil]

    [new-state new-effect]))