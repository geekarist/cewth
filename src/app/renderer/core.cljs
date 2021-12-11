(ns app.renderer.core
  (:require
   [reagent.dom :as rd]
   [app.renderer.weather :as weather]
   [reagent.core :as rc]))

(enable-console-print!)

(defn runtime [view init element-id]
  (rd/render
   [view (rc/atom init)]
   (js/document.getElementById element-id)))

(defn ^:dev/after-load start! []
  (runtime weather/view weather/init "app-container"))

(comment
  start!)