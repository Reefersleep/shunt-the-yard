(ns shunt-the-yard.math-test
  (:require [clojure.test :refer :all]
            [shunt-the-yard.math :as math]))

;;Converting infix to reverse polish

(deftest initial-test
  (is (= '(\3 \4 \+) (math/infix->reverse-polish '(\3 \+ \4)))))

(deftest parens-test
  (is (= '(\3 \4 \2 \1 \- \* \+ ) (math/infix->reverse-polish '(\3 \+ \4 \* \( \2 \- \1 \))))))

(deftest another-test
  (is (= '(\5 \4 \2 \6 \3 \/ \+ \* \+) (math/infix->reverse-polish '(\5 \+ \4 \* \( \2 \+ \6 \/ \3 \))))))

;;Full on calculation

(deftest calculate-test
  (is (= 7 (math/calculate "3 + 4 * (2 - 1)"))))

(deftest a-second-test
  (is (= 3 (math/calculate "3 + 4 * (2 - 4 / 2)"))))

(deftest a-third-test
  (is (= 3 (math/calculate "3 + 4 * (2 - 4 / 2 * (6 - 5))"))))

(deftest a-fourth-test
  (is (= 21 (math/calculate "5 + 4 * (6 / 3 + 2)"))))

;; Can't handle numbers that are more than one integer yet because the parser is too simple for that
;; Can't handle other whitespace than exactly 0 to one space between each meaningful token because the parser is too simpmle for that
;; Can't handle negative number literals (like -5) because... you guessed it :)