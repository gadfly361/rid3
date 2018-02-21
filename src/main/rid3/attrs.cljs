(ns rid3.attrs)


(defn attrs [node attr-map]
  (doseq [[k v] attr-map]
    (if (= (name k) "style")
      (doseq [[k-style v-style] v]
        (.style node (name k-style) v-style))
      (.attr node (name k) v)))
  node)
