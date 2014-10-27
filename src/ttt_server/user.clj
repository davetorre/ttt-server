(ns ttt-server.user
  (:require [ttt-server.html :refer :all]
            [ttt-server.http :as http]
            [ttt-server.db   :as db]))

(defn GET-user [request]
  (let [user-id (first (http/get-values (http/body-as-string request)))
        user-name (db/retrieve-user-name user-id)]
    (http/response-with-body user-name)))
