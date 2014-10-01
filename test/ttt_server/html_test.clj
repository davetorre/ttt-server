(ns ttt-server.html-test
  (:require [ttt-server.html :refer :all]
            [clojure.test    :refer :all]))

(defn string-contains? [string substring]
  (not (= -1 (.indexOf string substring))))

(deftest html-test
  (testing "form-for-new-move adds game-id parameter to form action"
    (let [game-id 523
          form (form-for-new-move game-id)]
      (is (string-contains? form "action=\"/game/move?game-id=523\""))))

  (testing "Makes an html table row from a sequence"
    (let [words   '("how" "many")
          numbers '(0 1 2)]
    
      (is (= "<tr><td>how</td><td>many</td></tr>"
             (make-row words)))
      (is (= "<tr><td>0</td><td>1</td><td>2</td></tr>"
             (make-row numbers)))))

  (testing "Makes an html table from sequence of sequences"
    (let [rows     [["O" "1"] ["3" "X"]]
          expected (str "<table>"
                        "<tr><td>O</td><td>1</td></tr>"
                        "<tr><td>3</td><td>X</td></tr>"
                        "</table>")]
      (is (= expected (make-table rows)))))

  )

