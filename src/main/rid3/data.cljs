(ns rid3.data)


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
