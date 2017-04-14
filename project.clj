(defproject rid3 "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.6.1"]
                 [cljsjs/d3 "4.3.0-4"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/main"]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :clean-targets ^{:protect false} ["resources/public/js"
                                    "target"]

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.8.2"]
                   [re-frisk "0.3.1"]
                   [garden "1.3.2"]]
    :plugins      [[lein-figwheel "0.5.10"]
                   [lein-externs "0.1.6"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/demo" "src/main"]
     :figwheel     {:on-jsload "rid3.demo/reload"}
     :compiler     {:main                 rid3.demo
                    :optimizations        :none
                    :output-to            "resources/public/js/app.js"
                    :output-dir           "resources/public/js/dev"
                    :asset-path           "js/dev"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config
                    {:devtools/config
                     {:features-to-install    [:formatters :hints]
                      :fn-symbol              "F"
                      :print-config-overrides true}}}}

    {:id           "min"
     :source-paths ["src/demo" "src/main"]
     :compiler     {:main            rid3.demo
                    :optimizations   :advanced
                    :output-to       "resources/public/js/app.js"
                    :output-dir      "resources/public/js/min"
                    :externs         ["externs.js"]
                    :elide-asserts   true
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    ]})
