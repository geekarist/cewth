(ns app.renderer.rgrt
  "Reagent runtime"
  (:require [reagent.dom :as rd]
            [reagent.core :as rc]))

(defn- view-component
  "View component. Operates on a reagent state atom. 
   Called each time the atom is changed."
  [state-ref update-fn view-fn handle-effect!]
  (letfn [(dispatch-event!
            [event]
            (let [new-state-vec (update-fn event @state-ref)
                  [new-state effect] new-state-vec]
              (compare-and-set! state-ref @state-ref new-state)
              (handle-effect! effect dispatch-event!)))]
    (view-fn dispatch-event! @state-ref)))

(defn run-program
  "Create and run a program:
   - `init`: initial state data
   - `update-fn`: state update function
   - `view-fn`: view function
   - `handle-effect!`: map keys to effects"
  [init update-fn view-fn handle-effect!]
  (rd/render
   [view-component (rc/atom init) update-fn view-fn handle-effect!]
   (js/document.getElementById  "app-container")))

