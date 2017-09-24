(ns rid3.data)


(defn- data-join [node
                  ratom
                  prepare-dataset
                  tag
                  class]
  (let [dataset (if prepare-dataset
                  (prepare-dataset ratom)
                  (clj->js (get @ratom :dataset)))]
    (-> node
        (.selectAll (str tag "." class))
        (.data dataset))))



(defn- data-enter [node ratom prepare-dataset tag class]
  (-> (data-join node ratom prepare-dataset tag class)
      .enter
      (.append tag)
      (.attr "class" class)))


(defn- data-update [node ratom prepare-dataset tag class update-fn]
  (let [ref (data-join node ratom prepare-dataset tag class)]
    (update-fn ref ratom)))


(defn- data-exit [node ratom prepare-dataset tag class]
  (-> (data-join node ratom prepare-dataset tag class)
      .exit
      .remove))
