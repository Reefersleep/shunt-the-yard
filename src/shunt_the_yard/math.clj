(ns shunt-the-yard.math)

(def number-token?
  (->> (range 10)
       (map str)
       (map first)
       set))

(def precedence
  {\* 3
   \/ 3
   \+ 2
   \- 2})

(def operator-token?
  #{\+ \- \* \/})

(defn handle-token-functionally
  "Not quite there yet."
  [{:keys [token
           output-queue
           operator-stack]}]
  (let [[departing remaining] (->> operator-stack
                                   (split-with #(and (not= \( %)
                                                     (> (precedence %)
                                                        (precedence token)))))]
    [(into (clojure.lang.PersistentQueue/EMPTY) (concat departing output-queue))
     (conj remaining token)]))

(defn handle-token [{:keys [token
                            output-queue
                            operator-stack]}]
  (loop [q output-queue
         o operator-stack]
    (let [top (peek o)]
      (if (and top
               (not= \( top)
               (> (precedence top)
                  (precedence token)))
        (recur (conj q top)
               (pop o))
        [q (conj o token)]))))

(defn flush-stack [output-queue
                   operator-stack]
  (loop [o output-queue
         s operator-stack]
    (if-not (peek s)
      o
      (recur (conj o (peek s))
             (pop s)))))

(defn handle-right-parentheses [output-queue
                                operator-stack]
  (loop [q output-queue
         o operator-stack]
    (if-not (seq o)
      (throw (ex-info "Mismatched parentheses!" {}))
      (let [top (peek o)]
        (if (= \( top)
          [q (pop o)]
          (recur (conj q (peek o))
                 (pop o)))))))

(defn infix->reverse-polish [expression]
  (loop [tokens expression
         output-queue (clojure.lang.PersistentQueue/EMPTY)
         operator-stack '()]
    (if-not (seq tokens)
      (flush-stack output-queue operator-stack)
      (let [token (first tokens)]
        (cond
          (number-token? token)
          (recur (rest tokens)
                 (conj output-queue token)
                 operator-stack)

          (operator-token? token)
          (let [[queue stack] (handle-token {:token          token
                                             :output-queue   output-queue
                                             :operator-stack operator-stack})]
            (recur (rest tokens)
                   queue
                   stack))

          (= \( token)
          (recur (rest tokens)
                 output-queue
                 (conj operator-stack token))

          (= \) token)
          (let [[queue stack] (handle-right-parentheses output-queue
                                                        operator-stack)]
            (recur (rest tokens)
                   queue
                   stack)))))))

(defn tokenize [expression]
  (->> expression
       seq
       (remove #(= % \space))))

(defn parse [chars]
  (->> chars
       (map (fn [c]
              (cond (number-token? c)
                    (->> c
                         str
                         (Integer/parseInt))
                    (operator-token? c)
                    (-> c
                        str
                        symbol
                        resolve))))))

(defn calculate-reverse-polish [coll]
  (loop [i coll]
    (if (= 1 (count i))
      (first i)
      (let [[neck tail] (split-with integer? i)
            op (first tail)
            nums (take-last 2 neck)
            result (int (apply op nums))]
        (recur (concat (drop-last 2 neck)
                       [result]
                       (drop 1 tail)))))))

(defn calculate [expression]
  (->> expression
       tokenize
       infix->reverse-polish
       parse
       calculate-reverse-polish))