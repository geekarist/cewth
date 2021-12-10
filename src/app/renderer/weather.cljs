(ns app.renderer.weather
  (:require
   [ajax.core :as ajx]))

;; State

(def init {})

;; Update

(defn- update-fn
  "Pure state update function"
  [[action arg :as message]
   state]
  (condp = action
    :change-query [(assoc state :query arg) nil]
    :execute-query [state [:execute-query arg (state :query)]]
    :take-search-result [(assoc state :location (arg :name)) nil]
    (println "Unknown action:" message)))

;; View

(defn- view-state
  "View function. Pure function operating on a state value."
  [dispatch state-val]
  [:div.root-ctn
   [:div.search-ctn
    [:input.search-txt
     {:type "text"
      :on-change #(dispatch [:change-query (.-value (.-target %))])
      :on-key-down #(dispatch [:execute-query (.-key %)])}]
    [:button.search-btn
     {:on-click #(dispatch [:execute-query "Enter"])}
     "Search"]
    (if (state-val :failed)
      [:span.search-warn "⚠️"]
      nil)]
   [:div.result-ctn
    [:div.location-ctn (state-val :location)]
    [:div.temperature-ctn
     [:img.weather-icn {:src (state-val :icon-src)}]
     [:span.temperature-lbl (state-val :temperature-val)]
     [:span.unit-lbl (state-val :temperature-unit)]]
    [:div.weather-text-blk (state-val :weather-text)]
    [:div.update-date-blk (state-val :updated-at)]]])

(declare dispatch)

(defn view
  "View component. Operates on a reagent state atom. 
   Called each time the atom is changed."
  [state-ref]
  (let [dispatch-event (fn [event] (dispatch event state-ref))]
    (view-state dispatch-event @state-ref)))

;; Effects

(defn- search! [query consume-query-result]
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
      {:failed (not ok?)
       :name (-> city-search-resp
                 (first)
                 (get "LocalizedName"))})]

    #_(Wire up steps)
    (-> query
        (to-city-search-req)
        (request-city-search
         (fn [city-search-resp]
           (-> city-search-resp
               (to-query-result)
               (consume-query-result)))))))

(defn- execute-query! [trigger state-ref dispatch]
  (if (= trigger "Enter")
    (search! (@state-ref :query)
             #(dispatch [:take-search-result %] state-ref))
    nil))

(defn- handle 
  "Effect handler. Individual effects are applied here."
  [[effect-key effect-arg :as _effect-vec]
               state-ref
               dispatch]
  (condp = effect-key
    :execute-query (execute-query! effect-arg state-ref dispatch)
    nil #_(Ignore nil effect)))

;; Runtime/support

(defn- dispatch 
  "Event dispatch function. Allows a view or effect to dispatch an event."
  [action state-ref]
  (let [new-state-vec (update-fn action @state-ref)
        [new-state effect] new-state-vec]
    (compare-and-set! state-ref @state-ref new-state)
    (handle effect state-ref dispatch)))