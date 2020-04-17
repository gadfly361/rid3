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

(defmacro rid3on-> [& forms]
  `(this-is this#
            (rid3-> (.select d3 this#)
                    ~@forms)))

(defmacro rid3on->2 [node attr & forms]
  `(.on ~node ~attr
        (fn [d i]
          (this-as this
                   (rid3-> (.select d3 this)
                           ~@forms)))))