(ns rid3.raw
  (:require
   [cljsjs.d3]
   [rid3.util :as util]))


(defn- piece-did-mount [piece opts]
  (let [{:keys [ratom]}             opts
        {:keys [did-mount]
         :or   {did-mount (fn [node ratom]
                            node)}} piece]
    (did-mount ratom)))


(defn- piece-did-update [piece opts]
  (let [{:keys [ratom]}              opts
        {:keys [did-update]
         :or   {did-update (fn [node ratom]
                             node)}} piece]
    (did-update ratom)))
