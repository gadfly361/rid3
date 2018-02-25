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
        (.select (str "." class))
        (.selectAll tag)
        (.data dataset key-fn))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Normal

(defn- data-enter [piece opts prev-classes]
  (let [{:keys [ratom]} opts
        {:keys [tag]}   piece]
    (-> (data-join piece opts prev-classes)
        .enter
        (.append tag))))

(defn- did-mount-data-update [piece opts prev-classes]
  (let [{:keys [ratom]}     opts
        {:keys [did-mount]} piece
        on-update           (or did-mount
                                (fn [node ratom] node))
        ref                 (data-join piece opts prev-classes)]
    (on-update ref ratom)))

(defn- did-update-data-update [piece opts prev-classes]
  (let [{:keys [ratom]}      opts
        {:keys [did-mount
                did-update]} piece
        on-update            (or did-update
                                 did-mount ;; sane fallback
                                 (fn [node ratom] node))
        ref                  (data-join piece opts prev-classes)]
    (on-update ref ratom)))

(defn- data-exit [piece opts prev-classes]
  (let [{:keys [ratom]} opts]
    (-> (data-join piece opts prev-classes)
        .exit
        .remove)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; GUP did-mount

(defn- did-mount-gup-data-enter [piece opts prev-classes]
  (let [{:keys [ratom]}         opts
        {:keys [tag
                did-mount-gup]} piece
        {:keys [enter]}         did-mount-gup
        on-enter                (or enter
                                    (fn [node ratom] node))]
    (-> (data-join piece opts prev-classes)
        .enter
        (.append tag)
        (on-enter ratom))))

(defn- did-mount-gup-data-update [piece opts prev-classes]
  (let [{:keys [ratom]}            opts
        {:keys [did-mount-gup]}    piece
        {did-mount-update :update} did-mount-gup
        on-update                  (or did-mount-update
                                       (fn [node ratom] node))
        ref                        (data-join piece opts prev-classes)]
    (on-update ref ratom)))

(defn- did-mount-gup-data-exit [piece opts prev-classes]
  (let [{:keys [ratom]}         opts
        {:keys [did-mount-gup]} piece
        {:keys [exit]}          did-mount-gup
        on-exit                 (or exit
                                    (fn [node ratom]
                                      node))]
    (-> (data-join piece opts prev-classes)
        .exit
        (on-exit ratom)
        .remove)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; GUP did-update

(defn- did-update-gup-data-enter [piece opts prev-classes]
  (let [{:keys [ratom]}           opts
        {:keys [tag
                did-update-gup
                did-mount-gup]}   piece
        {did-update-enter :enter} did-update-gup
        {did-mount-enter :enter}  did-mount-gup
        on-enter                  (or did-update-enter
                                      did-mount-enter ;; sane fallback
                                      (fn [node ratom] node))]
    (-> (data-join piece opts prev-classes)
        .enter
        (.append tag)
        (on-enter ratom))))

(defn- did-update-gup-data-update [piece opts prev-classes]
  (let [{:keys [ratom]}             opts
        {:keys [did-update-gup
                did-mount-gup]}    piece
        {did-update-update :update} did-update-gup
        {did-mount-update :update}  did-mount-gup
        on-update                   (or did-update-update
                                        did-mount-update
                                        (fn [node ratom] node))
        ref                         (data-join piece opts prev-classes)]
    (on-update ref ratom)))

(defn- did-update-gup-data-exit [piece opts prev-classes]
  (let [{:keys [ratom]}         opts
        {:keys [did-update-gup
                did-mount-gup]} piece
        {did-update-exit :exit} did-update-gup
        {did-mount-exit :exit}  did-mount-gup
        on-exit                 (or did-update-exit
                                    did-mount-exit ;; sane fallback
                                    (fn [node ratom] node))]
    (-> (data-join piece opts prev-classes)
        .exit
        (on-exit ratom)
        .remove)))
