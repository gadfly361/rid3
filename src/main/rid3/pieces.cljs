(ns rid3.pieces
  (:require
   [clojure.string :as string]
   [cljsjs.d3]
   [rid3.data :as data]
   [rid3.util :as util]))


(defn- node-selector [id prev-classes]
  (let [prev-classes (remove nil? prev-classes)]
    (str "#" id " svg"
         " ." util/main-container-class
         (when-not (empty? prev-classes)
           (str " ." (string/join " ." prev-classes))))))


(defn- handle-piece-did-mount
  ([piece opts]
   (handle-piece-did-mount piece opts []))

  ([piece opts prev-classes]
   (let [{:keys [id
                 ratom]}     opts
         {:keys [kind
                 class
                 tag
                 did-mount]} piece
         prepare-dataset     (get piece :prepare-dataset)
         did-mount           (or did-mount (fn [node] node))]

     (when (and (not class)
                (or (= kind :container)
                    (= kind :elem)
                    (= kind :elem-with-data)))
       (js/console.warn (str "[rid3] a " kind " needs to have a class")))

     (cond
       (= kind :container)
       (let [node (js/d3.select (node-selector id prev-classes))]
         (-> node
             (.append "g")
             (.attr "class" class)
             (did-mount ratom)))

       (= kind :elem)
       (let [node (js/d3.select (node-selector id prev-classes))]
         (-> node
             (.append tag)
             (.attr "class" class)
             (did-mount ratom)))

       (= kind :elem-with-data)
       (let [selector (node-selector id prev-classes)
             node     (js/d3.select selector)]
         (data/data-enter node ratom prepare-dataset tag class)
         (data/data-update node ratom prepare-dataset tag class did-mount)
         (data/data-exit node ratom prepare-dataset tag class))

       (= kind :raw)
       (did-mount ratom)

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


(defn- handle-piece-did-update
  ([piece opts]
   (handle-piece-did-update piece opts []))

  ([piece opts prev-classes]
   (let [{:keys [id
                 ratom]}     opts
         {:keys [kind
                 class
                 tag
                 did-update
                 did-mount]} piece
         prepare-dataset     (get piece :prepare-dataset)
         did-update-raw      did-update
         did-update          (or did-update
                                 did-mount ;; sane-fallback
                                 (fn [node] node))]

     (when (and (not class)
                (or (= kind :container)
                    (= kind :elem)
                    (= kind :elem-with-data)))
       (js/console.warn (str "[rid3] a " kind " needs to have a class")))

     (cond
       (= kind :container)
       (let [node (js/d3.select (str (node-selector id prev-classes)
                                     " ." class))]
         (did-update node ratom))

       (= kind :elem)
       (let [node (js/d3.select (str (node-selector id prev-classes)
                                     " " tag "." class))]
         (did-update node ratom))

       (= kind :elem-with-data)
       (let [selector (node-selector id prev-classes)
             node     (js/d3.select selector)]
         (data/data-enter node ratom prepare-dataset tag class)
         (data/data-update node ratom prepare-dataset tag class did-update)
         (data/data-exit node ratom prepare-dataset tag class))

       (= kind :raw)
       (when did-update-raw
         (did-update-raw ratom))

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
