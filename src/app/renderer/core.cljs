(ns app.renderer.core
  (:require [reagent.core :refer [atom]]
            [reagent.dom :as rd]
            [ajax.core :as ajx]))

(enable-console-print!)

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

(comment
  (search! "monti" #(println "Result: " %)))

(def init {})

(defonce state (atom init))

(declare dispatch)

(defn- change-query! [query]
  (compare-and-set! state
                    @state
                    (assoc state :query query)))

(defn- execute-query! [trigger]
  (if (= trigger "Enter")
    (search! (@state :query)
             #(dispatch [:take-search-result %]))
    nil))

(defn- take-search-result! [result]
  (compare-and-set!
   state
   @state
   (assoc state
          :location (result :name)
          :failed (result :failed))))

(defn update-fn [[action arg] state]
  (condp = action
    :change-query [state [:change-query arg]]
    :execute-query [state [:execute-query arg]]
    :take-search-result [(compare-and-set! state @state arg) nil]))

(defn view [dispatch]
  [:div.root-ctn
   [:div.search-ctn
    [:input.search-txt
     {:type "text"
      :on-change #(dispatch [:change-query (.-value (.-target %))]) 
      :on-key-down #(dispatch [:execute-query (.-key %)])}]
    [:button.search-btn
     {:on-click #(dispatch [:execute-query "Enter"])}
     "Search"]
    (if (@state :failed)
      [:span.search-warn "⚠️"]
      nil)]
   [:div.result-ctn
    [:div.location-ctn (@state :location)]
    [:div.temperature-ctn
     [:img.weather-icn {:src (@state :icon-src)}]
     [:span.temperature-lbl (@state :temperature-val)]
     [:span.unit-lbl (@state :temperature-unit)]]
    [:div.weather-text-blk (@state :weather-text)]
    [:div.update-date-blk (@state :updated-at)]]])

(defn handle [[effect arg :as _action]]
  (condp = effect
    :change-query (change-query! arg)
    :execute-query (execute-query! arg)
    :take-search-result (take-search-result! arg)))

(defn dispatch [action]
  (let [[new-state effect] (update-fn action @state)]
    (handle effect)
    (compare-and-set! state @state new-state)))

(defn ^:dev/after-load start! []
  (rd/render
   [view dispatch]
   (js/document.getElementById "app-container")))

(comment
  start!)