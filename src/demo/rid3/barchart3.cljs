(ns rid3.barchart3
  (:require
   [garden.core :refer [css]]
   [rid3.core :as d3]
   [rid3.demo-util :as dutil]
   ))


;; Example from:
;; https://bl.ocks.org/alandunning/274bf248fd0f362d64674920e85c1eb7

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; vars

(def margin
  {:top    20
   :right  20
   :bottom 30
   :left   40})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; util fns

(defn get-width [ratom]
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
        width (get-width ratom)
        domain  (map :area dataset)]
    (-> js/d3
        .scaleBand
        (.rangeRound #js [0 width])
        (.padding 0.15)
        (.domain (clj->js domain)))))

(defn create-y-scale [ratom]
  (let [dataset (get @ratom :dataset)
        height (get-height ratom)
        max-y   (apply max (map :value dataset))]
    (-> js/d3
        .scaleLinear
        (.rangeRound #js [height 0])
        (.domain #js [0 max-y]))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; axis

(defn x-axis [node ratom]
  (let [height (get-height ratom)
        x-scale (create-x-scale ratom)]
    (-> node
        (.attr "transform" (str "translate(0," height ")"))
        (.call (.axisBottom js/d3 x-scale)))
    (-> node
        (.select "path")
        (.style "stroke" "none"))))

(defn y-axis [node ratom]
  (let [width (get-width ratom)
        y-scale (create-y-scale ratom)]
    (-> node
        (.call (-> (.axisLeft js/d3 y-scale)
                   (.ticks 5)
                   (.tickFormat (fn [d]
                                  (str (js/parseInt (/ d 1000))
                                       "K")))
                   (.tickSizeInner #js [(- width)])
                   )))))

(defn y-label [node ratom]
  (-> node
      (.attr "transform" "rotate(-90)")
      (.attr "y" 6)
      (.attr "dy" "0.71em")
      (.attr "text-anchor" "end")
      (.attr "font-size" 10)
      (.attr "font-family" "sans-serif")
      (.attr "fill" "#5D6971")
      (.text "Average House Price - (£)")))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; bar

(def colors
  (-> js/d3
      .scaleOrdinal
      (.range #js ["#6F257F" "#CA0D59"])))

(defn bar [node ratom]
  (let [height (get-height ratom)
        x-scale (create-x-scale ratom)
        y-scale (create-y-scale ratom)]
    (-> node
        (.attr "x" (fn [d]
                     (x-scale (aget d "area"))))
        (.attr "y" (fn [d]
                     (y-scale (aget d "value"))))
        (.attr "width" (.bandwidth x-scale))
        (.attr "height" (fn [d]
                          (- height (y-scale (aget d "value")))))
        (.attr "fill" (fn [d]
                        (colors (aget d "area"))))
        (.on "mousemove" (fn [d]
                           (-> (js/d3.select "#b3-tooltip")
                               (.style "left" (str (- js/d3.event.pageX
                                                      50)
                                                   "px"))
                               (.style "top" (str (- js/d3.event.pageY
                                                     70)
                                                  "px"))
                               (.style "display" "inline-block")
                               (.html (str (aget d "area") "<br>" "£" (aget d "value")))
                               )))
        (.on "mouseout" (fn [d]
                          (-> (js/d3.select "#b3-tooltip")
                              (.style "display" "none"))))
        )))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; viz

(defn viz [ratom]
  [d3/viz
   {:id             "barchart3"
    :ratom          ratom
    :svg            {:did-mount svg}
    :main-container {:did-mount main-container}
    :pieces
    [{:kind      :raw
      :did-mount (fn []
                   (-> (js/d3.select "#barchart3")
                       (.append "div")
                       (.attr "id" "b3-tooltip")))}
     {:kind  :container
      :class "axis"
      :children
      [{:kind       :container
        :class      "x-axis"
        :did-mount  x-axis}
       {:kind      :container
        :class     "y-axis"
        :did-mount y-axis}]}
     {:kind      :elem
      :class     "y-label"
      :tag       "text"
      :did-mount y-label}
     {:kind      :elem-with-data
      :class     "bar"
      :tag       "rect"
      :did-mount bar}
     ]}])



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; dataset

(def dataset
  [{:area "regional" :value 190000}
   {:area "national" :value 250000}])

(defn randomize-dataset [ratom]
  (let [create-x (fn [] (* 10000 (max 1 (rand-int 30))))]
    (swap! ratom update :dataset
           (fn [points]
             (mapv #(hash-map :value (create-x)
                              :area (:area %))
                   points)))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; example

(defn style []
  [:style
   (css [[:#b3-tooltip
          {:position   "absolute"
           :display    "none"
           :min-width  "80px"
           :height     "auto"
           :background "none repeat scroll 0 0 #ffffff"
           :border     "1px solid #6F257F"
           :padding    "14px"
           :text-align "center"}]
         [:#barchart3
          [:svg
           {:background-color "#F1F3F3"}]

          [:.axis
          [:text
           {:font "15px sans-serif"}]]

          [:.axis
           [:path :line
            {:fill            "none"
             :stroke          "#D4D8DA"
             :stroke-width    "1px"
             :shape-rendering "crisEdges"}
            ]]]])])

(defn example [ratom]
  [:div
   [dutil/title "Barchart 3 (with tooltip)" "barchart3"]
   [dutil/btn-randomize-data ratom randomize-dataset]
   [style]
   [viz ratom]
   ])
