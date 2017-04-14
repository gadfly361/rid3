(ns rid3.pie
  (:require
   [cljs.spec :as spec]
   [reagent.core :as reagent]
   [rid3.core :as d3]
   [rid3.demo-util :as dutil]
   ))


;; Example from:
;; https://bl.ocks.org/mbostock/3887235

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; util

(defn get-width [ratom]
  (let [page-width (get @ratom :page-width)]
    (max (min 500
              (- page-width 100))
         300)))

(defn get-height [ratom]
  (let [width (get-width ratom)]
    width))

(defn get-radius [ratom]
  (let [width (get-width ratom)]
    (/ width 2)))

(def pie
  (-> js/d3
      .pie
      (.value (fn [d]
                (aget d "population")))
      (.sort nil)))

(defn prepare-dataset [ratom]
  (-> (clj->js (get @ratom :dataset))
      pie))

(defn create-path [ratom]
  (let [radius (get-radius ratom)]
    (-> js/d3
        .arc
        (.outerRadius (- radius 10))
        (.innerRadius 0))))

(defn create-label [ratom]
  (let [radius (get-radius ratom)]
    (-> js/d3
        .arc
        (.outerRadius (- radius 40))
        (.innerRadius (- radius 40)))))

(def color
  (-> js/d3
      (.scaleOrdinal #js ["#98abc5" "#8a89a6" "#7b6888" "#6b486b" "#a05d56" "#d0743c" "#ff8c00"])))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; elements

(defn svg [node ratom]
  (let [width (get-width ratom)
        height (get-height ratom)]
    (-> node
        (.attr "width" width)
        (.attr "height" height))))

(defn main-container [node ratom]
  (let [width  (get-width ratom)
        height (get-height ratom)]
    (-> node
        (.attr "transform" (str "translate("
                                (/ width 2)
                                ","
                                (/ height 2) ")")))))

(defn arc [node ratom]
  (let [path (create-path ratom)]
    (-> node
        (.attr "d" path)
        (.attr "fill" (fn [d]
                        (color (aget d "data" "age"))))
        (.style "stroke" "#FFF"))))

(defn text-label [node ratom]
  (let [label (create-label ratom)]
    (-> node
        (.attr "transform" (fn [d]
                             (str "translate(" (.centroid label d) ")")))
        (.attr "dy" "0.35em")
        (.text (fn [d]
                 (aget d "data" "age")))

        (.style "font" "10px sans-serif")
        (.style "text-anchor" "middle"))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; viz

(defn viz [ratom]
  [d3/viz
   {:id              "piechart"
    :ratom           ratom
    :prepare-dataset prepare-dataset
    :svg             {:did-mount svg}
    :main-container  {:did-mount main-container}
    :pieces
    [{:kind      :elem-with-data
      :class     "arc"
      :tag       "path"
      :did-mount arc}
     {:kind      :elem-with-data
      :class     "text-label"
      :tag       "text"
      :did-mount text-label}]}])



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; dataset

(def dataset
  [{:age "<5" :population 2704659}
   {:age "5-13" :population 4499890}
   {:age "14-17" :population 2159981}
   {:age "18-24" :population 3853788}
   {:age "25-44" :population 14106543}
   {:age "45-64" :population 8819342}
   {:age "≥65" :population 612463}])

(defn randomize-dataset [ratom]
  (swap! ratom assoc :dataset
         (drop-last (rand-int 6) dataset)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; example

(defn example [ratom]
  [:div
   [dutil/title "Pie Chart" "pie"]
   [dutil/btn-randomize-data ratom randomize-dataset]
   [viz ratom]
   ])
