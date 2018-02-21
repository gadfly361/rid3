(ns rid3.integration
  (:require
   [cljs.spec :as spec]
   [reagent.core :as reagent]
   [re-frisk.core :as rf]
   [rid3.core :as rid3]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state (reagent/atom {}))


(def height 100)
(def width 100)

(def margin {:top    16
             :right  16
             :bottom 16
             :left   16})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn page [app-state]
  [:div
   [rid3/viz
    {:id             "test1"
     :ratom          app-state
     :svg            {:did-mount (fn [node _]
                                   (-> node
                                       (rid3/attrs
                                        {:height (+ height
                                                    (:top margin)
                                                    (:bottom margin))
                                         :width  (+ width
                                                    (:left margin)
                                                    (:right margin))})))}
     :main-container {:did-mount (fn [node _]
                                   (-> node
                                       (rid3/attrs
                                        {:transform (str "translate("
                                                         (:left margin)
                                                         ","
                                                         (:top margin)
                                                         ")")})))}
     :pieces         [{:kind      :elem
                       :tag       "rect"
                       :class     "my-elem"
                       :did-mount (fn [node _]
                                    (-> node
                                        (rid3/attrs
                                         {:width  width
                                          :height height
                                          :fill   "grey"})))}

                      {:kind            :elem-with-data
                       :tag             "rect"
                       :class           "my-elem-with-data"
                       :prepare-dataset (fn [ratom]
                                          (clj->js ["A" "B" "C"]))
                       :did-mount       (fn [node _]
                                          (let [x-scale (-> js/d3
                                                            .scaleBand
                                                            (.rangeRound #js [0 width])
                                                            (.domain (clj->js ["A" "B" "C"])))]
                                            (-> node
                                                (rid3/attrs
                                                 {:x      (fn [d]
                                                            (+ (x-scale d)
                                                               (/ (.bandwidth x-scale) 4)))
                                                  :width  (/ (.bandwidth x-scale)
                                                             2)
                                                  :y      0
                                                  :height (fn [d i]
                                                            (/ height (inc i)))
                                                  :fill   "green"}))))}

                      {:kind      :elem
                       :tag       "text"
                       :class     "my-text"
                       :did-mount (fn [node _]
                                    (-> node
                                        (rid3/attrs
                                         {:x           (/ width 2)
                                          :y           (/ height 2)
                                          :font-size   20
                                          :text-anchor "middle"
                                          :style       {:font-weight 500
                                                        :font-family "sans-serif"}})
                                        (.text "foo")))}
                      ]
     }]])



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    (rf/enable-frisk!)
    (rf/add-data :app-state app-state)
    (spec/check-asserts true)))

(defn reload []
  (reagent/render [page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))
