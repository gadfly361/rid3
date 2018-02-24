(ns rid3.b03-add-element-on-top
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3 :refer [rid3->]]
   [rid3.basics-util :as util]
   ))

(def cursor-key :b03-add-element-on-top)

(def height 100)
(def width 100)

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (fn [app-state]
      [:div
       [:h4 "3) Add an element on top of another element: a circle on a rect"]
       [util/link-source (name cursor-key)]

       [rid3/viz
        {:id    "b03"
         :ratom viz-ratom

         :svg {:did-mount (fn [node ratom]
                            (rid3-> node
                                    {:height height
                                     :width  width}))}

         :pieces
         [{:kind      :elem
           :class     "some-element"
           :tag       "rect"
           :did-mount (fn [node ratom]
                        (rid3-> node
                                {:x            0
                                 :y            0
                                 :height       height
                                 :width        width
                                 :fill         "lightgrey"}))}

          ;; You can add any number of elements. They are added in
          ;; order, which means this circle overlaid on top of the
          ;; rect above."
          {:kind      :elem
           :class     "some-element-on-top"
           :tag       "circle"
           :did-mount (fn [node ratom]
                        (rid3-> node
                            {:cx   (/ width 2)
                             :cy   (/ height 2)
                             :r    20
                             :fill "green"}))}
          ]}]])))
