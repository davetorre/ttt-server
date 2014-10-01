(ns ttt-server.game
  (:require [ttt-server.db     :refer :all]
            [ttt-server.html   :refer :all]
            [tic-tac-toe.board :refer :all]))

(defn make-board [board]
  (-> (get-nice-board board)
      (get-rows)
      (make-table)))

(defn make-game-page [game-id]
  (let [board (make-board (retrieve-game-board game-id)) 
        form  (form-for-new-move game-id)]
    (new davetorre.httpserver.HTTPResponse
         "HTTP/1.1 200 OK\n"
         (new java.util.HashMap)
         (.getBytes (enclose-in-html (str board form))))))

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
