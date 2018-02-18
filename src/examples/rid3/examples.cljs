(ns rid3.examples
  (:require
   [cljs.spec :as spec]
   [reagent.core :as reagent]
   [re-frisk.core :as rf]
   [rid3.pie-simple :as pie-simple]
   [rid3.bar-simple :as bar-simple]
   [rid3.scatter-simple :as scatter-simple]
   ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state (reagent/atom {}))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn page [app-state]
  [:div
   [:h3
    "The following goes over a few "
    [:a {:href "https://github.com/gadfly361/rid3"}
     "Rid3"]
    " examples."]

   [:section
    [:h3 "Bar Chart"]
    [bar-simple/example app-state]
    ]

   [:section
    [:h3 "Scatter plot"]
    [scatter-simple/example app-state]
    ]

   [:section
    [:h3 "Pie Chart"]
    [pie-simple/example app-state]
    ]


   [:style
    "
    * {
    font-family:sans-serif
    }
    h3 {
     margin-bottom:0;
    }
    h4 {
     margin-top:8px;
     margin-bottom:0;
    font-weight:300;
    }

    code {
    background-color:pink;
    padding:4px;
    border-radius:4px;
    }

    button {
    margin:0;
    }
    "]


   ])



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
