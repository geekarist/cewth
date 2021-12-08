(ns app.renderer.core
  (:require
   [reagent.dom :as rd]
   [app.renderer.weather :as weather]))

(enable-console-print!)

(defn ^:dev/after-load start! []
  (rd/render
   [weather/view weather/dispatch]
   (js/document.getElementById "app-container")))

(comment
  start!)