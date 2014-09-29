(ns ttt-server.game-test
  (:require [ttt-server.game :refer :all]
            [ttt-server.db   :refer :all]
            [clojure.test    :refer :all]))
  
(deftest game-test
  (let [user-name "A User"
        game-name "The Game"]

    (def GET-request
      (new davetorre.httpserver.HTTPRequest
           "GET /new_game HTTP/1.1"
           (new java.util.HashMap)
           (.getBytes "")))

    (def POST-request
      (new davetorre.httpserver.HTTPRequest
           "POST /new_game HTTP/1.1"
           (new java.util.HashMap)
           (.getBytes (str "user=" user-name "&game=" game-name))))

    (testing "get-new-game returns an HTTPResponse with a form for a new game"
      (let [response (get-new-game GET-request)
            body (new String (.body response))]

        (is (= "HTTP/1.1 200 OK\n" (.statusLine response)))
        (is (not (= -1 (.indexOf body new-game-form))))))

    (testing "get-form-values gets form values from POST request"
      (is (= [user-name game-name]
             (get-form-values POST-request))))
    
    (testing "post-new-game adds a new game to the game database"
      (add-user user-name)

      (let [user-id (retrieve-user-id user-name)]
        (is (not (game-exists? user-id game-name)))
        (post-new-game POST-request)
        (is (game-exists? user-id game-name))

        (delete-game user-id game-name)
        (delete-user user-name)))

    (testing "post-new-game adds user if user doeesn't exist"
      (is (not (user-exists? user-name)))
      (post-new-game POST-request)
      (is (user-exists? user-name))
      
      (delete-game (retrieve-user-id user-name) game-name)
      (delete-user user-name))

    
))
