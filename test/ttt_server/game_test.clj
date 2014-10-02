(ns ttt-server.game-test
  (:require [ttt-server.game :refer :all]
            [ttt-server.db   :refer :all]
            [ttt-server.html :refer :all]
            [clojure.test    :refer :all]))

(defn string-contains? [string substring]
  (not (= -1 (.indexOf string substring))))

(deftest game-test
  (let [user-name "A User"
        game-name "The Game"
        user-id (first (vals (first (add-user user-name))))
        game-id (first (vals (first (add-game user-id game-name))))]
    
    (def GET-slash-request
      (new davetorre.httpserver.HTTPRequest
           "GET / HTTP/1.1"
           (new java.util.HashMap)
           (.getBytes "")))

    (def POST-game-request
      (new davetorre.httpserver.HTTPRequest
           "POST /game HTTP/1.1"
           (new java.util.HashMap)
           (.getBytes (str "user=" user-name "&game=" game-name))))

    (defn make-POST-move-request [game-id move]
      (new davetorre.httpserver.HTTPRequest
           (str "POST /game/move HTTP/1.1")
           (str "game-id=" game-id)
           (new java.util.HashMap)
           (.getBytes (str "move=" move))))
    
    (testing "GET-slash returns an HTTPResponse with a form for a new game"
      (let [response (GET-slash GET-slash-request)
            body (new String (.body response))]

        (is (= "HTTP/1.1 200 OK\n" (.statusLine response)))
        (is (string-contains? body form-for-new-game))))
    
    (testing "get-values gets values from a string"
      (is (= ["Val" "Mom" "OK"]
             (get-values "Key=Val&Hi=Mom&Yes=OK"))) 
      (is (= [user-name game-name]
             (get-values (body-as-string POST-game-request)))))

    (testing "make-board creates html-table version of board"
      (= "<table><tr><td>0</td><td>1</td><td>2</td><td>X</td></table>"
         (make-board [nil nil nil 0])))

    (testing "Given an invalid move, make-move doesn't change board in db"
      (let [invalid-move "bad move"
            game-board-before-move (retrieve-game-board game-id)]
        
        (make-move game-id invalid-move)
        (is (= game-board-before-move (retrieve-game-board game-id)))))

    (testing "Given a valid move, make-move marks space in db")
    
    (testing "Given an invalid move, POST-move returns same html page again"
      (let [invalid-move "bad move"
            response-before-move (make-game-page game-id)
            the-request (make-POST-move-request game-id invalid-move)]

        (is (= (new String (.body response-before-move))
               (new String (.body (POST-move the-request)))))))

    (testing "Given valid move, POST-move returns html page with updated board")
    
    
    (delete-game user-id game-name)
    (delete-user user-name)


    ))
