(ns rid3.viz
  (:require
   [reagent.core :as reagent]
   [rid3.main-container :as main-container]
   [rid3.pieces :as pieces]
   [rid3.svg :as svg]))


(defn- component-render [opts]
  (let [{:keys [id
                ratom]} opts
        _trigger-update @ratom]
    [:div {:id id}]))


(defn- component-did-mount [opts]
  (svg/svg-did-mount opts)
  (main-container/main-container-did-mount opts)

  (let [pieces (get opts :pieces [])]
    (doseq [piece pieces]
      (pieces/handle-piece-did-mount piece opts))))


(defn- component-did-update [opts]
  (svg/svg-did-update opts)
  (main-container/main-container-did-update opts)

  (let [pieces (get opts :pieces [])]
    (doseq [piece pieces]
      (pieces/handle-piece-did-update piece opts))))


(defn- component [opts]
  (reagent/create-class
   {:reagent-render       #(component-render opts)
    :component-did-mount  #(component-did-mount opts)
    :component-did-update #(component-did-update opts)}))
