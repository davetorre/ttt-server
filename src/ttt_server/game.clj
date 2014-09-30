(ns ttt-server.game
  (:require [ttt-server.db :refer :all]))

(defn enclose-in-html [body]
  (str "<!DOCTYPE html><html><head></head><body>"
       body
       "</body></html"))

(def form-for-new-game
  "<form action=\"/game\" method=\"post\">
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

(defn form-for-new-move [game-id]
  (str "<form action=\"/game/move?game-id=" game-id "\" method=\"post\">
    <div>
      <input name=\"move\" id=\"move\"/>
      <label for=\"move\">Move</label>
    </div>
    <button type=\"submit\">Submit</button>
  </form>"))

(defn make-board [game-id]
  "The board representation.")

(defn make-game-page [game-id]
  (new davetorre.httpserver.HTTPResponse
       "HTTP/1.1 200 OK\n"
       (new java.util.HashMap)
       (.getBytes
        (enclose-in-html
         (str (make-board game-id) (form-for-new-move game-id))))))

(defn GET-slash [request] 
  (new davetorre.httpserver.HTTPResponse
       "HTTP/1.1 200 OK\n"
       (new java.util.HashMap)
       (.getBytes (enclose-in-html form-for-new-game))))

(defn get-values [some-string]
  (let [key-value-pairs (clojure.string/split some-string #"&")]
    (map #(second (clojure.string/split % #"=")) key-value-pairs)))

(defn body-as-string [request]
  (new String (.body request)))

(defn POST-move [request]
  (let [game-id (first (get-values (.parameters request)))
        move (first (get-values (body-as-string request)))]
    ;(make-move-in-game game-id move)
    (make-game-page game-id)))
  
(defn POST-game [request]
  (let [form-values (get-values (body-as-string request))
        user-name (first form-values)
        game-name (second form-values)]

    (if-not (user-exists? user-name) (add-user user-name))

    (let [user-id (retrieve-user-id user-name)]
      (add-game user-id game-name)
      (make-game-page (retrieve-game-id user-id game-name)))))
