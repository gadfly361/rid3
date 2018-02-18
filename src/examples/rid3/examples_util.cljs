(ns rid3.examples-util
  (:require
   [rid3.version :as version]
   [clojure.string :as string]))


(defn link-source [n]
  [:div {:style {:margin-bottom "16px"}}
   [:a {:href
        (str "https://github.com/gadfly361/rid3/blob/"
             version/version-number
             "/src/examples/rid3/"
             (string/replace n "-" "_")
             ".cljs")}
    "source"]])
