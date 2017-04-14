(ns rid3.core
  (:require
   [clojure.string :as string]
   [cljs.spec :as spec]
   [cljsjs.d3]
   [reagent.core :as reagent]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Svg

(defn- svg-did-mount [opts]
  (let [{:keys [id
                ratom
                svg]} opts
        parent-node   (js/d3.select (str "#" id))
        did-mount     (get svg :did-mount (fn [node] node))]
    (-> parent-node
        (.append "svg")
        (did-mount ratom))))

(defn- svg-did-update [opts]
  (let [{:keys [id
                ratom
                svg]} opts
        did-update    (or (get svg :did-update)
                          (get svg :did-mount) ;; sane fallback
                          (fn [node] node))
        node          (js/d3.select (str "#" id " svg"))]
    (did-update node ratom)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Container

(defn- main-container-did-mount [id main-container ratom]
  (let [did-mount (get main-container :did-mount (fn [node] node))
        node      (js/d3.select (str "#" id " svg"))]
    (-> node
        (.append "g")
        (.attr "class" "main-container")
        (did-mount ratom))))

(defn- main-container-did-update [id main-container ratom]
  (let [did-update (or (get main-container :did-update)
                       (get main-container :did-mount) ;; sane fallback
                       (fn [node] node))
        node       (js/d3.select (str "#" id " svg .main-container"))]
    (did-update node ratom)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Data

(defn- data-join [node
                  ratom
                  prepare-dataset
                  tag]
  (let [dataset (if prepare-dataset
                  (prepare-dataset ratom)
                  (clj->js (get @ratom :dataset)))]
    (-> node
        (.selectAll tag)
        (.data dataset))))

(defn- data-enter [node ratom prepare-dataset tag]
  (-> (data-join node ratom prepare-dataset tag)
      .enter
      (.append tag)))

(defn- data-update [node ratom prepare-dataset tag update-fn]
  (let [ref (data-join node ratom prepare-dataset tag)]
    (update-fn ref ratom)))

(defn- data-exit [node ratom prepare-dataset tag]
  (-> (data-join node ratom prepare-dataset tag)
      .exit
      .remove))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Handle Pieces

(defn- elem-class [id prev-classes]
  (let [prev-classes (remove nil? prev-classes)]
    (str "#" id " svg .main-container"
         (when-not (empty? prev-classes)
           (str " ." (string/join " ." prev-classes))))))


(defn- handle-piece-did-mount
  ([piece opts]
   (handle-piece-did-mount piece opts []))

  ([piece opts prev-classes]
   (let [{:keys [id
                 ratom
                 prepare-dataset]} opts
         {:keys [kind
                 class
                 tag
                 did-mount]}       piece
         did-mount (or did-mount (fn [node] node))]

     (condp = kind
       :container
       (let [node      (js/d3.select (elem-class id prev-classes))]
         (-> node
             (.append "g")
             (.attr "class" class)
             (did-mount ratom)))

       :elem
       (let [node (js/d3.select (elem-class id prev-classes))]
         (-> node
             (.append "g")
             (.attr "class" class)
             (.append tag)
             (did-mount ratom)))

       :elem-with-data
       (let [selector (elem-class id prev-classes)
             node     (js/d3.select selector)]
         (-> node
             (.append "g")
             (.attr "class" class))
         (let [node-inner (js/d3.select (str selector " ." class))]
           (data-enter node-inner ratom prepare-dataset tag)
           (data-update node-inner ratom prepare-dataset tag did-mount)
           (data-exit node-inner ratom prepare-dataset tag)))

       :raw
       (did-mount ratom)

       (js/console.warn (str "[rid3] unknown kind --> " kind)))

     (let [children (get piece :children)]
       (doseq [child children]
         (handle-piece-did-mount child opts (conj prev-classes class)))))))


(defn- handle-piece-did-update
  ([piece opts]
   (handle-piece-did-update piece opts []))

  ([piece opts prev-classes]
   (let [{:keys [id
                 ratom
                 prepare-dataset]} opts
         {:keys [kind
                 class
                 tag
                 did-update
                 did-mount]}       piece
         did-update-raw            did-update
         did-update                (or did-update
                                       did-mount ;; sane-fallback
                                       (fn [node] node))]
     (condp = kind
       :container
       (let [node (js/d3.select (str (elem-class id prev-classes)
                                     " ." class))]
         (did-update node ratom))

       :elem
       (let [node (js/d3.select (str (elem-class id prev-classes)
                                     " ." class " " tag))]
         (did-update node ratom))

       :elem-with-data
       (let [selector   (elem-class id prev-classes)
             node-inner (js/d3.select (str selector " ." class))]
         (data-enter node-inner ratom prepare-dataset tag)
         (data-update node-inner ratom prepare-dataset tag did-update)
         (data-exit node-inner ratom prepare-dataset tag))

       :raw
       (when did-update-raw
         (did-update-raw ratom))

       (js/console.warn (str "[rid3] unknown kind --> " kind)))

     (let [children (get piece :children)]
       (doseq [child children]
         (handle-piece-did-update child opts (conj prev-classes class)))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; React Life Cycle

(defn- viz-render [opts]
  (let [{:keys [id
                ratom]} opts
        _trigger-update @ratom]
    [:div {:id id}]))


(defn- viz-did-mount [opts]
  (let [{:keys [id
                ratom
                pieces
                main-container]} opts]

    (svg-did-mount opts)
    (main-container-did-mount id main-container ratom)

    (doseq [piece pieces]
      (handle-piece-did-mount piece opts))))


(defn- viz-did-update [opts]
  (let [{:keys [id
                ratom
                pieces
                main-container]} opts]

    (svg-did-update opts)
    (main-container-did-update id main-container ratom)

    (doseq [piece pieces]
      (handle-piece-did-update piece opts))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Viz (Public)

(defn viz [opts]
  (reagent/create-class
   {:reagent-render       #(viz-render opts)
    :component-did-mount  #(viz-did-mount opts)
    :component-did-update #(viz-did-update opts)}))
