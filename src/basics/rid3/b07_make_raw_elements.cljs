(ns rid3.b07-make-raw-elements
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3 :refer [rid3->]]
   [rid3.basics-util :as util]
   ))

(def cursor-key :b07-make-raw-elements)

(def height 100)
(def width 100)

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (fn [app-state]
      [:div
       [:h4 "7) element on top of another element ... using the "
        [:em ":raw"]
        " piece kind (roughly same as 2, but different implementation)"]
       [util/link-source (name cursor-key)]

       [rid3/viz
        {:id    "b07"
         :ratom viz-ratom

         :svg {:did-mount (fn [node ratom]
                            (rid3-> node
                                    {:height height
                                     :width  width}))}

         ;; Using a :raw piece is like an escape hatch from
         ;; rid3. However, this means that a lot of the conveniences
         ;; that rid3 provides, you now have to perform manually.

         :pieces
         [{:kind :raw
           ;; Note: you do not supply a :class tag anymore

           ;; Note: you do not supply a :tag key anymore. You will have
           ;; to manually append and select your own elements

           :did-mount (fn [ratom]
                        ;; note: you are no longer passed a node argument
                        (let [node-main-container (js/d3.select "#b07 .rid3-main-container")]
                          (rid3-> node-main-container
                                  (.append "rect")
                                  {:class        "some-raw-element"
                                   :x            0
                                   :y            0
                                   :height       height
                                   :width        width
                                   :fill         "lightgrey"
                                   :stroke       "grey"
                                   :stroke-width 2})))

           ;; Special note: the :did-mount function is *not* default
           ;; to the :did-upate function like the other kinds of
           ;; pieces
           }

          {:kind      :elem
           :class     "some-raw-element-on-top"
           :did-mount (fn [node ratom]
                        (let [node-main-container (js/d3.select "#b07 .rid3-main-container")]
                          (rid3-> node-main-container
                                  (.append "circle")
                                  {:class "some-raw-element-on-top"
                                   :cx    (/ width 2)
                                   :cy    (/ height 2)
                                   :r     20
                                   :fill  "green"})))}]
         }]])))
