(ns rid3.pieces
  (:require
   [cljsjs.d3]
   [rid3.util :as util]
   [rid3.container :as container]
   [rid3.elem :as elem]
   [rid3.elem-with-data :as elem-with-data]
   [rid3.raw :as raw]
   ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Warnings

(defn warn-if-piece-needs-class [kind class]
  (when (and (not class)
             (or (= kind :container)
                 (= kind :elem)
                 (= kind :elem-with-data)))
    (js/console.warn (str "[rid3] a " kind " needs to have a class"))))


(defn warn-if-piece-is-keyword [piece]
  (js/console.warn (str "[rid3] a piece needs to be a hash-map, you provided a keyword --> " piece)))


(defn warn-if-piece-is-vector [piece]
  (js/console.warn (str "[rid3] a piece needs to be a hash-map, you provided a vector --> " piece)))


(defn warn-if-piece-is-missing-kind []
  (js/console.warn (str "[rid3] every piece needs to have a kind")))


(defn warn-if-piece-has-unknown-kind [kind]
  (js/console.warn (str "[rid3] unknown kind --> " kind)))


(defn warn-if-piece-shouldnt-have-children [kind]
  (js/console.warn (str "[rid3] " kind " piece cannot have children")))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Handlers

(defn- handle-piece-did-mount
  ([piece opts]
   (handle-piece-did-mount piece opts []))

  ([piece opts prev-classes]
   (let [{:keys [kind
                 class
                 did-mount]} piece]

     (warn-if-piece-needs-class kind class)

     (cond
       (= kind :container)
       (container/piece-did-mount piece opts prev-classes)

       (= kind :elem)
       (elem/piece-did-mount piece opts prev-classes)

       (= kind :elem-with-data)
       (elem-with-data/piece-did-mount piece opts prev-classes)

       (= kind :raw)
       (raw/piece-did-mount piece opts)

       ;; warn on mistakes

       (keyword? piece)
       (warn-if-piece-is-keyword piece)

       (vector? piece)
       (warn-if-piece-is-vector piece)

       (not kind)
       (warn-if-piece-is-missing-kind)

       :else
       (warn-if-piece-has-unknown-kind kind))


     (let [children (get piece :children)]
       (cond
         (and children
              (= kind :container))
         (doseq [child children]
           (handle-piece-did-mount child opts (conj prev-classes class)))

         children
         (warn-if-piece-shouldnt-have-children kind)

         :else nil)))))


(defn- handle-piece-did-update
  ([piece opts]
   (handle-piece-did-update piece opts []))

  ([piece opts prev-classes]
   (let [{:keys [id
                 ratom]} opts
         {:keys [kind
                 class]} piece]

     (warn-if-piece-needs-class kind class)

     (cond
       (= kind :container)
       (container/piece-did-update piece opts prev-classes)

       (= kind :elem)
       (elem/piece-did-update piece opts prev-classes)

       (= kind :elem-with-data)
       (elem-with-data/piece-did-update piece opts prev-classes)

       (= kind :raw)
       (raw/piece-did-update piece opts)

       ;; warn on mistakes

       (keyword? piece)
       (warn-if-piece-is-keyword piece)

       (vector? piece)
       (warn-if-piece-is-vector piece)

       (not kind)
       (warn-if-piece-is-missing-kind)

       :else
       (warn-if-piece-has-unknown-kind kind))


     (let [children (get piece :children)]
       (cond
         (and children
              (= kind :container))
         (doseq [child children]
           (handle-piece-did-update child opts (conj prev-classes class)))

         children
         (warn-if-piece-shouldnt-have-children kind)

         :else nil)))))
