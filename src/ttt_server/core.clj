(ns ttt-server.core
  (:require [ttt-server.server-io :refer :all]))

(def board "Where the board goes.")

(def form
  "<form action=\"/move\" method=\"post\" >
  <div>
    <input name=\"the-move\">
  </div>
  <div class=\"button\">
    <button type=\"submit\">Send</button>
  </div>
</form>")

(def game-page
  (str "<!DOCTYPE html><html><head></head><body>"
       board
       form
       "</body></html"))

(defn get-gamepage [request]
  (new davetorre.httpserver.HTTPResponse
       "HTTP/1.1 200 OK\n"
       (new java.util.HashMap)
       (.getBytes game-page)))

(defn post-move [request]
  (new davetorre.httpserver.HTTPResponse
       "HTTP/1.1 200 OK\n"
       (new java.util.HashMap)
       (.body request)))

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
    (.addRoute "GET /" (make-route-lamb get-gamepage))
    (.addRoute "POST /move" (make-route-lamb post-move))))


(defn -main [& args]
  (let [server (new davetorre.httpserver.Server router)]
    (.serve server 5000)))
