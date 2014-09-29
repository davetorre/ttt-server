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

(defn retrieve-user-id [name]
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

(defn retrieve-game-id [user-id game-name]
  (first (j/query mysql-db
                  ["select * from 3x3_game where player_one_id = ? and name = ?"
                   user-id game-name]
                  :row-fn :id)))
