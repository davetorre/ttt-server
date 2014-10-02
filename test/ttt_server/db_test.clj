(ns ttt-server.db-test
  (:require [ttt-server.db :refer :all]
            [clojure.test  :refer :all]
            [clojure.java.jdbc :as jdbc]))

(deftest db-test
  
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
      (is (= [nil 1 nil
              nil nil nil
              nil nil nil]
             (retrieve-game-board game-id))))

    (testing "Retrieves a space's value from game in database"
      (let [space-num 3
            token     1]
        (jdbc/update! mysql-db :3x3_game {:space_three token} ["id = ?" game-id])
        (is (= token (retrieve-space-in-game game-id space-num)))))
    
    (testing "Sets a space's value in game in database"
      (let [space-num 4
            token     0]
        (set-space-in-game game-id space-num token)
        (is (= token (retrieve-space-in-game game-id space-num)))))

    (delete-game user-id game-name)
    (delete-user user-name))



  )
