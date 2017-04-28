(ns rid3.barchart
  (:require
   [rid3.core :as d3]
   [rid3.demo-util :as dutil]
   ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(def margin {:top    20
             :right  20
             :bottom 20
             :left   0})

(def transition-duration 800)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Util Fns

(defn get-width [ratom]
  (let [page-width (get @ratom :page-width)]
    (max (min 400
              (- page-width 100))
         260)))

(defn get-height [ratom]
  (let [width (get-width ratom)]
    (/ width 2)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Container

(defn main-container-did-mount [node ratom]
  (-> node
      (.attr "transform" (str "translate("
                              (get margin :left)
                              ","
                              (get margin :top)
                              ")"))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SVG

(defn svg-did-mount [node ratom]
  (-> node
      (.attr "width" (+ (get-width ratom)
                        (get margin :left 0)
                        (get margin :right 0)))
      (.attr "height" (+ (get-height ratom)
                         (get margin :top 0)
                         (get margin :bottom 0)))))


(defn svg-did-update [node ratom]
  (-> node
      .transition
      (.duration transition-duration)
      (.attr "width" (+ (get-width ratom)
                        (get margin :left 0)
                        (get margin :right 0)))
      (.attr "height" (+ (get-height ratom)
                         (get margin :top 0)
                         (get margin :bottom 0)))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bar

(defn bar-did-mount [node ratom]
  (let [width       (get-width ratom)
        height      (get-height ratom)
        data-n      (count (get @ratom :dataset))
        rect-height (/ height data-n)
        x-scale     (-> js/d3
                        .scaleLinear
                        (.domain #js [0 5])
                        (.range #js [0 width]))]
    (-> node
        (.style "shape-rendering" "crispEdges")
        (.attr "fill" "green")
        (.attr "x" (x-scale 0))
        (.attr "y" (fn [_ i]
                     (* i rect-height)))
        (.attr "height" (- rect-height 1))
        (.attr "opacity" 0.2)
        .transition
        (.duration transition-duration)
        (.attr "width" (fn [d]
                         (x-scale (aget d "x"))))
        (.attr "opacity" 1)
        )))

(defn bar-did-update [node ratom]
  (let [width       (get-width ratom)
        height      (get-height ratom)
        data-n      (count (get @ratom :dataset))
        rect-height (/ height data-n)
        x-scale     (-> js/d3
                        .scaleLinear
                        (.domain #js [0 5])
                        (.range #js [0 width]))]
    (-> node
        (.style "shape-rendering" "crispEdges")
        (.attr "fill" "green")
        (.attr "opacity" 0.8)
        (.attr "y" (fn [_ i]
                     (* i rect-height)))
        .transition
        (.duration transition-duration)
        (.attr "opacity" 1)
        (.attr "height" (- rect-height 1))
        (.attr "x" (x-scale 0))
        (.attr "width" (fn [d]
                         (x-scale (aget d "x")))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bar Label

(defn bar-label-common [node ratom]
  (let [width       (get-width ratom)
        height      (get-height ratom)
        data-n      (count (get @ratom :dataset))
        rect-height (/ height data-n)]
    (-> node
        (.attr "y" (fn [_ i]
                     (+ (* i rect-height)
                        (/ rect-height 2))))
        (.attr "alignment-baseline" "middle")
        (.attr "fill" "grey")
        (.attr "font-family" "sans-serif")
        (.attr "font-size" "20")
        (.text (fn [d] (aget d "x"))))))

(defn bar-label-did-mount [node ratom]
  (let [width   (get-width ratom)
        x-scale (-> js/d3
                    .scaleLinear
                    (.domain #js [0 5])
                    (.range #js [0 width]))]
    (-> node
        (bar-label-common ratom)
        .transition
        (.duration transition-duration)
        (.attr "x" (fn [d] (+ (x-scale (aget d "x"))
                              8))))))

(defn bar-label-did-update [node ratom]
  (let [width   (get-width ratom)
        x-scale (-> js/d3
                    .scaleLinear
                    (.domain #js [0 5])
                    (.range #js [0 width]))]
    (-> node
        (bar-label-common ratom)
        .transition
        (.duration transition-duration)
        (.attr "x" (fn [d] (+ (x-scale (aget d "x"))
                              8))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Viz

(defn viz [ratom]
  [d3/viz
   {:id             "barchart"
    :ratom          ratom
    :svg            {:did-mount  svg-did-mount
                     :did-update svg-did-update}
    :main-container {:did-mount main-container-did-mount}
    :pieces
    [{:kind  :container
      :class "bars"
      :children
      [{:kind       :elem-with-data
        :class      "bar"
        :tag        "rect"
        :did-mount  bar-did-mount
        :did-update bar-did-update}

       {:kind       :elem-with-data
        :class      "bar-label"
        :tag        "text"
        :did-mount  bar-label-did-mount
        :did-update bar-label-did-update}]}
     ]}])



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data

(def dataset
  [{:x 5}
   {:x 2}
   {:x 3}])

(defn randomize-dataset [ratom]
  (let [points-n (max 2 (rand-int 8))
        points   (range points-n)
        create-x (fn [] (max 1 (rand-int 5)))]
    (swap! ratom update :dataset
           (fn []
             (mapv #(hash-map :x (create-x))
                   points)))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Example

(defn example [ratom]
  [:div
   [dutil/title "Barchart" "barchart"]
   [dutil/btn-randomize-data ratom randomize-dataset]
   [viz ratom]
   ])
