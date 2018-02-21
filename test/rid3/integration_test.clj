(ns rid3.integration-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer :all]
   [etaoin.api :refer :all]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Setup

(def ^:dynamic
  *driver*)

(defn fixture-driver
  "Executes a test running a driver. Bounds a driver
   with the global *driver* variable."
  [f]
  (with-chrome {:headless true} driver
    (binding [*driver* driver]
      (f))))


(use-fixtures
  :each ;; start and stop driver for each test
  fixture-driver)



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(def url "http://localhost:3449/integration.html")

(def screenshot-success-path "screenshots/success")
(def screenshot-failure-path "screenshots/failure")

(def desktop-env
  {:label  "desktop"
   :width  1920
   :height 1080})

(def mobile-env
  {:label  "mobile"
           :width  337
           :height 667})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Util

(defn ->screenshot [driver env name]
  (screenshot driver
              (str screenshot-success-path
                   "/"
                   (get env :label)
                   "_"
                   name
                   ".png")))


(defn px [int]
  (when int
    (str int "px")))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test

(defn ->test1 [env]
  (testing "basic skeleton of a rid3 component"
    (with-postmortem *driver* {:dir screenshot-failure-path}
      (doto *driver*
        (set-window-size (get env :width)
                         (get env :height))
        (go url)
        (wait-visible {:id "test1"})
        (->screenshot env "test1"))

      ;; NOTE:
      ;; a rid3 component has the following structure:

      ;; div#id
      ;; svg
      ;; g.rid3-main-container
      ;; [<pieces>]


      ;; div#id
      (testing "should have div with id"
        (is (exists? *driver* {:css "div#test1"})))

      ;; svg
      (testing "should have svg as child of div with id"
        (is (exists? *driver* {:css "#test1 svg"})))

      (testing "svg should have set dimensions"
        (is (= {:width  132
                :height 132} (get-element-size *driver* {:css "#test1 svg"}))))

      ;; g.rid3-main-container
      (testing "there should be a g tag with class rid3-main-container"
        (is (exists? *driver* {:css "#test1 svg g.rid3-main-container"})))

      (testing "a rect piece should be inside the main-container"
        (is (exists? *driver* {:css (string/join " "
                                                 ["#test1"
                                                  "svg"
                                                  "g.rid3-main-container"
                                                  "g.my-elem"
                                                  "rect"])})))

      ;; [<pieces>]

      ;; :elem
      (testing "a rect (:elem piece) should have set dimensions"
        (is (= {:width  100
                :height 100} (get-element-size *driver*
                                               {:css (string/join " "
                                                                  ["#test1"
                                                                   "svg"
                                                                   "g.rid3-main-container"
                                                                   "g.my-elem"
                                                                   "rect"])}))))

      ;; elem-with-data
      (testing ":elem-with-data piece should create multiple rects inside its parent g"
        (let [rects (query-all *driver*
                               {:css (string/join " "
                                                  ["#test1"
                                                   "svg"
                                                   "g.rid3-main-container"
                                                   "g.my-elem-with-data"
                                                   "rect"])})]
          (is (= 3 (count rects)))
          (doseq [rect rects]
            (is (= "rect"
                   (get-element-tag-el *driver* rect))))))

      ;; :elem
      (testing "a text (:elem piece) should have set attrs and styles"
        (is (= ["50" "50" "20" "middle"]
             (get-element-attrs
              *driver*
              {:css (string/join " "
                                 ["#test1"
                                  "svg"
                                  "g.rid3-main-container"
                                  "g.my-text"
                                  "text"])}
              "x"
              "y"
              "font-size"
              "text-anchor")))

        (is (= ["500" "sans-serif"]
               (get-element-csss
                *driver*
                {:css (string/join " "
                                   ["#test1"
                                    "svg"
                                    "g.rid3-main-container"
                                    "g.my-text"
                                    "text"])}
                "font-weight"
                "font-family")))
        )

      )))


(deftest ^:integration
  desktop-1920x1080
  (->test1 desktop-env))
