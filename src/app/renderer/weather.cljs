(ns app.renderer.weather
  (:require
   [ajax.core :as ajx]
   [clojure.string :as str]))

;; Effects

(defn- search! [req dispatch]
  (ajx/ajax-request
   (assoc req
          :handler
          (fn [result]
            (dispatch [:ev/take-city-search-response result])))))

;; Handle effects

(defn handle-effect!
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

(defn- handle-event-change-query 
  "Change query field:
   - State change: add query value
   - No effect"
  [state query]
  
  (let [new-state (assoc state :state/query query)
        new-effect nil]
    [new-state new-effect]))

(defn- handle-event-send-city-search-req 
  "Convert query to city search request:
   - State change: clear query if blank, or create city search request
   - Effect: trigger search only if query is not blank and kbd key is Enter"
  [state kbd-key]

  (let [new-state
        (if (str/blank? (state :state/query))
          (assoc state
                 :state/location nil
                 :state/failed nil)
          (assoc state
                 :state/city-search-req
                 {:uri (str "http://localhost:3000"
                            "/dataservice.accuweather.com"
                            "/locations/v1/cities/search")
                  :method :get
                  :params {:q (state :state/query)
                           :api-key "TODO"}
                  :format (ajx/json-request-format)
                  :response-format (ajx/json-response-format)}))

        new-effect
        (if (and
             (not (str/blank? (state :state/query)))
             (= kbd-key "Enter"))
          [:fx/search (state :state/city-search-req)]
          nil)]
    
    [new-state new-effect]))

(defn- handle-event-execute-city-search
  "Execute query 
   - No state change
   - Trigger search effect with query value"
  [state _event-arg]

  (let [new-state state

        new-effect
        [:fx/search (new-state :state/city-search-req)]]

    [new-state new-effect]))

(defn- handle-event-take-city-search-response
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

(defn update-fn
  "Pure state update function"
  [[event-key event-arg :as _event] state]

  (condp = event-key

    :ev/change-query
    (handle-event-change-query state event-arg)

    :ev/send-city-search-req
    (handle-event-send-city-search-req state event-arg)

    :ev/execute-city-search
    (handle-event-execute-city-search state event-arg)

    :ev/take-city-search-response
    (handle-event-take-city-search-response state event-arg)))

;; View

(defn view-fn
  "View function. Pure function operating on a state value"
  [dispatch state-val]
  [:div.root-ctn
   [:div.search-ctn
    [:input.search-txt
     {:type "text"
      :on-change #(dispatch [:ev/change-query (.-value (.-target %))])
      :on-key-down #(dispatch [:ev/send-city-search-req (.-key %)])}]
    [:button.search-btn
     {:on-click #(dispatch [:ev/send-city-search-req "Enter"])}
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

