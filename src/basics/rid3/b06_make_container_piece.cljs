(ns rid3.b06-make-container-piece
  (:require
   [reagent.core :as reagent]
   [rid3.core :as rid3]
   [rid3.basics-util :as util]
   ))

(def cursor-key :b06-make-container-piece)

(def height 100)
(def width 100)
(def radius 4)

(defn translate [left top]
  (str "translate("
       (or left 0)
       ","
       (or top 0)
       ")"))


(defn example [app-state]
  (let [viz-ratom (reagent/cursor app-state [cursor-key])]
    (fn [app-state]
      [:div
       [:h4 "6) Make a "
        [:em ":container"]
        " piece. A :container is particularly useful when you want to
        transform multiple elements together."]
       [util/link-source (name cursor-key)]

       [rid3/viz
        {:id    "b06"
         :ratom viz-ratom
         :svg   {:did-mount (fn [node ratom]
                              (-> node
                                  (.attr "height" height)
                                  (.attr "width" width)))}

         :pieces
         [;; translate a circle and a text together to the top right
          ;; of the svg
          {:kind      :container
           :class     "top-right-container"
           :did-mount (fn [node ratom]
                        (-> node
                            (.attr "transform" (translate (- width
                                                             (* 2 radius))
                                                          10))))
           :children  [{:kind      :elem
                        :tag       "text"
                        :class     "my-text"
                        :did-mount (fn [node ratom]
                                     (-> node
                                         (.attr "font-size" 8)
                                         (.attr "text-anchor" "end")
                                         (.text "top right circle")
                                         ))}
                       {:kind      :elem
                        :tag       "circle"
                        :class     "my-circle"
                        :did-mount (fn [node ratom]
                                     (-> node
                                         (.attr "cy" 8)
                                         (.attr "r" radius)
                                         (.attr "fill" "green")
                                         ))}]}

          ;; translate a circle and a text together to the bottom
          ;; right of the svg
          {:kind      :container
           :class     "bottom-right-container"
           :did-mount (fn [node ratom]
                        (-> node
                            (.attr "transform" (translate (- width
                                                             (* 2 radius))
                                                          (- height
                                                             10
                                                             (* 2 radius))))))
           :children  [{:kind      :elem
                        :tag       "text"
                        :class     "my-text"
                        :did-mount (fn [node ratom]
                                     (-> node
                                         (.attr "font-size" 8)
                                         (.attr "text-anchor" "end")
                                         (.text "bottom right circle")
                                         ))}
                       {:kind      :elem
                        :tag       "circle"
                        :class     "my-circle"
                        :did-mount (fn [node ratom]
                                     (-> node
                                         (.attr "cy" -12)
                                         (.attr "r" radius)
                                         (.attr "fill" "green")
                                         ))}]}
          ]}]])))
