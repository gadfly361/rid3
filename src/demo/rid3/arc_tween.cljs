(ns rid3.arc-tween
  (:require
   [rid3.core :as rid3]
   [rid3.demo-util :as dutil]
   ))


;; Example from:
;; http://bl.ocks.org/mbostock/5100636

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; vars

(def tau
  (* 2 js/Math.PI))



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


(defn create-arc [ratom]
  (let [width  (get-width ratom)
        radius (/ width 2)]
    (-> js/d3
        .arc
        (.innerRadius (* 0.8 radius))
        (.outerRadius radius)
        (.startAngle 0))))

(defn arc-tween [new-angle ratom]
  (fn [d]
    (let [end-angle   (aget d "endAngle")
          interpolate (js/d3.interpolate end-angle new-angle)]
      (fn [t]
        (let [arc (create-arc ratom)]
          (set! (.-endAngle d) (interpolate t))
          (arc d))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; svg

(defn svg [node ratom]
  (let [width  (get-width ratom)
        height (get-height ratom)]
    (-> node
        (.attr "width" width)
        (.attr "height" height))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main container

(defn main-container [node ratom]
  (let [width  (get-width ratom)
        height (get-height ratom)]
    (-> node
        (.attr "transform" (str "translate(" (/ width 2)
                                ","
                                (/ height 2) ")")))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; background

(defn background [node ratom]
  (let [arc (create-arc ratom)]
    (-> node
        (.datum #js {:endAngle tau})
        (.style "fill" "#ddd")
        (.attr "d" arc))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; foreground

(defn foreground-common [node ratom]
  (let [arc (create-arc ratom)]
    (-> node
        (.datum #js {:endAngle (* 0.127 tau)})
        (.style "fill" "orange")
        (.attr "d" arc))))


(defn foreground-did-mount [node ratom]
  (foreground-common node ratom)
  (js/d3.interval
   (fn []
     (-> node
         .transition
         (.duration 750)
         (.attrTween "d" (arc-tween (* (js/Math.random) tau)
                                    ratom))))
   1500))

(defn foreground-did-update [node ratom]
  (foreground-common node ratom))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; viz

(defn viz [ratom]
  [rid3/viz
   {:id             "arc-tween"
    :ratom          ratom
    :svg            {:did-mount svg}
    :main-container {:did-mount main-container}
    :pieces
    [{:kind      :elem
      :class     "background"
      :tag       "path"
      :did-mount background}
     {:kind       :elem
      :class      "foreground"
      :tag        "path"
      :did-mount  foreground-did-mount
      :did-update foreground-did-update}]}])



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; example

(defn example [ratom]
  [:div
   [dutil/title "Arc Tween" "arc_tween"]
   [viz ratom]
   ])
