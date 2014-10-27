(ns ttt-server.game-test
  (:require [ttt-server.game   :refer :all]
            [ttt-server.db     :refer :all]
            [ttt-server.html   :refer :all]
            [tic-tac-toe.board :refer :all]
            [clojure.test      :refer :all]))

(defn string-contains? [string substring]
  (not (= -1 (.indexOf string substring))))

(deftest game-test
  (let [user-name "A User"
        game-name "The Game"
        user-id (first (vals (first (add-user user-name))))
        game-id (first (vals (first (add-game user-id game-name))))]
    
    (def GET-slash-request
      (new httpserver.HTTPRequest
           "GET / HTTP/1.1"
           (new java.util.HashMap)
           (.getBytes "")))

    (def POST-game-request
      (new httpserver.HTTPRequest
           "POST /game HTTP/1.1"
           (new java.util.HashMap)
           (.getBytes (str "user=" user-name "&game=" game-name))))

    (defn make-POST-move-request [game-id move]
      (new httpserver.HTTPRequest
           (str "POST /game/move HTTP/1.1")
           (str "game-id=" game-id)
           (new java.util.HashMap)
           (.getBytes (str "move=" move))))
    
    (testing "GET-slash returns an HTTPResponse with a form for a new game"
      (let [response (GET-slash GET-slash-request)
            body (new String (.body response))]

        (is (= "HTTP/1.1 200 OK" (.statusLine response)))
        (is (string-contains? body form-for-new-game))))
    
    (testing "make-html-board creates html-table version of board"
      (= "<table><tr><td>0</td><td>1</td><td>2</td><td>X</td></table>"
         (make-html-board [nil nil nil 0])))

    (testing "With invalid move, make-human-move returns the board it was given"
      (let [invalid-move-1 "bad move"
            invalid-move-2 "3"
            board [nil nil nil 1 0 0 nil nil nil]]
        
        (is (= board (make-human-move board invalid-move-1)))
        (is (= board (make-human-move board invalid-move-2)))))

    (testing "Given a valid move, make-human-move marks space in board"
      (let [board (gen-board)]
        (is (= [nil nil nil nil nil nil nil 0 nil]
             (make-human-move board "7")))))

    (testing "Given an invalid move, POST-move returns same html page again"
      (let [invalid-move "bad move"
            response-before-move (make-game-page game-id)
            the-request (make-POST-move-request game-id invalid-move)]

        (is (= (new String (.body response-before-move))
               (new String (.body (POST-move the-request)))))))

    (testing "Given a valid move, POST-move marks space in game database"
      (let [move "7"
            space-num (Integer/parseInt move)
            the-request (make-POST-move-request game-id move)]

        (is (nil? (retrieve-space-in-game game-id space-num)))
        (POST-move the-request)
        (is (not (nil? (retrieve-space-in-game game-id space-num))))))
    
    
    (delete-game user-id game-name)
    (delete-user user-name)


    ))
