(ns rid3.svg
  (:require [cljsjs.d3]))


(defn- svg-did-mount [opts]
  (let [{:keys [id
                ratom
                svg]} opts
        parent-node   (js/d3.select (str "#" id))
        did-mount     (get svg :did-mount (fn [node ratom] node))]
    (-> parent-node
        (.append "svg")
        (did-mount ratom))))


(defn- svg-did-update [opts]
  (let [{:keys [id
                ratom
                svg]} opts
        did-update    (or (get svg :did-update)
                          (get svg :did-mount) ;; sane fallback
                          (fn [node ratom] node))
        node          (js/d3.select (str "#" id " svg"))]
    (did-update node ratom)))
