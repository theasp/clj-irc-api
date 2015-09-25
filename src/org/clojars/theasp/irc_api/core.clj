(ns org.clojars.theasp.irc-api.core
  (:require
   [clojure.string :as str])
  (:import
   [com.ircclouds.irc.api IRCApiImpl IServerParameters Callback]
   [com.ircclouds.irc.api.domain IRCServer IRCUser]
   [com.ircclouds.irc.api.domain.messages UserPrivMsg]
   [com.ircclouds.irc.api.listeners IVariousMessageListener]))

;; Because of IRC's scandanavian origin, the characters {}| are
;; considered to be the lower case equivalents of the characters []\,
;; respectively. This is a critical issue when determining the
;; equivalence of two nicknames.
(def lower-case-escape {"[" "{"
                        "]" "}"
                        "\\" "|"})
(defn lower-case [string]
  (str/lower-case (str/escape string lower-case-escape)))

(defn make-irc
  ([]
   (make-irc true))
  ([save-irc-state]
   (new IRCApiImpl save-irc-state)))

(defn make-server-params [server-params]
  (let [{:keys [nick alt-nicks real-name ident server port password ssl?]} server-params]
    (reify IServerParameters
      (getServer [this]
        (new IRCServer server (int port) password ssl?))
      (getRealname [this]
        real-name)
      (getNickname [this]
        nick)
      (getIdent [this]
        ident)
      (getAlternativeNicknames [this]
        alt-nicks))))

(defn make-callback [success-fn fail-fn]
  (proxy [Callback] []
    (onSuccess [state]
      (when success-fn
        (success-fn state)))
    (onFailure [exception]
      (when fail-fn
        (fail-fn exception)))))

(defn make-listener [listener-fn]
  (reify IVariousMessageListener
    (onUserPing [this obj]
      (listener-fn :user-ping obj))
    (onUserVersion [this obj]
      (listener-fn :user-version obj))
    (onServerPing [this obj]
      (listener-fn :server-ping obj))
    (onMessage [this obj]
      (listener-fn :message obj))
    (onChannelMessage [this obj]
      (listener-fn :channel-message obj))
    (onChannelJoin [this obj]
      (listener-fn :channel-join obj))
    (onChannelPart [this obj]
      (listener-fn :channel-part obj))
    (onChannelNotice [this obj]
      (listener-fn :channel-notice obj))
    (onChannelAction [this obj]
      (listener-fn :channel-action obj))
    (onChannelKick [this obj]
      (listener-fn :channel-kick obj))
    (onTopicChange [this obj]
      (listener-fn :topic-change obj))
    (onUserPrivMessage [this obj]
      (listener-fn :user-priv-message obj))
    (onUserNotice [this obj]
      (listener-fn :user-notice obj))
    (onUserAction [this obj]
      (listener-fn :user-action obj))
    (onServerNumericMessage [this obj]
      (listener-fn :server-numeric-message obj))
    (onServerNotice [this obj]
      (listener-fn :server-notice obj))
    (onNickChange [this obj]
      (listener-fn :nick-change obj))
    (onUserQuit [this obj]
      (listener-fn :user-quit obj))
    (onError [this obj]
      (listener-fn :error obj))
    ;;      (onClientError [this obj]
    ;;      (listener-fn :client-error
    ;;      (client-error-fn obj))
    (onChannelMode [this obj]
      (listener-fn :channel-mode obj))))
;; (onUserAway [this obj]
;; (listener-fn :user-away obj))))))

(defn connect
  "Asynchronous connect

  Connect to an IRC server without enabling IRCv3.
  @param aServerParameters
           The IRC Server connection parameters
  @param cb
           A callback that will be invoked when the connection is
           established, and will return an {@link IIRCState} on success,
           or an {@link Exception} in case of failure"
  ([irc options success-fn fail-fn]
   (connect irc (make-server-params options) (make-callback success-fn fail-fn)))
  ([irc server-params cb]
   (.connect irc server-params cb)))

(defn disconnect
  "Synchronous disconnect

  @param aQuitMessage The Quit message"
  [irc quit-message]
  (.disconnect irc quit-message))

(defn join-channel
  "Asynchronous channel join

  @param aChannelName A Channel name
  @param aKey A channel key
  @param aCallback A callback that will return an {@link IRCChannel} on success, or an {@link Exception} in case of failure"
  ([irc channel-name key success-fn fail-fn]
   (join-channel irc channel-name key (make-callback success-fn fail-fn)))
  ([irc channel-name key cb]
   (.joinChannel irc channel-name key cb)))

(defn leave-channel
  "Asynchronous channel leave

  @param aChannelName A Channel name
  @param aCallback A callback that will return the left channel name in case of success, or an {@link Exception} in case of failure"
  ([irc channel-name part-message success-fn fail-fn]
   (leave-channel irc channel-name part-message (make-callback success-fn fail-fn)))
  ([irc channel-name part-message cb]
   (.leaveChannel irc channel-name part-message cb)))

(defn change-nick
  "Asynchronous nick change

  @param aNewNick A new nickname
  @param aCallback A callback that returns the new nick on success, or an {@link Exception} in case of failure"
  ([irc new-nick success-fn fail-fn]
   (change-nick irc new-nick (make-callback success-fn fail-fn)))
  ([irc new-nick cb]
   (.changeNick irc new-nick cb)))

(defn message
  "Asynchronous Private message

  @param aTarget Can be a channel or a nickname
  @param aMessage A message
  @param aCallback A callback that will return the sent message on success, or an {@link Exception} in case of failure"
  ([irc channel-name message success-fn fail-fn]
   (message irc channel-name message (make-callback success-fn fail-fn)))
  ([irc channel-name message cb]
   (.message irc channel-name message cb)))

(defn act
  "Asynchronous Action message

  @param aTarget Can be a channel or a nickname
  @param aMessage A message
  @param aCallback A callback that will return the sent action message on success, or an {@link Exception} in case of failure"
  ([irc channel-name message success-fn fail-fn]
   (act irc channel-name message (make-callback success-fn fail-fn)))
  ([irc channel-name message cb]
   (.act irc channel-name message cb)))

(defn notice
  "Asynchronous Notice message

  @param aTarget Can be a channel or a nickname
  @param aMessage A message
  @param aCallback A callback that will return the sent notice message on success, or an {@link Exception} in case of failure"
  ([irc channel-name message success-fn fail-fn]
   (notice irc channel-name message (make-callback success-fn fail-fn)))
  ([irc channel-name message cb]
   (.notice irc channel-name message cb)))

(defn kick
  "Asynchronous kick message

  @param aChannel A channel name
  @param aNick A nick to be kicked
  @param aKickMessage A kick message
  @param aCallback A callback that will return an empty message on success, or an {@link Exception} in case of failure"
  ([irc channel-name nick message success-fn fail-fn]
   (kick irc channel-name nick message (make-callback success-fn fail-fn)))
  ([irc channel-name nick message cb]
   (.kick irc channel-name nick message cb)))

;; TODO: DCC

(defn change-topic
  "Synchronous change topic

   @param aChannel A channel name
   @param aTopic A new topic"
  [irc channel-name topic]
  (.changeTopic irc channel-name topic))


(defn change-mode
  "Synchronous change mode

   @param aModeString This will basically execute a 'mode ' + aModeString"
  [irc mode-string]
  (.changeMode irc mode-string))

(defn raw-message
  "Synchronous raw message

   @param message A raw text message to be sent to the IRC server"
  [irc message]
  (.rawMessage message))


(defn add-listener
  "Adds a message listener

   @param aListener A message listener"
  [irc listener]
  (.addListener irc listener))

(defn delete-listener
  "Deletes a message listener

   @param aListener A message listener"
  [irc listener]
  (.deleteListener irc listener))

(defn set-message-filter
  "Sets a message filter

   @param aFilter A message filter"
  [irc filter]
  (.setMessageFilter irc filter))
