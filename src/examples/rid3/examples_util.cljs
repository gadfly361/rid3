(ns rid3.examples-util
  (:require
   [rid3.version :as version]))


(defn link-source [n]
  [:div {:style {:margin-bottom "8px"}}
   [:a {:href
        (str "https://github.com/gadfly361/rid3/tree/"
             "v" version/version-number
             "/src/examples/rid3/"
             n)}
    "source"]])
