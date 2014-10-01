(ns ttt-server.html-test
  (:require [ttt-server.html :refer :all]
            [clojure.test    :refer :all]))

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

