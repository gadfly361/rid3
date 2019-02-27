(ns rid3.container
  (:require
   [cljsjs.d3]
   [rid3.util :as util]))


(defn piece-did-mount [piece opts prev-classes]
  (let [{:keys [id
                ratom]}             opts
        {:keys [class
                did-mount]
         :or   {did-mount (fn [node ratom]
                            node)}} piece
        node                        (js/d3.select (util/node-selector id prev-classes))]
    (-> node
        (.append "g")
        (.attr "class" class)
        (did-mount ratom))))


(defn piece-did-update [piece opts prev-classes]
  (let [{:keys [id
                ratom]}      opts
        {:keys [class
                did-mount
                did-update]} piece
        did-update           (or did-update
                                 did-mount ;; sane-fallback
                                 (fn [node ratom]
                                   node))
        node                 (js/d3.select (str (util/node-selector id prev-classes)
                                                " ." class))]
    (did-update node ratom)))
