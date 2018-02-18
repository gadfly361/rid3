(ns rid3.basics
  (:require
   [cljs.spec :as spec]
   [reagent.core :as reagent]
   [re-frisk.core :as rf]
   [rid3.b01-make-elem :as b01-make-elem]
   [rid3.b02-add-attrs-to-elem :as b02-add-attrs-to-elem]
   [rid3.b03-add-element-on-top :as b03-add-element-on-top]

   [rid3.b04-make-elements-from-data :as b04-make-elements-from-data]
   [rid3.b05-elements-from-arbitrary-data-location :as b05-elements-from-arbitrary-data-location]

   [rid3.b06-make-container-piece :as b06-make-container-piece]

   [rid3.b07-make-raw-elements :as b07-make-raw-elements]
   ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state (reagent/atom {}))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn page [app-state]
  [:div
   [:h1
    {:style {:font-family "sans-serif"
             :font-weight "300"}}
    [:a {:href "https://github.com/gadfly361/rid3"}
     "Rid3"]
    " is a reagent interface to d3."]
   [:p
    "The following goes over the very basics of rid3."]

   [:section
    [:h2 [:strong "Basics"]]
    [:h3 [:code ":elem"] " piece"]
    [b01-make-elem/example app-state]
    [b02-add-attrs-to-elem/example app-state]
    [b03-add-element-on-top/example app-state]

    [:h3 [:code ":elem-with-data"] " piece"]
    [b04-make-elements-from-data/example app-state]
    [b05-elements-from-arbitrary-data-location/example app-state]

    [:h3 [:code ":container"] " piece"]
    [b06-make-container-piece/example app-state]

    [:h3 [:code ":raw"] " piece"]
    [b07-make-raw-elements/example app-state]
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
     margin-bottom:0;
    }

    code {
    background-color:pink;
    padding:4px;
    border-radius:4px;
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
