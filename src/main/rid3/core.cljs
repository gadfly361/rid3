(ns rid3.core
  (:require
   ;; note: need to pull in this ns for rid3-> macro
   [rid3.attrs]
   [rid3.viz :as viz]
   ))


(def viz viz/component)
