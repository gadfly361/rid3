(ns rid3.bar-simple
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3]
   [rid3.examples-util :as util]
   [goog.object :as gobj]
   ))

(def cursor-key :bar-simple)

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
;; Example

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (reset! viz-ratom {:dataset (->mock-dataset)})
    (fn [app-state]
      [:div
       [:h4 "Simple Bar Chart"]
       [util/link-source (name cursor-key)]
       [:button
        {:on-click #(swap! viz-ratom assoc :dataset (->mock-dataset))}
        "Randomize data"]

       [rid3/viz
        {:id    "bar-simple"
         :ratom viz-ratom
         :svg {:did-mount
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
         [{:kind  :container
           :class "x-axis"
           :did-mount
           (fn [node ratom]
             (let [x-scale (->x-scale ratom)]
               (-> node
                   (rid3/attrs
                    {:transform (translate 0 height)})
                   (.call (.axisBottom js/d3 x-scale)))))}

          {:kind  :container
           :class "y-axis"
           :did-mount
           (fn [node ratom]
             (let [y-scale (->y-scale ratom)]
               (-> node
                   (.call (-> (.axisLeft js/d3 y-scale)
                              (.ticks 3))))))}

          {:kind            :elem-with-data
           :class           "bars"
           :tag             "rect"
           :prepare-dataset prepare-dataset
           :did-mount
           (fn [node ratom]
             (let [y-scale (->y-scale ratom)
                   x-scale (->x-scale ratom)]
               (-> node
                   (rid3/attrs
                    {:x      (fn [d]
                               (let [label (gobj/get d "label")]
                                 (x-scale label)))
                     :width  (.bandwidth x-scale)
                     :fill   (fn [d i]
                               (color i))
                     :height (fn [d]
                               (let [value (gobj/get d "value")]
                                 (- height
                                    (y-scale value))))
                     :y      (fn [d]
                               (let [value (gobj/get d "value")]
                                 (y-scale value)))}))))
           }]
         }]
       ])))
