(ns org.clojars.theasp.irc-api.decode
  (:import
   [com.ircclouds.irc.api IRCApiImpl IServerParameters]
   [com.ircclouds.irc.api.domain
    IRCServer IRCUser]
   [com.ircclouds.irc.api.domain.messages
    UserPrivMsg]
   [com.ircclouds.irc.api.domain.messages.interfaces
    IChannelMessage IHasNumericCode IHasText IUserMessage IServerMessage]))

(defn irc-server [obj]
  (when obj
    {:password (.getPassword obj)
     :hostname (.getHostname obj)
     :port (.getPort obj)
     ;;:resolve-by-proxy? (.isResolveByProxy obj)
     :ssl? (.isSSL obj)}))

(defn irc-user [obj]
  (when obj
    {:nick (.getNick obj)
     :ident (.getIdent obj)
     :hostname (.getHostname obj)}))

(defn i-server-paramaters [obj]
  (when obj
    {:server (irc-server (.getServer obj))
     :real-name (.getRealname obj)
     :nick (.getNickname obj)
     :ident (.getIdent obj)
     :alt-nicks (.getAlternativeNicknames obj)}))

(defn i-channel-message [obj]
  (when obj
    {:channel-name (.getChannelName obj)}))

(defn i-has-numeric-code [obj]
  (when obj
    {:numeric-code (.getNumericCode obj)}))

(defn i-has-text [obj]
  (when obj
    {:text (.getText obj)}))

(defn i-server-message [obj]
  (when obj
    {:source (irc-server (.getSource obj))}))

(defn i-user-message [obj]
  (when obj
    {:source (irc-user (.getSource obj))}))

;; public class UserPrivMsg extends AbstractPrivMsg
;; public abstract class AbstractPrivMsg implements IUserMessage, IHasText
(defn user-priv-msg [obj]
  (when obj
    {:to (.getToUser obj)}))

(defn decode [obj]
  (when obj
    (cond-> {}
      (instance? IServerParameters obj) (merge (i-server-paramaters obj))
      (instance? IChannelMessage obj) (merge (i-channel-message obj))
      (instance? IHasNumericCode obj) (merge (i-has-numeric-code obj))
      (instance? IHasText obj) (merge (i-has-text obj))
      (instance? IServerMessage obj) (merge (i-server-message obj))
      (instance? IUserMessage obj) (merge (i-user-message obj))
      (instance? UserPrivMsg obj) (merge (user-priv-msg obj))
      (instance? IRCUser obj) (merge (irc-user obj))
      (instance? IRCServer obj) (merge (irc-server obj)))))
