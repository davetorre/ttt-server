(ns ttt-server.html)

(defn enclose-in-html [body]
  (str "<!DOCTYPE html><html><head></head><body>"
       body
       "</body></html>"))

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

(defn make-row [row]
  (str "<tr>"
       (-> (map #(str "<td>" % "</td>") row)
           (clojure.string/join))
       "</tr>"))

(defn make-table [rows]
  (str "<table>"
       (-> (map #(make-row %) rows)
           (clojure.string/join))
       "</table>"))
