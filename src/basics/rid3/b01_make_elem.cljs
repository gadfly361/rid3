(ns rid3.b01-make-elem
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3 :refer [rid3->]]
   [rid3.basics-util :as util]
   ))

(def cursor-key :b01-make-elem)

(def height 100)
(def width 100)

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (fn [app-state]
      [:div
       [:h4 "1) Add a simple element: a rect"]
       [util/link-source (name cursor-key)]

       [rid3/viz
        {:id    "b01"
         :ratom viz-ratom

         ;; This is required to, at a minimum, set the dimensions of
         ;; your svg.  Think of your `svg` as a whiteboard that you are
         ;; going to draw stuff on
         :svg {:did-mount (fn [node ratom]
                            (rid3-> node
                                    {:height height
                                     :width  width}))}

         ;; Think of pieces as the things you are drawing on your
         ;; whiteboard.
         :pieces
         [{:kind      :elem
           :class     "some-element"
           :tag       "rect"
           :did-mount (fn [node ratom]
                        (rid3-> node
                                {:x      0
                                 :y      0
                                 :height height
                                 :width  width}))}
          ]
         }]])))
