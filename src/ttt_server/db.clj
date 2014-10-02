(ns ttt-server.db
  (:require [clojure.java.jdbc :as j]))

(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/ttt_server"
               :user "admin"
               :password "password"})

(defn user-exists? [name]
  (< 0 (count (j/query mysql-db
                      ["select * from user where name = ?" name]
                      :row-fn :name))))

(defn add-user [name]
  (j/insert! mysql-db :user
             {:name name}))

(defn delete-user [name]
  (j/delete! mysql-db :user ["name = ?" name]))

(defn retrieve-user-id
  "Returns a user's id. If user doesn't exist, creates user."
  [name]
  (if-not (user-exists? name)
    (add-user name))
  (first (j/query mysql-db
                    ["select * from user where name = ?" name]
                    :row-fn :id)))

(defn game-exists? [player-one-id game-name]
  (< 0 (count
        (j/query mysql-db
                 ["select * from 3x3_game where player_one_id = ? and name = ?"
                  player-one-id game-name]
                 :row-fn :id))))

(defn add-game [user-id game-name]
  (j/insert! mysql-db :3x3_game
             {:name game-name :player_one_id user-id}))

(defn delete-game [user-id game-name]
  (j/delete! mysql-db :3x3_game
             ["name = ? and player_one_id = ?" game-name, user-id]))

(defn retrieve-game-id
  "Returns a game's id. If game doesn't exist, creates game."
  [user-id game-name]
  (if-not (game-exists? user-id game-name)
    (add-game user-id game-name))
  (first (j/query mysql-db
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
  (second (j/query mysql-db
                   [(str "select " all-spaces
                         " from 3x3_game where id = ?") game-id]
                   :as-arrays? true)))

(defn retrieve-space-in-game [game-id space-num]
  (let [space-names (clojure.string/split all-spaces #", ")
        space (nth space-names space-num)]
    (first (j/query mysql-db
                    [(str "select " space
                          " from 3x3_game where id = ?") game-id]
                    :row-fn (keyword space)))))

(defn set-space-in-game [game-id space-num token]
  (let [space (nth space-names space-num)]
    (j/update! mysql-db :3x3_game
               {(keyword space) token} ["id = ?" game-id])))

(defn reset-game-board [game-id]
  (let [keys-to-nil-map (zipmap space-names (repeat nil))]
    (j/update! mysql-db :3x3_game keys-to-nil-map ["id = ?" game-id])))
