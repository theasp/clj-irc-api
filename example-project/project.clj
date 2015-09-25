(defproject org.clojars.theasp/irc-api-example "0.0.1"
  :description "Example project for irc-api"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojars.theasp/irc-api "0.0.1"]
                 [com.taoensso/timbre "4.1.1"]]
  :main ^:skip-aot example-project.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
