(ns app.renderer.weather.handle-event
  (:require [ajax.core :as ajx]
            [clojure.string :as str]))

(defn change-query
  "Change query field:
   - State change: add query value
   - No effect"
  [state query]

  (let [new-state (assoc state :state/query query)
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

(defn execute-city-search
  "Execute query 
   - No state change
   - Effect: search provided request"
  [state req]

  (let [new-state state

        new-effect
        [:fx/search req]]

    [new-state new-effect]))

(defn take-city-search-response
  "Take search response:
   - State change: convert response to result (extract location and status)
   - No effect"
  [state [ok? city-search-resp :as _event-arg]]

  (let [new-state
        (assoc state
               :state/location (-> city-search-resp
                                   (first)
                                   (get "LocalizedName"))
               :state/failed   (not ok?))
        new-effect nil]

    [new-state new-effect]))
