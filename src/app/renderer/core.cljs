(ns app.renderer.core
  (:require
   [reagent.dom :as rd]
   [app.renderer.weather :as weather]))

(enable-console-print!)

(defn runtime [init update-fn view]
  (rd/render
   [view init update-fn]
   (js/document.getElementById "app-container")))

(defn ^:dev/after-load start! []
  (runtime weather/init 
           weather/update-fn 
           weather/view))

(comment
  start!)