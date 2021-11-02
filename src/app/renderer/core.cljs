(ns app.renderer.core
  (:require [reagent.core :refer [atom]]
            [reagent.dom :as rd]))

(enable-console-print!)

(defonce state (atom 0))

(defn root-component []
  [:div.root-ctn
   [:div.search-ctn
    [:input {:type "text"}]
    [:button {:on-click #(println "Yo")} "Search"]]
   [:div.result-ctn
    [:div "Bourron Marlotte, Île-de-France"]
    [:div.temperature-ctn
     [:img.weather-icn {:src "http://localhost:3000/developer.accuweather.com/sites/default/files/13-s.png"}]
     [:span.temperature-lbl "14"]
     [:span.unit-lbl "℃"]]
    [:div.weather-text-blk "Mostly cloudy"]
    [:div.update-date-blk "Updated as of 15:40"]]])

(defn root-component-2 []
  [:div
   [:div.logos
    [:img.electron {:src "img/electron-logo.png"}]
    [:img.cljs {:src "img/cljs-logo.svg"}]
    [:img.reagent {:src "img/reagent-logo.png"}]]
   [:button
    {:on-click #(swap! state inc)}
    (str "Clicked " @state " times")]])

(defn ^:dev/after-load start! []
  (rd/render
   [root-component]
   (js/document.getElementById "app-container")))
