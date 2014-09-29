(ns ttt-server.core
  (:require [ttt-server.game :refer :all]
            [ttt-server.db   :refer :all]))

(defn make-route-lamb [function-to-call]
  (reify
    davetorre.httpserver.ResponderCreator
    (create [this]
      (reify
        davetorre.httpserver.Responder
        (respond [this request]
          (function-to-call request))))))

(def router
  (doto (new davetorre.httpserver.Router)
    (.addRoute "GET /new_game" (make-route-lamb get-new-game))
    (.addRoute "POST /new_game" (make-route-lamb post-new-game))))

(defn -main [& args]
  (let [server (new davetorre.httpserver.Server router)]
    (.serve server 5000)))
