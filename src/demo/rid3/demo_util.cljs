(ns rid3.demo-util)


(defn title [label file-name]
  [:div
   [:h3
    {:style {:font-family "sans-serif"
             :font-weight "300"
             :margin      0}}
    label]
   [:h4
    {:style {:font-family "sans-serif"
             :margin-top  0}}
    [:a {:href (str "https://github.com/gadfly361/rid3/blob/master/src/demo/rid3/"
                    file-name
                    ".cljs")}
     "source"]]])


(defn btn-randomize-data [ratom randomize-fn]
  [:div
   [:button
    {:on-click #(randomize-fn ratom)}
    "Randomize data"]])
