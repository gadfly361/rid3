(ns rid3.core)


(defmacro rid3-> [node & forms]
  (let [forms-sanitized (mapv
                         (fn [form]
                           (if (map? form)
                             `(rid3.attrs/attrs ~form)
                             form))
                         forms)]
    `(-> ~node
         ~@forms-sanitized)))
