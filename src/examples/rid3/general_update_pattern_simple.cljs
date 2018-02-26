(ns rid3.general-update-pattern-simple
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3 :refer [rid3->]]
   [rid3.examples-util :as util]
   ))

(def cursor-key :general-update-pattern-simple)

(def height 40)
(def width 260)

(def alphabet
  ["A" "B" "C" "D" "E" "F" "G" "H" "I" "J" "K" "L" "M" "N" "O" "P" "Q" "R" "S" "T" "U" "V" "W" "X" "Y" "Z"])

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (reset! viz-ratom {:dataset alphabet})
    (js/setInterval (fn []
                      (swap! viz-ratom assoc :dataset (->> alphabet
                                                           shuffle
                                                           (take (rand-int 26))
                                                           sort)))
                    3000)
    (fn [app-state]
      [:div
       [:h4 "Simple GUP (w/ transitions on enter and exit)"]
       [util/link-source (name cursor-key)]

       [rid3/viz
        {:id             (name cursor-key)
         :ratom          viz-ratom
         :svg            {:did-mount (fn [node ratom]
                                       (rid3-> node
                                               {:height height
                                                :width  width}))}
         :main-container {:did-mount (fn [node ratom]
                                       (-> node
                                           (.attr "transform" "translate(16,20)")))}
         :pieces
         [{:kind            :elem-with-data
           :tag             "text"
           :class           "gup-text"
           :prepare-dataset (fn [ratom]
                              (-> @ratom
                                  (get :dataset)
                                  clj->js))
           :key-fn          (fn [d] d)

           ;; Instead of using `:did-mount`, for `:elem-with-data`
           ;; pieces, you can instead use `:gup`. This will give you
           ;; more control and will allow you to explicitly define the
           ;; enter, update, and exit parts of the general update
           ;; pattern.

           :gup
           {:enter  (fn [node ratom]
                      (rid3-> node
                              {:fill        "green"
                               :dy          ".35em"
                               :x           (fn [d i]
                                              (* i 9))
                               :y           -20
                               :text-anchor "middle"
                               :font-size   10
                               :style       {:fill-opacity 1e-6}}
                              (.text (fn [d] d))
                              .transition
                              (.duration 800)
                              {:y     0
                               :style {:fill-opacity 1}}))
            :update (fn [node ratom]
                      (rid3-> node
                              {:fill  "grey"
                               :y     0
                               :style {:fill-opacity 1}}
                              .transition
                              (.duration 800)
                              {:x (fn [d i]
                                    (* i 9))}))
            :exit   (fn [node ratom]
                      (rid3-> node
                              {:fill "brown"}
                              .transition
                              (.duration 800)
                              {:y     20
                               :style {:fill-opacity 1e-6}}))}}
          ]}]])))
