(ns ttt-server.game
  (:require [ttt-server.db      :refer :all]
            [ttt-server.html    :refer :all]
            [tic-tac-toe.board  :refer :all]
            [tic-tac-toe.player :refer :all]
            [tic-tac-toe.rules  :refer :all]))

(defn make-html-board [board]
  (-> (get-nice-board board)
      (get-rows)
      (make-table)))

(defn response-with-body [body]
  (new httpserver.HTTPResponse
       "HTTP/1.1 200 OK"
       (new java.util.HashMap)
       (.getBytes body)))

(defn make-game-status [board]
  (if (game-over? board)
    (let [token (get-winner board)
          player (get-player-string token)]
      (if token
        (str "Game over. " player " wins!")
        ("Game over. Draw.")))
    " "))

(defn make-game-page [game-id]
  (let [board (retrieve-game-board game-id)
        html-board (make-html-board board) 
        form  (form-for-new-move game-id)
        status (enclose-in-p-tags (make-game-status board))]
    (response-with-body (enclose-in-html (str html-board form status)))))

(defn GET-slash [request]
  (response-with-body (enclose-in-html form-for-new-game)))

(defn get-values [some-string]
  (let [key-value-pairs (clojure.string/split some-string #"&")]
    (map #(second (clojure.string/split % #"=")) key-value-pairs)))

(defn body-as-string [request]
  (new String (.body request)))

(defn make-human-move [board move]
  (let [open-spaces (get-open-spaces board)
        open-spaces-strings (map #(str %) open-spaces)]
    (if (contains? (set open-spaces-strings) move)
      (set-space board (Integer/parseInt move) (get-token board))
      board)))

(defn POST-move [request]
  (let [game-id (first (get-values (.parameters request)))
        move (first (get-values (body-as-string request)))
        board (retrieve-game-board game-id)
        board-after-human-move (make-human-move board move)]

    (if-not (= board board-after-human-move)
      (set-game-board game-id
                      (make-move (new-minmax-player) board-after-human-move)))

    (make-game-page game-id)))

(defn POST-game [request]
  (let [form-values (get-values (body-as-string request))
        user-name (first form-values)
        game-name (second form-values)
        user-id (retrieve-user-id user-name)
        game-id (retrieve-game-id user-id game-name)]
    (make-game-page game-id)))
