(ns example-project.core
  (:require
   [taoensso.timbre :as timbre :refer (tracef debugf infof warnf errorf)]
   [org.clojars.theasp.irc-api.core :as irc-api]
   [org.clojars.theasp.irc-api.decode :as irc-decode])
  (:gen-class))

(defn connect-ok [irc-state]
  (debugf "Connected!"))

(defn connect-fail [e]
  (debugf "Connection failed: %s" e))

(defn listener-callback [type obj]
  (debugf "Listener received %s: %s" type (irc-decode/decode obj)))

(defn -main [& args]
  (let [server {:nick "irc-api-test"
                :alt-nicks ["irc-api-test1" "irc-api-test2" "irc-api-test3"]
                :real-name "irc-api-test"
                :ident "irc-api"
                :server "irz.freenode.net"
                :port 6667
                :ssl? false}
        server-params (irc-api/make-server-params server)
        connect-callback (irc-api/make-callback connect-ok connect-fail)
        listener (irc-api/make-listener listener-callback)
        irc (irc-api/make-irc)]
    (debugf "Adding listener")
    (irc-api/add-listener irc listener)
    (debugf "Connecting to %s:%s" (:server server) (:port server))
    (irc-api/connect irc server-params connect-callback)
    (Thread/sleep (* 15 1000))
    (debugf "Disconnecting")
    (irc-api/disconnect irc "Quit")
    (Thread/sleep (* 1 1000))
    (System/exit 0)))
