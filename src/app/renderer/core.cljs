(ns app.renderer.core
  (:require [reagent.core :refer [atom]]
            [reagent.dom :as rd]))

(enable-console-print!)

(defonce state (atom {:location "Bourron Marlotte, Île-de-France"
                      :icon-src "http://localhost:3000/developer.accuweather.com/sites/default/files/13-s.png"
                      :temperature-val 16
                      :temperature-unit "℃"
                      :weather-text "Mostly cloudy"
                      :updated-at "Updated as of 15:40"}))

(defn root-component []
  [:div.root-ctn
   [:div.search-ctn
    [:input.search-txt {:type "text"}]
    [:button.search-btn {:on-click #(println "Yo")} "Search"]]
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
