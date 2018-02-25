(ns rid3.bar-intermediate
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3 :refer [rid3->]]
   [rid3.examples-util :as util]
   [goog.object :as gobj]
   ))

(def cursor-key :bar-intermediate)

(def height 140)
(def width 260)

(def margin {:top    8
             :left   32
             :right  16
             :bottom 40})

(defn translate [left top]
  (str "translate("
       (or left 0)
       ","
       (or top 0)
       ")"))

(def color
  (-> js/d3
      (.scaleOrdinal #js ["#3366CC"
                          "#DC3912"
                          "#FF9900"])))

(def transition-duration 800)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Dataset

(defn ->mock-dataset []
  (mapv #(hash-map :label %
                   :value (rand-int 200))
        ["A" "B" "C"]))

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
  (rid3-> node
          (.select "path")
          {:style {:stroke "lightgrey"}})

  ;; light text
  (rid3-> node
          (.selectAll ".tick text")
          {:style {:fill "#404040"}})

  ;; light line
  (rid3-> node
          (.selectAll ".tick line")
          {:style {:stroke "lightgrey"}}))


(defn x-axis-did-mount [node ratom]
  (let [x-scale (->x-scale ratom)]
    (rid3-> node
            {:transform (translate 0 height)}
            (.call (.axisBottom js/d3 x-scale)))
    (lighten-axis node)))


(defn ->y-axis [did-mount?]
  (fn [node ratom]
    (let [y-scale (->y-scale ratom)]

      ;; create axis
      (if did-mount?
        (rid3-> node
                (.call (-> (.axisLeft js/d3 y-scale)
                           (.ticks 3))))
        ;; Add did-update version that transitions on re-render (i.e.,
        ;; when data changes)
        (rid3-> node
                .transition
                (.duration transition-duration)
                (.call (-> (.axisLeft js/d3 y-scale)
                           (.ticks 3)))))

      (lighten-axis node)
      )))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bars

(defn ->bars [did-mount?]
  (fn [node ratom]
    (let [y-scale (->y-scale ratom)
          x-scale (->x-scale ratom)]

      ;; common
      (rid3-> node
              {:x     (fn [d]
                        (let [label (gobj/get d "label")]
                          (x-scale label)))
               :fill  (fn [d i]
                        (color i))
               :width (.bandwidth x-scale)})

      ;; only for did-mount, set have bars grow from x-axis
      (when did-mount?
        (rid3-> node
                {:height 0
                 :y      height}))

      ;; add transition to bars height on page load and when data
      ;; changes
      (rid3-> node
              .transition
              (.duration transition-duration)
              {:height (fn [d]
                         (let [value (gobj/get d "value")]
                           (- height
                              (y-scale value))))
               :y      (fn [d]
                         (let [value (gobj/get d "value")]
                           (y-scale value)))}))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Example

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (reset! viz-ratom {:dataset (->mock-dataset)})
    (fn [app-state]
      [:div
       [:h4 "Intermediate Bar Chart"]
       [util/link-source (name cursor-key)]
       [:button
        {:on-click #(swap! viz-ratom assoc :dataset (->mock-dataset))}
        "Randomize data"]

       [rid3/viz
        {:id    (name cursor-key)
         :ratom viz-ratom
         :svg   {:did-mount
                 (fn [node ratom]
                   (rid3-> node
                           {:width  (+ width
                                       (get margin :left)
                                       (get margin :right))
                            :height (+ height
                                       (get margin :top)
                                       (get margin :bottom))}))}

         :main-container {:did-mount
                          (fn [node ratom]
                            (rid3-> node
                                    {:transform (translate
                                                 (get margin :left)
                                                 (get margin :right))}))}
         :pieces
         [{:kind      :container
           :class     "x-axis"
           :did-mount x-axis-did-mount}

          {:kind       :container
           :class      "y-axis"
           :did-mount  (->y-axis true)
           :did-update (->y-axis false)}

          {:kind            :elem-with-data
           :class           "bars"
           :tag             "rect"
           :prepare-dataset prepare-dataset
           :did-mount       (->bars true)
           :did-update      (->bars false)
           }]
         }]
       ])))
