(ns ttt-server.db-test
  (:require [ttt-server.db :refer :all]
            [clojure.test  :refer :all]))

(deftest db-test
  (let [user-name "Temp User"]
    (add-user user-name)

    (testing "Adds and deletes a user"
      (let [name "Another User"]
        (add-user name)
        (is (user-exists? name))
        (delete-user name)
        (is (not (user-exists? name)))))

    ; How can I know what a user's id should be?
    (testing "Retrieves a user's id"
      (is (< 0 (retrieve-user-id user-name))))

    (testing "Adds and deletes a game"
      (let [user-id (retrieve-user-id user-name)
            game-name "Some Game"]
        (add-game user-id game-name)
        (is (game-exists? user-id game-name))
        (delete-game user-id game-name)
        (is (not (game-exists? user-id game-name)))))

    ; How can I know what a game's id should be?
    (testing "Retrieves a game's id"
      (let [user-id (retrieve-user-id user-name)
            game-name "My Game"]
        (add-game user-id game-name)
        (is (< 0 (retrieve-game-id user-id game-name)))
        (delete-game user-id game-name)))
    
    (delete-user user-name)
))

