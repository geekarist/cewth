(ns app.renderer.weather
  (:require
   [ajax.core :as ajx]
   [clojure.string :as str]))

;; Effects

(defn- search! [query dispatch] ;; TODO: query becomes req
  (letfn #_(Define steps to search)

    [#_(1. Create city search request) ;; TODO: Convert to :new-city-search-req event handler
     (to-city-search-req
      [query]
      {:uri "http://localhost:3000/dataservice.accuweather.com/locations/v1/cities/search"
       :method :get
       :params {:q query
                :api-key "TODO"}
       :format (ajx/json-request-format)
       :response-format (ajx/json-response-format)})

     #_(2. Send city search request) ;; TODO: Keep
     (request-city-search
      [req consume-resp]
      (ajx/ajax-request (assoc req :handler consume-resp)))

     #_(3. Convert city search response to query result) ;; TODO: Convert to :new-city-search-result event handler
     (to-query-result
      [[ok? city-search-resp]]
      {:ev/failed (not ok?)
       :ev/name (-> city-search-resp
                    (first)
                    (get "LocalizedName"))})

     #_(4. Dispatch query result) ;; TODO: Remove
     (dispatch-query-result
      [query-result]
      (dispatch [:ev/take-search-result query-result]))]

    #_(Wire up steps)
    #_(TODO
       (ajx/ajax-request
        (assoc req
               :handler
               (fn [result]
                 (dispatch [:ev/take-search-result result])))))
    (-> query
        (to-city-search-req)
        (request-city-search
         (fn [city-search-resp]
           (-> city-search-resp
               (to-query-result)
               (dispatch-query-result)))))))

;; Handle effects

(defn handle
  "Effect handler. Individual effects are applied here."
  [[effect-key effect-arg :as _effect-vec]
   dispatch]
  (condp = effect-key
    :fx/search (search! effect-arg dispatch)
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

    :ev/change-query (assoc state :state/query event-arg) ;; TODO (handle-change-query event state)

    ;; If query is empty, clear result
    :ev/execute-query ;; TODO (handle-execute-query event state)
    (if (str/blank? (state :state/query))
      (assoc state
             :state/location nil
             :state/failed nil)
      state)

    :ev/take-search-result ;; TODO (handle-search-result event state)
    (assoc state
           :state/location (event-arg :ev/name)
           :state/failed (event-arg :ev/failed))

    state))

(defn- create-effect [[event-key event-arg :as _event] state]

  (println _event)
  (condp = event-key

    :ev/execute-query
    ;; If query is empty, don't search
    (if (and (not (str/blank? (state :state/query)))
             (= event-arg "Enter"))
      [:fx/search (state :state/query)]
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

