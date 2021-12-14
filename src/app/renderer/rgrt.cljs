(ns app.renderer.rgrt
  (:require [reagent.dom :as rd]
            [reagent.core :as rc]))

(defn- view-component
  "View component. Operates on a reagent state atom. 
   Called each time the atom is changed."
  [state-ref update-fn view-fn handle]
  (let [dispatch-event (fn dispatch [event]
                         (let [new-state-vec (update-fn event @state-ref)
                               [new-state effect] new-state-vec]
                           (compare-and-set! state-ref @state-ref new-state)
                           (handle effect state-ref dispatch)))]
    (view-fn dispatch-event @state-ref)))

(defn make-program [init update-fn view-fn handle]
  (rd/render
   [view-component (rc/atom init) update-fn view-fn handle]
   (js/document.getElementById  "app-container")))

