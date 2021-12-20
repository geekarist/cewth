(ns app.renderer.weather.program
  (:require
   [app.renderer.weather.handle-event :as handle-event]
   [app.renderer.weather.handle-effect :as handle-effect]))

;; State

(def init {:state/location nil
           :state/icon-src nil
           :state/query nil
           :state/temperature-unit nil
           :state/temperature-val nil
           :state/updated-at nil
           :state/weather-text nil})

;; Effects

(defn handle-effect!
  "Effect handler. Individual effects are applied here."
  [[effect-key effect-arg :as _effect-vec]
   dispatch!]
  (condp = effect-key
    
    :fx/search (handle-effect/search! effect-arg dispatch!)
    
    :fx/current-conditions
    (handle-effect/current-conditions! effect-arg dispatch!)

    :fx/request-current-time
    (handle-effect/request-current-time! dispatch!)

    nil #_(Ignore nil effect)))

;; Update

(defn update-fn
  "Pure state update function"
  [[event-key event-arg :as _event] state]

  (condp = event-key

    :ev/change-query
    (handle-event/change-query state event-arg)

    :ev/send-city-search-req
    (handle-event/send-city-search-req state event-arg)

    :ev/take-city-search-response
    (handle-event/take-city-search-response state event-arg)

    :ev/take-current-conditions-response
    (handle-event/take-current-conditions-response state event-arg)
    
    :ev/take-current-date-response
    (handle-event/take-current-date-response state event-arg)))

;; View

(defn view-fn
  "View function. Pure function operating on a state value"
  [dispatch! state-val]
  [:div.root-ctn
   [:div.search-ctn
    [:input.search-txt
     {:type "text"
      :on-change #(dispatch! [:ev/change-query (.-value (.-target %))])
      :on-key-down #(dispatch! [:ev/send-city-search-req (.-key %)])}]
    [:button.search-btn
     {:on-click #(dispatch! [:ev/send-city-search-req "Enter"])}
     "Search"]
    (if (state-val :state/failed)
      [:span.search-warn "⚠️"]
      nil)]
   [:div.result-ctn
    [:div.location-ctn (state-val :state/location)]
    [:div.temperature-ctn
     [:img.weather-icn {:src (state-val :state/icon-src)}]
     [:span.temperature-lbl (state-val :state/temperature-val)]
     [:span.unit-lbl (state-val :state/temperature-unit)]]
    [:div.weather-text-blk (state-val :state/weather-text)]
    [:div.update-date-blk (state-val :state/updated-at)]]])

