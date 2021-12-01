(ns app.renderer.core
  (:require [reagent.core :refer [atom]]
            [reagent.dom :as rd]
            [ajax.core :as ajx]))

(enable-console-print!)

(defonce state (atom {:query ""
                      :location "Bourron Marlotte, Île-de-France"
                      :icon-src "http://localhost:3000/developer.accuweather.com/sites/default/files/13-s.png"
                      :temperature-val 16
                      :temperature-unit "℃"
                      :weather-text "Mostly cloudy"
                      :updated-at "Updated as of 15:40"}))

(defn- update-state-query [state new-query]
  (assoc state :query new-query))

(defn- update-state-result [state new-result]
  (assoc state
         :location (new-result :name)
         :successful (new-result :successful)))

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
     {:successful ok?
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

(defn root-component []
  [:div.root-ctn
   [:div.search-ctn
    [:input.search-txt {:type "text"
                        :on-change #(swap! state
                                           update-state-query
                                           (.-value (.-target %)))
                        :on-key-down
                        (fn [event]
                          (if (= (.-key event) "Enter")
                            (search! (@state :query)
                                     #(swap! state update-state-result %))
                            nil))}]
    [:button.search-btn
     {:on-click
      (fn []
        (search! (@state :query)
                 #(swap! state update-state-result %)))}
     "Search"]
    (if (@state :successful)
      nil
      [:span.search-warn "⚠️"])]
   [:div.result-ctn
    [:div.location-ctn (@state :location)]
    [:div.temperature-ctn
     [:img.weather-icn {:src (@state :icon-src)}]
     [:span.temperature-lbl (@state :temperature-val)]
     [:span.unit-lbl (@state :temperature-unit)]]
    [:div.weather-text-blk (@state :weather-text)]
    [:div.update-date-blk (@state :updated-at)]]])

(defn ^:dev/after-load start! []
  (rd/render
   [root-component]
   (js/document.getElementById "app-container")))

(comment
  start!)