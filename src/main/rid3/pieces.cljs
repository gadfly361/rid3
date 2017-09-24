(ns rid3.pieces
  (:require
   [clojure.string :as string]
   [cljsjs.d3]
   [rid3.data :as data]
   [rid3.util :as util]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; did-mount

(defn- container-did-mount [piece opts prev-classes]
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


(defn- elem-did-mount [piece opts prev-classes]
  (let [{:keys [id
                ratom]}             opts
        {:keys [tag
                class
                did-mount]
         :or   {did-mount (fn [node ratom]
                            node)}} piece
        node                        (js/d3.select (util/node-selector id prev-classes))]
    (-> node
        (.append tag)
        (.attr "class" class)
        (did-mount ratom))))


(defn- elem-with-data-did-mount [piece opts prev-classes]
  (data/data-enter piece opts prev-classes)
  (data/data-update-on-did-mount piece opts prev-classes)
  (data/data-exit piece opts prev-classes))


(defn- raw-did-mount [piece opts]
  (let [{:keys [ratom]}             opts
        {:keys [did-mount]
         :or   {did-mount (fn [node ratom]
                            node)}} piece]
    (did-mount ratom)))


(defn- handle-piece-did-mount
  ([piece opts]
   (handle-piece-did-mount piece opts []))

  ([piece opts prev-classes]
   (let [{:keys [kind
                 class
                 did-mount]} piece]

     (when (and (not class)
                (or (= kind :container)
                    (= kind :elem)
                    (= kind :elem-with-data)))
       (js/console.warn (str "[rid3] a " kind " needs to have a class")))

     (cond
       (= kind :container)
       (container-did-mount piece opts prev-classes)

       (= kind :elem)
       (elem-did-mount piece opts prev-classes)

       (= kind :elem-with-data)
       (elem-with-data-did-mount piece opts prev-classes)

       (= kind :raw)
       (raw-did-mount piece opts)

       ;; warn on mistakes

       (keyword? piece)
       (js/console.warn (str "[rid3] a piece needs to be a hash-map, you provided a keyword --> " piece))

       (vector? piece)
       (js/console.warn (str "[rid3] a piece needs to be a hash-map, you provided a vector"))

       (not kind)
       (js/console.warn (str "[rid3] every piece needs to have a kind"))

       :else
       (js/console.warn (str "[rid3] unknown kind --> " kind)))


     (let [children (get piece :children)]
       (cond
         (and children (or (= kind :container)
                           (= kind :elem)))
         (doseq [child children]
           (handle-piece-did-mount child opts (conj prev-classes class)))

         children
         (js/console.warn (str "[rid3] " kind " piece cannot have children"))

         :else nil)))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; did-update

(defn- container-did-update [piece opts prev-classes]
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


(defn- elem-did-update [piece opts prev-classes]
  (let [{:keys [id
                ratom]}      opts
        {:keys [tag
                class
                did-mount
                did-update]} piece
        did-update           (or did-update
                                 did-mount ;; sane-fallback
                                 (fn [node ratom]
                                   node))
        node                 (js/d3.select (str (util/node-selector id prev-classes)
                                                " " tag "." class))]
    (did-update node ratom)))


(defn- elem-with-data-did-update [piece opts prev-classes]
  (data/data-enter piece opts prev-classes)
  (data/data-update-on-did-update piece opts prev-classes)
  (data/data-exit piece opts prev-classes))


(defn- raw-did-update [piece opts]
  (let [{:keys [ratom]}              opts
        {:keys [did-update]
         :or   {did-update (fn [node ratom]
                             node)}} piece]
    (did-update ratom)))


(defn- handle-piece-did-update
  ([piece opts]
   (handle-piece-did-update piece opts []))

  ([piece opts prev-classes]
   (let [{:keys [id
                 ratom]} opts
         {:keys [kind
                 class]} piece]

     (when (and (not class)
                (or (= kind :container)
                    (= kind :elem)
                    (= kind :elem-with-data)))
       (js/console.warn (str "[rid3] " kind " needs to have a class")))

     (cond
       (= kind :container)
       (container-did-update piece opts prev-classes)

       (= kind :elem)
       (elem-did-update piece opts prev-classes)

       (= kind :elem-with-data)
       (elem-with-data-did-update piece opts prev-classes)

       (= kind :raw)
       (raw-did-update piece opts)

       ;; warn on mistakes

       (keyword? piece)
       (js/console.warn (str "[rid3] a piece needs to be a hash-map, you provided a keyword --> " piece))

       (vector? piece)
       (js/console.warn (str "[rid3] a piece needs to be a hash-map, you provided a vector"))

       (not kind)
       (js/console.warn (str "[rid3] every piece needs to have a kind"))

       :else
       (js/console.warn (str "[rid3] unknown kind --> " kind)))


     (let [children (get piece :children)]
       (cond
         (and children (or (= kind :container)
                           (= kind :elem)))
         (doseq [child children]
           (handle-piece-did-update child opts (conj prev-classes class)))

         children
         (js/console.warn (str "[rid3] " kind " cannot have children"))

         :else nil)))))
