(ns ttt-server.db-test
  (:require [ttt-server.db :refer :all]
            [clojure.test  :refer :all]
            [clojure.java.jdbc :as jdbc]))

(defn drop-table [name]
  (if (table-exists? name)
    (jdbc/execute! mysql-db [(str "drop table " name)])))

(defn clear-table [name]
  (if (table-exists? name)
    (jdbc/execute! mysql-db [(str "truncate " name)])))

(defn clear-tables [names]
  (map #(clear-table %) names))

(defn test-fixture [f]
  (clear-tables ["user" "3x3_game"])
  (f)
  (clear-tables ["user" "3x3_game"]))

(use-fixtures :each test-fixture)

(deftest tables-test
  (testing "Creates user table if doesn't exist"
    (drop-table "user")
    (is (not (table-exists? "user")))
    (user-table)
    (is (table-exists? "user")))

  (testing "Creates game table if doesn't exist"
    (drop-table "3x3_game")
    (is (not (table-exists? "3x3_game")))
    (game-table)
    (is (table-exists? "3x3_game"))))

(deftest user-test
  (let [name "Bon Scott"]
    (testing "Adds a user"
      (is (not (user-exists? name)))
      (add-user name)
      (is (user-exists? name)))

    (testing "Deletes a user"
      (delete-user name)
      (is (not (user-exists? name))))
    
    (testing "Retrieves a user's id"
      (add-user name)
      (is (= (retrieve-user-id name)
             (first (jdbc/query mysql-db
                                ["select * from user where name = ?" name]
                                :row-fn :id)))))

    (testing "retrieve-user-id creates user if user doesn't exist"
      (delete-user name)
      (is (not (user-exists? name)))
      (retrieve-user-id name)
      (is (user-exists? name)))

    (testing "retrieve-user-id returns correct id of new user"
      (let [new-name "New User 2"
            user-id (retrieve-user-id new-name)]
        (is (= user-id (retrieve-user-id new-name)))))))

(deftest game-test
  (let [user-name "Cookie Monster"
        game-name "Game of the Century"
        user-id (retrieve-user-id user-name)]

    (testing "Adds and deletes a game"
      (is (not (game-exists? user-id game-name)))
      (add-game user-id game-name)
      (is (game-exists? user-id game-name))
      (delete-game user-id game-name)
      (is (not (game-exists? user-id game-name))))
    
    (testing "Retrieves a game's id"
      (add-game user-id game-name)
      (is (= (retrieve-game-id user-id game-name)
             (first (jdbc/query mysql-db
                                [(str "select * from " (key-to-str (game-table))
                                      " where player_one_id = ? and name = ?")
                                 user-id game-name]
                                :row-fn :id))))
      (delete-game user-id game-name))

    (testing "retrieve-game-id creates game if game doesn't exist"
      (is (not (game-exists? user-id game-name)))
      (retrieve-game-id user-id game-name)
      (is (game-exists? user-id game-name))
      (delete-game user-id game-name))
    
    (testing "retrieve-game-id returns correct id of new game"
      (let [game-id (retrieve-game-id user-id game-name)]
        (is (= game-id (retrieve-game-id user-id game-name)))
        (delete-game user-id game-name)))))

(deftest spaces-board-test
  (let [user-name "Chicago Bulls"
        game-name "Game 7"
        user-id (retrieve-user-id user-name)
        game-id (retrieve-game-id user-id game-name)]
    
    (testing "Retrieves board vector from game in database"
      (jdbc/update! mysql-db (game-table) {:space_one 1} ["id = ?" game-id])
      (is (= [nil 1   nil
              nil nil nil
              nil nil nil]
             (retrieve-game-board game-id)))
      (jdbc/update! mysql-db (game-table) {:space_one nil} ["id = ?" game-id]))

    (testing "Retrieves a space's value from game in database"
      (let [space-num 3
            token     1]
        (jdbc/update! mysql-db (game-table)
                      {:space_three token} ["id = ?" game-id])
        (is (= token (retrieve-space-in-game game-id space-num)))
        (jdbc/update! mysql-db (game-table)
                      {:space_three nil} ["id = ?" game-id])))

    (testing "Sets a space's value in game in database"
      (let [space-num 4
            token     0]
        (is (not (= token (retrieve-space-in-game game-id space-num))))
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
        (is (= board (retrieve-game-board game-id)))))))
