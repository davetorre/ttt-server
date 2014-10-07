(ns ttt-server.core
  (:require [ttt-server.game :refer :all]
            [ttt-server.db   :refer :all]))

(defn make-route-lamb [function-to-call]
  (reify
    httpserver.ResponderCreator
    (create [this]
      (reify
        httpserver.Responder
        (respond [this request]
          (function-to-call request))))))

(def router
  (doto (new httpserver.DefaultRouter)
    (.addRoute "GET /" (make-route-lamb GET-slash))
    (.addRoute "POST /game" (make-route-lamb POST-game))
    (.addRoute "POST /game/move" (make-route-lamb POST-move))))

(defn -main [& args]
  (let [server (new httpserver.Server router)]
    (.serve server 5000)))
