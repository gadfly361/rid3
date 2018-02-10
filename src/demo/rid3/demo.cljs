(ns rid3.demo
  (:require
   [cljs.spec :as spec]
   [reagent.core :as reagent]
   [re-frisk.core :as rf]

   [rid3.core :as d3]

   [rid3.demo-util :as dutil]
   [rid3.arc-tween :as arc-tween]
   [rid3.barchart :as barchart]
   [rid3.barchart2 :as barchart2]
   [rid3.barchart3 :as barchart3]
   [rid3.pie :as pie]
   ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state
  (reagent/atom
   {
    :app {:page-width nil}

    :arc-tween {}
    :barchart  {:dataset barchart/dataset}
    :barchart2 {:dataset barchart2/dataset}
    :barchart3 {:dataset barchart3/dataset}
    :pie       {:dataset pie/dataset}
    }))

(def app-cursor (reagent/cursor app-state [:app]))

(def arc-tween-cursor (reagent/cursor app-state [:arc-tween]))
(def barchart-cursor (reagent/cursor app-state [:barchart]))
(def barchart2-cursor (reagent/cursor app-state [:barchart2]))
(def barchart3-cursor (reagent/cursor app-state [:barchart3]))
(def pie-cursor (reagent/cursor app-state [:pie]))

(defn viz [ratom]
  [d3/viz
   {:id    "some-id"
    :ratom ratom
    :svg   {:did-mount (fn [node _]
                         (-> node
                             (.attr "width" 200)
                             (.attr "height" 200)
                             (.style "background-color" "grey")))}
    :pieces
    [{:kind      :elem
      :class     "backround"
      :tag       "circle"
      :did-mount (fn [node _]
                   (-> node
                       (.attr "cx" 100)
                       (.attr "cy" 100)
                       (.attr "r" 50)))}

     {:kind      :elem
      :class     "foreground"
      :tag       "text"
      :did-mount (fn [node _]
                   (-> node
                       (.attr "x" 100)
                       (.attr "y" 100)
                       (.attr "text-anchor" "middle")
                       (.attr "alignment-baseline" "middle")
                       (.attr "fill" "green")
                       (.attr "font-size" "24px")
                       (.attr "font-family" "sans-serif")
                       (.text "RID3")))}]
    }])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(def cursors [arc-tween-cursor
              barchart-cursor
              barchart2-cursor
              barchart3-cursor
              pie-cursor])

(defn set-viz-page-widths! [page-width]
  (doseq [c cursors]
    (swap! c assoc :page-width page-width)))

(defn page [ratom]
  (let [page-width (get @app-cursor :page-width 300)
        _ (set-viz-page-widths! page-width)]
    [:div

     [:h2
      {:style {:font-family "sans-serif"
               :font-weight "300"}}
      [:a {:href "https://github.com/gadfly361/rid3"}
       "Rid3"]
      " is a reagent interface to d3. The following are examples of
    going for a rid3."]

     [:br]
     [:br]

     (when page-width
       [:section
        [barchart/example barchart-cursor]
        [barchart2/example barchart2-cursor]
        [barchart3/example barchart3-cursor]
        [pie/example pie-cursor]
        [arc-tween/example arc-tween-cursor]])

     [:div
      {:style {:margin-top "200px"}}]
     ]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Track Window Size

(def mobile-breakpoint 768)

(defn window-size [ratom]
  (let [set-size!
        (fn []
          (let [match-media (.matchMedia js/window (str "(max-width: "
                                                        mobile-breakpoint
                                                        "px)"))
                mobile?     (aget match-media "matches")

                inner-width  (some-> js/window .-innerWidth)
                client-width (some-> js/document .-body .-clientWidth)
                page-width   (or inner-width client-width)]

            (swap! ratom  assoc
                   :page-width page-width
                   :mobile? mobile?)))]
    (set-size!)
    (.addEventListener js/window "resize"
                       set-size!
                       true)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    (rf/enable-frisk!)
    (rf/add-data :app-state app-state)
    (spec/check-asserts true)
    ))

(defn reload []
  (reagent/render [page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload)
  (window-size app-cursor))
