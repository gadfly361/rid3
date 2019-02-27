(ns rid3.elem-with-data
  (:require
   [cljsjs.d3]
   [rid3.util :as util]
   [rid3.data :as data]))


(defn piece-did-mount [piece opts prev-classes]
  (let [id       (get opts :id)
        selector (util/node-selector id prev-classes)
        node     (js/d3.select selector)
        class    (get piece :class)
        gup?     (get piece :gup)]
    (-> node
        (.append "g")
        (.attr "class" class))
    (if gup?
      (do
        ;; enter needs to be after update
        (data/gup-data-exit piece opts prev-classes)
        (data/gup-data-update piece opts prev-classes)
        (data/gup-data-enter-init piece opts prev-classes))
      (do
        ;; update needs to be after enter
        (data/data-enter piece opts prev-classes)
        (data/did-mount-data-update piece opts prev-classes)
        (data/data-exit piece opts prev-classes)))))


(defn piece-did-update [piece opts prev-classes]
  (let [gup? (get piece :gup)]
    (if gup?
      (do
        ;; enter needs to be after update
        (data/gup-data-exit piece opts prev-classes)
        (data/gup-data-update piece opts prev-classes)
        (data/gup-data-enter piece opts prev-classes))
      (do
        ;; update needs to be after enter
        (data/data-enter piece opts prev-classes)
        (data/did-update-data-update piece opts prev-classes)
        (data/data-exit piece opts prev-classes)))))
