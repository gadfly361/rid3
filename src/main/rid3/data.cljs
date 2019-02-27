(ns rid3.data
  (:require
   [rid3.util :as util]))


(defn data-join [piece opts prev-classes]
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
        (.select (str "." class))
        (.selectAll tag)
        (.data dataset key-fn))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Normal

(defn data-enter [piece opts prev-classes]
  (let [{:keys [ratom]} opts
        {:keys [tag]}   piece]
    (-> (data-join piece opts prev-classes)
        .enter
        (.append tag))))

(defn did-mount-data-update [piece opts prev-classes]
  (let [{:keys [ratom]}     opts
        {:keys [did-mount]} piece
        on-update           (or did-mount
                                (fn [node ratom] node))
        ref                 (data-join piece opts prev-classes)]
    (on-update ref ratom)))

(defn did-update-data-update [piece opts prev-classes]
  (let [{:keys [ratom]}      opts
        {:keys [did-mount
                did-update]} piece
        on-update            (or did-update
                                 did-mount ;; sane fallback
                                 (fn [node ratom] node))
        ref                  (data-join piece opts prev-classes)]
    (on-update ref ratom)))

(defn data-exit [piece opts prev-classes]
  (let [{:keys [ratom]} opts]
    (-> (data-join piece opts prev-classes)
        .exit
        .remove)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; GUP

(defn gup-data-enter-init [piece opts prev-classes]
  (let [{:keys [ratom]}      opts
        {:keys [tag
                gup]}        piece
        {:keys [enter
                enter-init]} gup
        on-enter             (or enter-init
                                 enter ;; sane alternative
                                 (fn [node ratom] node))]
    (-> (data-join piece opts prev-classes)
        .enter
        (.append tag)
        (on-enter ratom))))

(defn gup-data-enter [piece opts prev-classes]
  (let [{:keys [ratom]} opts
        {:keys [tag
                gup]}   piece
        {enter :enter}  gup
        on-enter        (or enter
                            (fn [node ratom] node))]
    (-> (data-join piece opts prev-classes)
        .enter
        (.append tag)
        (on-enter ratom))))

(defn gup-data-update [piece opts prev-classes]
  (let [{:keys [ratom]}      opts
        {:keys [gup]}        piece
        {gup-update :update} gup
        on-update            (or gup-update
                                 (fn [node ratom] node))
        ref                  (data-join piece opts prev-classes)]
    (on-update ref ratom)))

(defn gup-data-exit [piece opts prev-classes]
  (let [{:keys [ratom]} opts
        {:keys [gup]}   piece
        {:keys [exit]}  gup
        on-exit         (or exit
                            (fn [node ratom] node))]
    (-> (data-join piece opts prev-classes)
        .exit
        (on-exit ratom)
        .remove)))
