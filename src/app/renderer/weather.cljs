(ns app.renderer.weather
  (:require
   [ajax.core :as ajx]))

;; Effects

(defn- search! [query dispatch]
  (letfn #_(Define steps to search)

    [#_(1. Create city search request)
     (to-city-search-req
      [query]
      {:uri "http://localhost:3000/dataservice.accuweather.com/locations/v1/cities/search"
       :method :get
       :params {:q query
                :api-key "TODO"}
       :format (ajx/json-request-format)
       :response-format (ajx/json-response-format)})

     #_(2. Send city search request)
     (request-city-search
      [req consume-resp]
      (ajx/ajax-request (assoc req :handler consume-resp)))

     #_(3. Convert city search response to query result)
     (to-query-result
      [[ok? city-search-resp]]
      {:ev/failed (not ok?)
       :ev/name (-> city-search-resp
                    (first)
                    (get "LocalizedName"))})]

    #_(Wire up steps)
    (-> query
        (to-city-search-req)
        (request-city-search
         (fn [city-search-resp]
           (-> city-search-resp
               (to-query-result)
               (dispatch)))))))

;; Handle effects

(defn handle
  "Effect handler. Individual effects are applied here."
  [[effect-key effect-arg :as _effect-vec]
   dispatch]
  (condp = effect-key
    :fx/execute-query (search! effect-arg
                               #(dispatch [:ev/take-search-result %]))
    nil #_(Ignore nil effect)))

;; State

(def init {:state/location nil
           :state/icon-src nil
           :state/query nil
           :state/temperature-unit nil
           :state/temperature-val nil
           :state/updated-at nil
           :state/weather-text nil})

;; Update

(defn- update-state [[event-key event-arg :as _event] state]
  
  (condp = event-key
    
    :ev/change-query (assoc state :state/query event-arg)

    :ev/take-search-result
    (assoc state
           :state/location (event-arg :ev/name)
           :state/failed (event-arg :ev/failed))
    
    state))

(defn- create-effect [[event-key event-arg :as _event] state]

  (println _event)
  (condp = event-key

    :ev/execute-query
    (if (= event-arg "Enter")
      [:fx/execute-query (state :state/query)]
      nil)

    nil))

(defn update-fn
  "Pure state update function"
  [event state]
  (let [new-state (update-state  event state)
        effect    (create-effect event new-state)]
    [new-state effect]))

;; View

(defn view-fn
  "View function. Pure function operating on a state value"
  [dispatch state-val]
  [:div.root-ctn
   [:div.search-ctn
    [:input.search-txt
     {:type "text"
      :on-change #(dispatch [:ev/change-query (.-value (.-target %))])
      :on-key-down #(dispatch [:ev/execute-query (.-key %)])}]
    [:button.search-btn
     {:on-click #(dispatch [:ev/execute-query "Enter"])}
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

