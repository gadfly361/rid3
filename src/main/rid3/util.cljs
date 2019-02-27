(ns rid3.util
  (:require
   [clojure.string :as string]))


(def main-container-class "rid3-main-container")


(defn node-selector [id prev-classes]
  (let [prev-classes (remove nil? prev-classes)]
    (str "#" id " svg"
         " ." main-container-class
         (when-not (empty? prev-classes)
           (str " ." (string/join " ." prev-classes))))))
