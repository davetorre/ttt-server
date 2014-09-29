(ns ttt-server.game
  (:require [ttt-server.db :refer :all]))

(defn enclose-in-html [body]
  (str "<!DOCTYPE html><html><head></head><body>"
       body
       "</body></html"))

(def new-game-form
  "<form action=\"/new_game\" method=\"post\">
    <div>
      <input name=\"user\" id=\"user\"/>
      <label for=\"user\">User</label>
    </div>
    <div>
      <input name=\"game\" id=\"game\"/>
      <label for=\"game\">Game</label>
    </div>
    <button type=\"submit\">Submit</button>
  </form>")

(defn get-new-game [request] 
  (new davetorre.httpserver.HTTPResponse
       "HTTP/1.1 200 OK\n"
       (new java.util.HashMap)
       (.getBytes (enclose-in-html new-game-form))))

(defn get-form-values [request]
  (let [body (new String (.body request))
        key-value-pairs (clojure.string/split body #"&")]
    (map #(second (clojure.string/split % #"=")) key-value-pairs)))

(defn post-new-game [request]
  (let [form-values (get-form-values request)
        user (first form-values)
        game (second form-values)]
    (if-not (user-exists? user) (add-user user))
    (add-game (retrieve-user-id user) game))
  
  (new davetorre.httpserver.HTTPResponse
       "HTTP/1.1 200 OK\n"
       (new java.util.HashMap)
       (.getBytes "Here's the game board.")))
