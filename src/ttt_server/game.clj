(ns ttt-server.game
  (:require [ttt-server.db   :refer :all]
            [ttt-server.html :refer :all]))

(defn make-board [game-id]
  "The board representation.")

(defn make-game-page [game-id]
  (new davetorre.httpserver.HTTPResponse
       "HTTP/1.1 200 OK\n"
       (new java.util.HashMap)
       (.getBytes
        (enclose-in-html
         (str (make-board game-id) (form-for-new-move game-id))))))

(defn GET-slash [request] 
  (new davetorre.httpserver.HTTPResponse
       "HTTP/1.1 200 OK\n"
       (new java.util.HashMap)
       (.getBytes (enclose-in-html form-for-new-game))))

(defn get-values [some-string]
  (let [key-value-pairs (clojure.string/split some-string #"&")]
    (map #(second (clojure.string/split % #"=")) key-value-pairs)))

(defn body-as-string [request]
  (new String (.body request)))

(defn POST-move [request]
  (let [game-id (first (get-values (.parameters request)))
        move (first (get-values (body-as-string request)))]
    ;(make-move-in-game game-id move)
    (make-game-page game-id)))
  
(defn POST-game [request]
  (let [form-values (get-values (body-as-string request))
        user-name (first form-values)
        game-name (second form-values)]

    (if-not (user-exists? user-name) (add-user user-name))

    (let [user-id (retrieve-user-id user-name)]
      (add-game user-id game-name)
      (make-game-page (retrieve-game-id user-id game-name)))))
