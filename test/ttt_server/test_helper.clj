(ns ttt-server.test-helper)

(defn string-contains? [string substring]
  (not (= -1 (.indexOf string substring))))
