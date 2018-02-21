(ns rid3.b05-elements-from-arbitrary-data-location
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3]
   [rid3.basics-util :as util]
   ))

(def cursor-key :b05-elements-from-arbitrary-data-location)

(def height 20)
(def width 100)

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (reset! viz-ratom
            ;; when using an `:elem-with-data` piece, it will default
            ;; to look at the dataset key of your ratom. However, you
            ;; can put your data wherever you want.
            {:arbitrary-dataset-location ["A" "B" "C"]})
    (fn [app-state]
      [:div
       [:h4 "5) Make elements based on an array of data (where the
       dataset is located somewhere other than the "
        [:em ":dataset"]
        " key in
       your ratom)"]
       [util/link-source (name cursor-key)]

       [rid3/viz
        {:id    "b05"
         :ratom viz-ratom

         :svg {:did-mount (fn [node ratom]
                            (-> node
                                (rid3/attrs
                                 {:height height
                                  :width  width})))}

         :pieces
         [{:kind            :elem-with-data
           :tag             "text"
           :class           "text-from-arbitrary-data-source"
           ;; because we are using a key other than `:dataset` to
           ;; store our data, we need to pass in a :prepare-dataset
           ;; function
           :prepare-dataset (fn [ratom]
                              (-> @ratom
                                  (get :arbitrary-dataset-location)
                                  clj->js))
           :did-mount       (fn [node ratom]
                              (let [dataset-n (-> @ratom
                                                  (get :arbitrary-dataset-location)
                                                  count)
                                    x-scale   (-> js/d3
                                                  .scaleLinear
                                                  (.rangeRound #js [0 width])
                                                  (.domain #js [0 dataset-n]))]
                                (-> node
                                    (rid3/attrs
                                     {:x    (fn [d i]
                                              (x-scale i))
                                      :y    12
                                      :fill "black"})
                                    (.text (fn [d]
                                             d)))))}]
         }]])))
