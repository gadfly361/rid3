(defproject rid3 "0.2.1-1"
  :description "Reagent Interface to D3"
  :url "https://github.com/gadfly361/rid3"
  :license {:name "MIT"}
  :scm {:name "git"
        :url  "https://github.com/gadfly361/rid3"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.6.1"]
                 [cljsjs/d3 "4.3.0-4"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/main"]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :clean-targets ^{:protect false} ["dev-resources/public/js"
                                    "target"]

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.8.2"]
                   [re-frisk "0.3.1"]
                   [garden "1.3.2"]
                   [etaoin "0.2.7"]]
    :plugins      [[lein-figwheel "0.5.10"]
                   [lein-externs "0.1.6"]]}}

  :cljsbuild
  {:builds
   [;; basics
    {:id           "basics-dev"
     :source-paths ["src/basics" "src/main" "src/version"]
     :figwheel     {:on-jsload "rid3.basics/reload"}
     :compiler     {:main                 rid3.basics
                    :optimizations        :none
                    :output-to            "dev-resources/public/js/basics-dev.js"
                    :output-dir           "dev-resources/public/js/basics-dev"
                    :asset-path           "js/basics-dev"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config
                    {:devtools/config
                     {:features-to-install    [:formatters :hints]
                      :fn-symbol              "F"
                      :print-config-overrides true}}}}

    {:id           "basics-min"
     :source-paths ["src/basics" "src/main"  "src/version"]
     :compiler     {:main            rid3.basics
                    :optimizations   :advanced
                    :output-to       "dev-resources/public/js/basics.js"
                    :output-dir      "dev-resources/public/js/basics-min"
                    :externs         ["externs.js"]
                    :elide-asserts   true
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}


    ;; examples
    {:id           "examples-dev"
     :source-paths ["src/examples" "src/main" "src/version"]
     :figwheel     {:on-jsload "rid3.examples/reload"}
     :compiler     {:main                 rid3.examples
                    :optimizations        :none
                    :output-to            "dev-resources/public/js/examples-dev.js"
                    :output-dir           "dev-resources/public/js/examples-dev"
                    :asset-path           "js/examples-dev"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config
                    {:devtools/config
                     {:features-to-install    [:formatters :hints]
                      :fn-symbol              "F"
                      :print-config-overrides true}}}}

    {:id           "examples-min"
     :source-paths ["src/examples" "src/main"  "src/version"]
     :compiler     {:main            rid3.examples
                    :optimizations   :advanced
                    :output-to       "dev-resources/public/js/examples.js"
                    :output-dir      "dev-resources/public/js/examples-min"
                    :externs         ["externs.js"]
                    :elide-asserts   true
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    ;; integration
    {:id           "integration-dev"
     :source-paths ["src/integration" "src/main" "src/version"]
     :figwheel     {:on-jsload "rid3.integration/reload"}
     :compiler     {:main                 rid3.integration
                    :optimizations        :none
                    :output-to            "dev-resources/public/js/integration.js"
                    :output-dir           "dev-resources/public/js/integration-dev"
                    :asset-path           "js/integration-dev"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config
                    {:devtools/config
                     {:features-to-install    [:formatters :hints]
                      :fn-symbol              "F"
                      :print-config-overrides true}}}}
    ]})
