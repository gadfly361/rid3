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

  ;; TODO: remove notice in v0.3.0
  (when (get opts :prepare-dataset)
    (js/console.warn (str "[rid3] using :prepare-dataset at the top-level has been deprecated. Please move :prepare-dataset inside of your :elem-with-data pieces.")))

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
