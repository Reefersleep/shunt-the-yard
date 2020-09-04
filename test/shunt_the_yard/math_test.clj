(ns shunt-the-yard.math-test
  (:require [clojure.test :refer :all]
            [shunt-the-yard.math :as math]))

;;Converting infix to reverse polish

(deftest initial-test
  (is (= '(\3 \4 \+) (math/infix->reverse-polish '(\3 \+ \4)))))

(deftest parens-test
  (is (= '(\3 \4 \2 \1 \- \* \+ ) (math/infix->reverse-polish '(\3 \+ \4 \* \( \2 \- \1 \))))))

;;Full on calculation

(deftest calculate-test
  (is (= 7 (math/calculate "3 + 4 * (2 - 1)"))))