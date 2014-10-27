(ns ttt-server.http)

(defn response-with-body [body]
  (new httpserver.HTTPResponse
       "HTTP/1.1 200 OK"
       (new java.util.HashMap)
       (.getBytes body)))

(defn body-as-string [request]
  (new String (.body request)))

(defn get-values [some-string]
  (let [key-value-pairs (clojure.string/split some-string #"&")]
    (map #(second (clojure.string/split % #"=")) key-value-pairs)))

