(ns rid3.b04-make-elements-from-data
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3]
   [rid3.basics-util :as util]
   ))

(def cursor-key :b04-make-elements-from-data)

(def height 20)
(def width 100)

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (reset! viz-ratom
            ;; when using an `:elem-with-data` piece, it will default
            ;; to look at the dataset key of your ratom
            {:dataset ["A" "B" "C"]})
    (fn [app-state]
      [:div
       [:h4 "4) Make elements based on an array of data"]
       [util/link-source (name cursor-key)]

       [rid3/viz
        {:id    "b04"
         :ratom viz-ratom

         :svg {:did-mount (fn [node ratom]
                            (-> node
                                (.attr "height" height)
                                (.attr "width" width)))}

         :pieces
         [{:kind            :elem-with-data
           :tag             "text"
           :class           "text-from-data"
           ;; note: by default, this is what the prepare-dataset
           ;; function will be ... so if you wanted, you could drop
           ;; this. I am showing it here for completeness.
           :prepare-dataset (fn [ratom]
                              (-> @ratom
                                  (get :dataset)
                                  clj->js))
           :did-mount       (fn [node ratom]
                              (let [dataset-n (-> @ratom
                                                  (get :dataset)
                                                  count)
                                    x-scale   (-> js/d3
                                                  .scaleLinear
                                                  (.rangeRound #js [0 width])
                                                  (.domain #js [0 dataset-n]))]
                                (-> node
                                    (.attr "x" (fn [d i]
                                                 (x-scale i)))
                                    (.attr "y" 12)
                                    (.attr "fill" "black")
                                    (.text (fn [d]
                                             d))
                                    )))}]
         }]])))
