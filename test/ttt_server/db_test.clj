(ns ttt-server.db-test
  (:require [ttt-server.db :refer :all]
            [clojure.test  :refer :all]
            [clojure.java.jdbc :as jdbc]))

(deftest db-test
  (let [user-name "Temp User"]
    (add-user user-name)

    (testing "Adds and deletes a user"
      (let [name "Another User"]
        (add-user name)
        (is (user-exists? name))
        (delete-user name)
        (is (not (user-exists? name)))))

    (testing "Retrieves a user's id"
      (is (< 0 (retrieve-user-id user-name))))

    (testing "Adds and deletes a game"
      (let [user-id (retrieve-user-id user-name)
            game-name "Some Game"]
        (add-game user-id game-name)
        (is (game-exists? user-id game-name))
        (delete-game user-id game-name)
        (is (not (game-exists? user-id game-name)))))

    (testing "Retrieves a game's id"
      (let [user-id (retrieve-user-id user-name)
            game-name "My Game"]
        (add-game user-id game-name)
        (is (< 0 (retrieve-game-id user-id game-name)))
        (delete-game user-id game-name)))

    (testing "Retrieves board vector from game in database"
      (let [user-id (retrieve-user-id user-name)
            game-name "My Game"]
        (add-game user-id game-name)

        (let [game-id (retrieve-game-id user-id game-name)]
          (jdbc/update! mysql-db :3x3_game {:space_one 2} ["id = ?" game-id])
          (is (= [0 2 0 0 0 0 0 0 0] (retrieve-game-board game-id)))

          (delete-game user-id game-name))))

    
    
    (delete-user user-name)
))

