(ns rid3.data
  (:require
   [rid3.util :as util]))


(defn- data-join [piece opts prev-classes]
  (let [{:keys [id
                ratom]}  opts
        {:keys [tag
                class
                prepare-dataset
                key-fn]} piece
        dataset          (if prepare-dataset
                           (prepare-dataset ratom)
                           (clj->js (get @ratom :dataset)))
        selector         (util/node-selector id prev-classes)
        node             (js/d3.select selector)]
    (-> node
        (.selectAll (str tag "." class))
        (.data dataset key-fn))))



(defn- data-enter [piece opts prev-classes]
  (let [{:keys [tag
                class]} piece]
    (-> (data-join piece opts prev-classes)
        .enter
        (.append tag)
        (.attr "class" class))))


(defn- data-update-on-did-mount [piece opts prev-classes]
  (let [{:keys [ratom]}             opts
        {:keys [did-mount]
         :or   {did-mount (fn [node ratom]
                            node)}} piece
        ref                         (data-join piece opts prev-classes)]
    (did-mount ref ratom)))


(defn- data-update-on-did-update [piece opts prev-classes]
  (let [{:keys [ratom]}      opts
        {:keys [did-mount
                did-update]} piece
        did-update           (or did-update
                                 did-mount ;; sane-fallback
                                 (fn [node ratom]
                                   node))
        ref                  (data-join piece opts prev-classes)]
    (did-update ref ratom)))


(defn- data-exit [piece opts prev-classes]
  (-> (data-join piece opts prev-classes)
      .exit
      .remove))
