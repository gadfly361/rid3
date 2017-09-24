(ns rid3.main-container
  (:require
   [cljsjs.d3]
   [rid3.util :as util]))


(defn- main-container-did-mount [opts]
  (let [{:keys [id
                ratom
                pieces
                main-container]} opts
        did-mount                (get main-container :did-mount
                                      (fn [node ratom]
                                        node))
        node                     (js/d3.select (str "#" id " svg"))]
    (-> node
        (.append "g")
        (.attr "class" util/main-container-class)
        (did-mount ratom))))


(defn- main-container-did-update [opts]
  (let [{:keys [id
                ratom
                pieces
                main-container]} opts
        did-update               (or (get main-container :did-update)
                                     (get main-container :did-mount) ;; sane fallback
                                     (fn [node ratom]
                                       node))
        node                     (js/d3.select (str "#" id " svg"
                                                    " ." util/main-container-class))]
    (did-update node ratom)))
