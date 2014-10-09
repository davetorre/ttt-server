(ns ttt-server.db-test
  (:require [ttt-server.db :refer :all]
            [clojure.test  :refer :all]
            [clojure.java.jdbc :as jdbc]))

(deftest db-test

  (testing "Creates user table if doesn't exist"
    (jdbc/execute! mysql-db ["drop table user"])
    (is (not (table-exists? "user")))
    (user-table)
    (is (table-exists? "user")))

  (testing "Creates game table if doesn't exist"
    (jdbc/execute! mysql-db ["drop table 3x3_game"])
    (is (not (table-exists? "3x3_game")))
    (game-table)
    (is (table-exists? "3x3_game")))  
  
  (let [user-name "Test User"
        game-name "Test Game"
        user-id (first (vals (first (add-user user-name))))
        game-id (first (vals (first (add-game user-id game-name))))]
    
    (testing "Adds and deletes a user"
      (let [name "Some User"]
        (add-user name)
        (is (user-exists? name))
        (delete-user name)
        (is (not (user-exists? name)))))
    
    (testing "Retrieves a user's id"
      (is (= user-id (retrieve-user-id user-name))))

    (testing "retrieve-user-id creates user if user doesn't exist"
      (let [name "No User 1"]
        (is (not (user-exists? name)))
        (retrieve-user-id name)
        (is (user-exists? name))
        (delete-user name)))

    (testing "retrieve-user-id returns correct id of new user"
      (let [name "No User 2"
            user-id (retrieve-user-id name)]
        (is (= user-id
               (first (jdbc/query mysql-db
                                  ["select * from user where name = ?" name]
                                  :row-fn :id))))
        (delete-user name)))
    
    (testing "Adds and deletes a game"
      (let [game-name "Some Game"]
        (add-game user-id game-name)
        (is (game-exists? user-id game-name))
        (delete-game user-id game-name)
        (is (not (game-exists? user-id game-name)))))

    (testing "Retrieves a game's id"
      (is (= game-id (retrieve-game-id user-id game-name))))

    (testing "retrieve-game-id creates game if game doesn't exist"
      (let [game-name "No Game 1"]
        (is (not (game-exists? user-id game-name)))
        (retrieve-game-id user-id game-name)
        (is (game-exists? user-id game-name))
        (delete-game user-id game-name)))

    (testing "retrieve-game-id returns correct id of new game"
      (let [game-name "No Game 2"
            game-id (retrieve-game-id user-id game-name)]
        (is (= game-id
               (first (jdbc/query
                       mysql-db
                       ["select * from 3x3_game where player_one_id = ? and name = ?"
                        user-id game-name]
                       :row-fn :id))))
        (delete-game user-id game-name)))

    (testing "Retrieves board vector from game in database"
      (jdbc/update! mysql-db :3x3_game {:space_one 1} ["id = ?" game-id])
      (is (= [nil 1   nil
              nil nil nil
              nil nil nil]
             (retrieve-game-board game-id)))

      (jdbc/update! mysql-db :3x3_game {:space_one nil} ["id = ?" game-id]))

    (testing "Retrieves a space's value from game in database"
      (let [space-num 3
            token     1]
        (jdbc/update! mysql-db :3x3_game {:space_three token} ["id = ?" game-id])
        (is (= token (retrieve-space-in-game game-id space-num)))

        (jdbc/update! mysql-db :3x3_game {:space_three nil} ["id = ?" game-id])))
    
    (testing "Sets a space's value in game in database"
      (let [space-num 4
            token     0]
        (set-space-in-game game-id space-num token)
        (is (= token (retrieve-space-in-game game-id space-num)))

        (set-space-in-game game-id space-num nil)))

    (testing "Resets game board in game in database"
      (set-space-in-game game-id 0 0)
      (set-space-in-game game-id 4 1)
      (set-space-in-game game-id 6 0)
      (is (= [0 nil nil nil 1 nil 0 nil nil]
             (retrieve-game-board game-id)))

      (reset-game-board game-id)
      (is (= [nil nil nil nil nil nil nil nil nil]
             (retrieve-game-board game-id))))

    (testing "Sets game board in game in database"
      (let [board [0 1 0 1 1 0 nil nil nil]]

        (set-game-board game-id board)
        (is (= board
               (retrieve-game-board game-id)))

        (reset-game-board game-id)))

    ; Teardown needs to always happen, even if there's an error in the tests
    (delete-game user-id game-name)
    (delete-user user-name))



  )
