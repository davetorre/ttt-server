(ns ttt-server.game-test
  (:require [ttt-server.game :refer :all]
            [ttt-server.db   :refer :all]
            [ttt-server.html :refer :all]
            [clojure.test    :refer :all]))

(defn string-contains? [string substring]
  (not (= -1 (.indexOf string substring))))

(deftest game-test
  (let [user-name "A User"
        game-name "The Game"]

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
    
    (testing "POST-game adds a new game to the game database"
      (add-user user-name)

      (let [user-id (retrieve-user-id user-name)]
        (is (not (game-exists? user-id game-name)))
        (POST-game POST-game-request)
        (is (game-exists? user-id game-name))

        (delete-game user-id game-name)
        (delete-user user-name)))

    (testing "POST-game adds user if user doeesn't exist"
      (is (not (user-exists? user-name)))
      (POST-game POST-game-request)
      (is (user-exists? user-name))
      
      (delete-game (retrieve-user-id user-name) game-name)
      (delete-user user-name))

    (testing "make-board creates html-table version of board"
      (= "<table><tr><td>0</td><td>1</td><td>2</td><td>X</td></table>"
         (make-board [nil nil nil 0])))

 
))
