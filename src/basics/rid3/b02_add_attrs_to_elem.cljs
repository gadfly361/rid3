(ns rid3.b02-add-attrs-to-elem
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3]
   [rid3.basics-util :as util]
   ))

(def cursor-key :b02-add-attrs-to-elem)

(def height 100)
(def width 100)

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (fn [app-state]
      [:div
       [:h4 "2) Add attributes to the rect (fill and stroke)"]
       [util/link-source (name cursor-key)]

       [rid3/viz
        {:id    "b02"
         :ratom viz-ratom

         :svg {:did-mount (fn [node ratom]
                            (-> node
                                (rid3/attrs
                                 {:height height
                                  :width  width})))}

         :pieces
         [{:kind      :elem
           :class     "some-element"
           :tag       "rect"
           :did-mount (fn [node ratom]
                        (-> node
                            (rid3/attrs
                             {:x            0
                              :y            0
                              :height       height
                              :width        width
                              ;; You can add arbitrary attributes to
                              ;; your element
                              :fill         "lightgrey"
                              :stroke       "grey"
                              :stroke-width 2})))}
          ]
         }]])))
