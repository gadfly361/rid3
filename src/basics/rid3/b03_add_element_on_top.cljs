(ns rid3.b03-add-element-on-top
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3]
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
                            (-> node
                                (.attr "height" height)
                                (.attr "width" width)))}

         :pieces
         [{:kind      :elem
           :class     "some-element"
           :tag       "rect"
           :did-mount (fn [node ratom]
                        (-> node
                            (.attr "x" 0)
                            (.attr "y" 0)
                            (.attr "height" height)
                            (.attr "width" width)
                            (.attr "fill" "lightgrey")
                            (.attr "stroke" "grey")
                            (.attr "stroke-width" 2)))}

          ;; You can add any number of elements. They are added in
          ;; order, which means this circle overlaid on top of the
          ;; rect above."
          {:kind      :elem
           :class     "some-element-on-top"
           :tag       "circle"
           :did-mount (fn [node ratom]
                        (-> node
                            (.attr "cx" (/ width 2))
                            (.attr "cy" (/ height 2))
                            (.attr "r" 20)
                            (.attr "fill" "green")))}]
         }]])))
