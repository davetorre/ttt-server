(ns ttt-server.user-test
  (:require [ttt-server.user :refer :all]
            [clojure.test    :refer :all]
            [ttt-server.test-helper :as helper]
            [ttt-server.db          :as db]))

(deftest user-test
  (def GET-user-request
    (new httpserver.HTTPRequest
         "GET /user HTTP/1.1"
         (new java.util.HashMap)
         (.getBytes "")))

  (defn make-GET-user-request [user-id]
    (new httpserver.HTTPRequest
         (str "GET /user HTTP/1.1")
         (new java.util.HashMap)
         (.getBytes (str "user-id=" user-id))))

  (let [user-name "John Doe"
        game1-name "Game 1"
        game2-name "Game 2"
        game3-name "Game 3"
        user-id (first (vals (first (db/add-user user-name))))
        game1-id (first (vals (first (db/add-game user-id game1-name))))
        game2-id (first (vals (first (db/add-game user-id game2-name))))
        game3-id (first (vals (first (db/add-game user-id game3-name))))]
    
    (testing "GET-user returns an HTTPResponse containing the user's name"
      (let [request (make-GET-user-request user-id)
            response (GET-user request)
            body (new String (.body response))]

        (is (= "HTTP/1.1 200 OK" (.statusLine response)))
        (is (helper/string-contains? body user-name))))
     
     )
  
  )
