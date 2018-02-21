(ns rid3.pie-simple
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3]
   [rid3.examples-util :as util]
   [goog.object :as gobj]
   ))

(def cursor-key :pie-simple)

(def height 152)
(def width 280)
(def radius 70)

(def color
  (-> js/d3
      (.scaleOrdinal #js ["#3366CC"
                          "#DC3912"
                          "#FF9900"])))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; dataset

(defn ->mock-dataset []
  (mapv #(hash-map :label %
                   :value (rand-int 200))
        ["A" "B" "C"]))

(def pie
  (-> js/d3
      .pie
      (.value (fn [d]
                (gobj/get d "value")))
      (.sort nil)))

(defn prepare-dataset [ratom]
  (-> @ratom
      (get :dataset)
      clj->js
      pie))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Wedges

(defn ->wedge [ratom]
  (-> js/d3
      .arc
      (.outerRadius (- radius 10))
      (.innerRadius 0)))

(defn wedges-did-mount [node ratom]
  (let [wedge (->wedge ratom)]
    (-> node
        (rid3/attrs
         {:d     wedge
          :fill  (fn [d i]
                   (color i))
          :style {:stroke "#FFF"}}))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Labels

(defn ->label [ratom]
  (-> js/d3
      .arc
      (.outerRadius radius)
      (.innerRadius radius)))

(defn labels-did-mount [node ratom]
  (let [label (->label ratom)
        total (some->> @ratom
                       :dataset
                       (mapv :value)
                       (apply +))]
    (-> node
        (rid3/attrs
         {:transform (fn [d]
                       (str "translate(" (.centroid label d) ")"))
          :dy        "2px"
          :style     {:font        "10px sans-serif"
                      :text-anchor "middle"}})
        (.text (fn [d]
                 (let [value (aget d "data" "value")]
                   (str (some-> value
                                (/ total)
                                (* 100)
                                js/Math.round)
                        "%")))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Example

(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (reset! viz-ratom {:dataset (->mock-dataset)})
    (fn [app-state]
      [:div
       [:h4 "Simple Pie Chart"]
       [util/link-source (name cursor-key)]
       [:button
        {:on-click #(swap! viz-ratom assoc :dataset (->mock-dataset))}
        "Randomize data"]

       [rid3/viz
        {:id    "e01-pie-simple"
         :ratom viz-ratom
         :svg   {:did-mount
                 (fn [node ratom]
                   (-> node
                       (rid3/attrs
                        {:height height
                         :width  width})))}

         :main-container {:did-mount
                          (fn [node ratom]
                            (-> node
                                (rid3/attrs
                                 {:transform (str "translate("
                                                  (/ width 2)
                                                  ","
                                                  (/ height 2)
                                                  ")")})))}

         :pieces
         [{:kind            :elem-with-data
           :class           "wedges"
           :tag             "path"
           :prepare-dataset prepare-dataset
           :did-mount       wedges-did-mount}

          {:kind            :elem-with-data
           :class           "labels"
           :tag             "text"
           :prepare-dataset prepare-dataset
           :did-mount       labels-did-mount}

          ]
         }]
       ])))
