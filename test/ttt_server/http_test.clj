(ns ttt-server.http-test
  (:require [ttt-server.http :refer :all]
            [clojure.test    :refer :all]
            ))

  (deftest http-test
    (testing "get-values gets values from a string"
      (is (= ["Val" "Mom" "OK"]
             (get-values "Key=Val&Hi=Mom&Yes=OK"))) 
      (is (= ["A User" "A Game"]
             (get-values "user=A User&game=A Game"))))

    )

