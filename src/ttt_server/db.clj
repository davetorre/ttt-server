(ns ttt-server.db
  (:require [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]))

(def mysql-db (load-string (slurp (env :db-config-file))))

(defn table-exists? [table-name]
  (let [query-text (str "show tables like '" table-name "'")
        matching-tables (jdbc/query mysql-db [query-text])]
    (< 0 (count matching-tables))))

(defn user-table
  "Returns keyword :user for jdbc calls. Creates table if doesn't exist"
  []
  (if (not (table-exists? "user"))
    (jdbc/execute! mysql-db [(str "CREATE TABLE user ("
                                  "id int(11) NOT NULL AUTO_INCREMENT,"
                                  "name varchar(32),"
                                  "PRIMARY KEY(id));")]))
  :user)

(defn game-table
  "Returns keyword :3x3_game for jdbc calls. Creates table if doesn't exist"
  []
  (if (not (table-exists? "3x3_game"))
    (jdbc/execute! mysql-db [(str "CREATE TABLE 3x3_game ("
                                  "id int(11) NOT NULL AUTO_INCREMENT,"
                                  "name varchar(32),"
                                  "player_one_id int(11),"
                                  "player_two_id int(11),"
                                  "space_zero int(11),"
                                  "space_one int(11),"
                                  "space_two int(11),"
                                  "space_three int(11),"
                                  "space_four int(11),"
                                  "space_five int(11),"
                                  "space_six int(11),"
                                  "space_seven int(11),"
                                  "space_eight int(11),"
                                  "PRIMARY KEY(id));")]))
  :3x3_game)

(defn key-to-str [some-keyword]
  (name some-keyword))

(defn user-exists? [name]
  (< 0 (count (jdbc/query mysql-db
                          [(str "select * from " (key-to-str (user-table))
                                " where name = ?") name]
                          :row-fn :name))))

(defn add-user [name]
  (jdbc/insert! mysql-db (user-table) {:name name}))

(defn delete-user [name]
  (jdbc/delete! mysql-db (user-table) ["name = ?" name]))

(defn retrieve-user-id
  "Returns a user's id. If user doesn't exist, creates user."
  [name]
  (if-not (user-exists? name)
    (add-user name))
  (first (jdbc/query mysql-db
                     [(str "select * from " (key-to-str (user-table))
                           " where name = ?") name]
                     :row-fn :id)))

(defn retrieve-user-name [id]
  (first (jdbc/query mysql-db
                     [(str "select * from " (key-to-str (user-table))
                           " where id = ?") id]
                     :row-fn :name))
  )

(defn find-games-with [player-one-id game-name]
  (jdbc/query mysql-db
              [(str "select * from " (key-to-str (game-table))
                    " where player_one_id = ? and name = ?")
               player-one-id game-name]
              :row-fn :id))

(defn game-exists? [player-one-id game-name]
  (< 0 (count (find-games-with player-one-id game-name))))

(defn add-game [user-id game-name]
  (jdbc/insert! mysql-db (game-table)
             {:name game-name :player_one_id user-id}))

(defn delete-game [user-id game-name]
  (jdbc/delete! mysql-db (game-table)
             ["name = ? and player_one_id = ?" game-name, user-id]))

(defn retrieve-game-id
  "Returns a game's id. If game doesn't exist, creates game."
  [user-id game-name]
  (if-not (game-exists? user-id game-name)
    (add-game user-id game-name))
  (first (find-games-with user-id game-name)))

(def all-spaces
  (str "space_zero, space_one, space_two, "
       "space_three, space_four, space_five, "
       "space_six, space_seven, space_eight"))

(def space-names
  (clojure.string/split all-spaces #", "))

(defn retrieve-game-board [game-id]
  (second (jdbc/query mysql-db
                   [(str "select " all-spaces
                         " from " (key-to-str (game-table))
                         " where id = ?") game-id]
                   :as-arrays? true)))

(defn retrieve-space-in-game [game-id space-num]
  (let [space-names (clojure.string/split all-spaces #", ")
        space (nth space-names space-num)]
    (first (jdbc/query mysql-db
                       [(str "select " space
                             " from " (key-to-str (game-table))
                             " where id = ?") game-id]
                    :row-fn (keyword space)))))

(defn set-space-in-game [game-id space-num token]
  (let [space (nth space-names space-num)]
    (jdbc/update! mysql-db (game-table)
               {(keyword space) token} ["id = ?" game-id])))

(defn reset-game-board [game-id]
  (let [keys-to-nil-map (zipmap space-names (repeat nil))]
    (jdbc/update! mysql-db (game-table) keys-to-nil-map ["id = ?" game-id])))

(defn set-game-board [game-id board]
  (let [keys-to-board-vals (zipmap space-names board)]
    (jdbc/update! mysql-db (game-table) keys-to-board-vals ["id = ?" game-id])))
