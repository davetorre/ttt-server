(ns ttt-server.db
  (:require [clojure.java.jdbc :as jdbc]))

(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/ttt_server"
               :user "admin"
               :password "password"})

(defn user-exists? [name]
  (< 0 (count (jdbc/query mysql-db
                      ["select * from user where name = ?" name]
                      :row-fn :name))))

(defn add-user [name]
  (jdbc/insert! mysql-db :user
             {:name name}))

(defn delete-user [name]
  (jdbc/delete! mysql-db :user ["name = ?" name]))

(defn retrieve-user-id
  "Returns a user's id. If user doesn't exist, creates user."
  [name]
  (if-not (user-exists? name)
    (add-user name))
  (first (jdbc/query mysql-db
                    ["select * from user where name = ?" name]
                    :row-fn :id)))

(defn game-exists? [player-one-id game-name]
  (< 0 (count
        (jdbc/query mysql-db
                 ["select * from 3x3_game where player_one_id = ? and name = ?"
                  player-one-id game-name]
                 :row-fn :id))))

(defn add-game [user-id game-name]
  (jdbc/insert! mysql-db :3x3_game
             {:name game-name :player_one_id user-id}))

(defn delete-game [user-id game-name]
  (jdbc/delete! mysql-db :3x3_game
             ["name = ? and player_one_id = ?" game-name, user-id]))

(defn retrieve-game-id
  "Returns a game's id. If game doesn't exist, creates game."
  [user-id game-name]
  (if-not (game-exists? user-id game-name)
    (add-game user-id game-name))
  (first (jdbc/query mysql-db
                  ["select * from 3x3_game where player_one_id = ? and name = ?"
                   user-id game-name]
                  :row-fn :id)))

(def all-spaces
  (str "space_zero, space_one, space_two, "
       "space_three, space_four, space_five, "
       "space_six, space_seven, space_eight"))

(def space-names
  (clojure.string/split all-spaces #", "))

(defn retrieve-game-board [game-id]
  (second (jdbc/query mysql-db
                   [(str "select " all-spaces
                         " from 3x3_game where id = ?") game-id]
                   :as-arrays? true)))

(defn retrieve-space-in-game [game-id space-num]
  (let [space-names (clojure.string/split all-spaces #", ")
        space (nth space-names space-num)]
    (first (jdbc/query mysql-db
                    [(str "select " space
                          " from 3x3_game where id = ?") game-id]
                    :row-fn (keyword space)))))

(defn set-space-in-game [game-id space-num token]
  (let [space (nth space-names space-num)]
    (jdbc/update! mysql-db :3x3_game
               {(keyword space) token} ["id = ?" game-id])))

(defn reset-game-board [game-id]
  (let [keys-to-nil-map (zipmap space-names (repeat nil))]
    (jdbc/update! mysql-db :3x3_game keys-to-nil-map ["id = ?" game-id])))

(defn set-game-board [game-id board]
  (let [keys-to-board-vals (zipmap space-names board)]
    (jdbc/update! mysql-db :3x3_game keys-to-board-vals ["id = ?" game-id])))
