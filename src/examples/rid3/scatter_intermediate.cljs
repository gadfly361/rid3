(ns rid3.scatter-intermediate
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3]
   [rid3.examples-util :as util]
   [goog.object :as gobj]
   ))

(def cursor-key :scatter-intermediate)

(def height 140)
(def width 260)

(def margin {:top    8
             :left   32
             :right  16
             :bottom 40})

(def transition-duration 800)

(defn translate [left top]
  (str "translate("
       (or left 0)
       ","
       (or top 0)
       ")"))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Dataset

(defn ->mock-dataset []
  (drop 1
        (reductions (fn [m d]
                      (hash-map
                       :label d
                       :value (+ (get m :value)
                                 (rand-int 5))))
                    {}
                    ["Jan" "Feb" "Mar" "Apr" "May" "Jun" "Jul" "Aug" "Sep" "Oct" "Nov" "Dec"])))

(defn prepare-dataset [ratom]
  (-> @ratom
      (get :dataset)
      clj->js))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Scales

(defn ->x-scale [ratom]
  (let [{:keys [dataset]} @ratom
        labels            (mapv :label dataset)]
    (-> js/d3
        .scaleBand
        (.rangeRound #js [0 width])
        (.padding 0.1)
        (.domain (clj->js labels)))))

(defn ->y-scale [ratom]
  (let [{:keys [dataset]} @ratom
        values            (mapv :value dataset)
        max-value         (apply max values)]
    (-> js/d3
        .scaleLinear
        (.rangeRound #js [height 0])
        (.domain #js [0 max-value]))))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Axes

(defn lighten-axis [node]
  ;; lighten path
  (-> node
      (.select "path")
      (rid3/attrs {:style {:stroke "lightgrey"}}))

  ;; light text
  (-> node
      (.selectAll ".tick text")
      (rid3/attrs {:style {:fill "#404040"}}))

  ;; light line
  (-> node
      (.selectAll ".tick line")
      (rid3/attrs {:style {:stroke "lightgrey"}})))

(defn x-axis-did-mount [node ratom]
  (let [x-scale (->x-scale ratom)]
    (-> node
        (rid3/attrs
         {:transform (translate 0 height)})
        (.call (.axisBottom js/d3 x-scale)))
    (-> node
        (.selectAll "text")
        (rid3/attrs
         {:dx        "-1.7em"
          :dy        "-0.1em"
          :transform "rotate(-60)"}))
    (lighten-axis node)))


(defn ->y-axis [did-mount?]
  (fn [node ratom]
    (let [y-scale (->y-scale ratom)]

      ;; create axis
      (if did-mount?
        (-> node
            (.call (-> (.axisLeft js/d3 y-scale)
                       (.ticks 4))))
        ;; Add did-update version that transitions on re-render (i.e.,
        ;; when data changes)
        (-> node
            .transition
            (.duration transition-duration)
            (.call (-> (.axisLeft js/d3 y-scale)
                       (.ticks 3)))))

      (lighten-axis node))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; dots

(defn ->dots [did-mount?]
  (fn [node ratom]
    (let [y-scale (->y-scale ratom)
          x-scale (->x-scale ratom)]
      (-> node
          ;; common
          (rid3/attrs
           {:r    4
            :fill "#3366CC"
            :cx   (fn [d]
                    (let [label (gobj/get d "label")]
                      (x-scale label)))}))

      (if did-mount?
        ;; did-mount
        (-> node
            (rid3/attrs
             {:cy      (fn [d]
                         (let [value (gobj/get d "value")]
                           (y-scale value)))
              :opacity 0})
            .transition
            (.duration transition-duration)
            (rid3/attrs
             {:opacity 1}))

        ;; did-update
        (-> node
            .transition
            (.duration transition-duration)
            (rid3/attrs
             {:cy (fn [d]
                    (let [value (gobj/get d "value")]
                      (y-scale value)))})))
      )))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Example

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (reset! viz-ratom {:dataset (->mock-dataset)})
    (fn [app-state]
      [:div
       [:h4 "Intermediate Scatter Plot"]
       [util/link-source (name cursor-key)]
       [:button
        {:on-click #(swap! viz-ratom assoc :dataset (->mock-dataset))}
        "Randomize data"]

       [rid3/viz
        {:id    "scatter-intermediate"
         :ratom viz-ratom
         :svg   {:did-mount
                 (fn [node ratom]
                   (-> node
                       (rid3/attrs
                        {:width  (+ width
                                    (get margin :left)
                                    (get margin :right))
                         :height (+ height
                                    (get margin :top)
                                    (get margin :bottom))})))}

         :main-container {:did-mount
                          (fn [node ratom]
                            (-> node
                                (rid3/attrs
                                 {:transform (translate
                                              (get margin :left)
                                              (get margin :right))})))}
         :pieces
         [{:kind      :container
           :class     "x-axis"
           :did-mount x-axis-did-mount}

          {:kind       :container
           :class      "y-axis"
           :did-mount  (->y-axis true)
           :did-update (->y-axis false)}

          {:kind            :elem-with-data
           :class           "dots"
           :tag             "circle"
           :prepare-dataset prepare-dataset
           :did-mount       (->dots true)
           :did-update      (->dots false)
           }]
         }]
       ])))
