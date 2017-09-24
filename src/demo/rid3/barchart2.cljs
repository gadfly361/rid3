(ns rid3.barchart2
  (:require
   [rid3.core :as rid3]
   [rid3.demo-util :as dutil]
   ))


;; Example from:
;; https://bl.ocks.org/mbostock/3885304

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; vars

(def margin
  {:top    20
   :right  20
   :bottom 30
   :left   40})



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; util fns

(defn get-width
  [ratom]
  (let [page-width (get @ratom :page-width)]
    (max (min 900
              (- page-width 100))
         260)))

(defn get-height [ratom]
  (let [width (get-width ratom)]
    (/ width 2)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; svg

(defn svg [node ratom]
  (let [width (get-width ratom)
        height (get-height ratom)]
    (-> node
        (.attr "width" (+ width
                          (get margin :left)
                          (get margin :right)))
        (.attr "height" (+ height
                           (get margin :top)
                           (get margin :bottom))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main-container

(defn main-container [node]
  (-> node
      (.attr "transform" (str "translate("
                              (get margin :left)
                              ","
                              (get margin :top) ")"))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; scales

(defn create-x-scale [ratom]
  (let [dataset (get @ratom :dataset)
        width   (get-width ratom)
        domain  (map :letter dataset)]
    (-> js/d3
        .scaleBand
        (.rangeRound #js [0 width])
        (.padding 0.1)
        (.domain (clj->js domain)))))

(defn create-y-scale [ratom]
  (let [dataset (get @ratom :dataset)
        height  (get-height ratom)
        max-y   (apply max (map :frequency dataset))]
    (-> js/d3
        .scaleLinear
        (.rangeRound #js [height 0])
        (.domain #js [0 max-y]))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; axis

(defn x-axis [node ratom]
  (let [height  (get-height ratom)
        x-scale (create-x-scale ratom)]
    (-> node
        (.attr "transform" (str "translate(0," height ")"))
        (.call (.axisBottom js/d3 x-scale)))
    (-> node
        (.select "path")
        (.style "stroke" "none"))))

(defn y-axis [node ratom]
  (let [y-scale (create-y-scale ratom)]
    (-> node
        (.call (-> (.axisLeft js/d3 y-scale)
                   (.ticks 10 "%"))))))

(defn y-label [node ratom]
  (-> node
      (.attr "transform" "rotate(-90)")
      (.attr "y" 6)
      (.attr "dy" "0.71em")
      (.attr "text-anchor" "end")
      (.attr "font-size" 10)
      (.attr "font-family" "sans-serif")
      (.text "Frequency")))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; bar

(defn bar [node ratom]
  (let [height  (get-height ratom)
        x-scale (create-x-scale ratom)
        y-scale (create-y-scale ratom)]
    (-> node
        (.style "fill" "steelblue")
        (.on "mouseover" (fn []
                           (this-as this
                             (-> (js/d3.select this)
                                 (.style "fill" "brown")))))
        (.on "mouseout" (fn []
                          (this-as this
                            (-> (js/d3.select this)
                                (.style "fill" "steelblue")))))
        (.attr "x" (fn [d]
                     (x-scale (aget d "letter"))))
        (.attr "y" (fn [d]
                     (y-scale (aget d "frequency"))))
        (.attr "width" (.bandwidth x-scale))
        (.attr "height" (fn [d]
                          (- height
                             (y-scale (aget d "frequency"))))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; dataset

(def dataset
  [{:letter "A" :frequency .08167}
   {:letter "B" :frequency .01492}
   {:letter "C" :frequency .02782}
   {:letter "D" :frequency .04253}
   {:letter "E" :frequency .12702}
   {:letter "F" :frequency .02288}
   {:letter "G" :frequency .02015}
   {:letter "H" :frequency .06094}
   {:letter "I" :frequency .06966}
   {:letter "J" :frequency .00153}
   {:letter "K" :frequency .00772}
   {:letter "L" :frequency .04025}
   {:letter "M" :frequency .02406}
   {:letter "N" :frequency .06749}
   {:letter "O" :frequency .07507}
   {:letter "P" :frequency .01929}
   {:letter "Q" :frequency .00095}
   {:letter "R" :frequency .05987}
   {:letter "S" :frequency .06327}
   {:letter "T" :frequency .09056}
   {:letter "U" :frequency .02758}
   {:letter "V" :frequency .00978}
   {:letter "W" :frequency .02360}
   {:letter "X" :frequency .00150}
   {:letter "Y" :frequency .01974}
   {:letter "Z" :frequency .00074}])


(defn randomize-dataset [ratom]
  (swap! ratom assoc :dataset
         (drop-last (rand-int 25) dataset)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; viz

(defn viz [ratom]
  [rid3/viz
   {:id             "barchart2"
    :ratom          ratom
    :svg            {:did-mount svg}
    :main-container {:did-mount main-container}
    :pieces
    [{:kind      :elem-with-data
      :class     "bar"
      :tag       "rect"
      :did-mount bar}
     {:kind  :container
      :class "axis"
      :children
      [{:kind      :container
        :class     "x-axis"
        :did-mount x-axis}
       {:kind      :container
        :class     "y-axis"
        :did-mount y-axis}]}
     {:kind      :elem
      :class     "y-label"
      :tag       "text"
      :did-mount y-label}
     ]}])



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; example

(defn example [ratom]
  [:div
   [dutil/title "Barchart 2" "barchart2"]
   [dutil/btn-randomize-data ratom randomize-dataset]
   [viz ratom]
   ])
