(ns shunt-the-yard.math)

(def number-token?
  (->> (range 10)
       (map str)
       (map first)
       set))

(def operator->precedence
  {\* 3
   \/ 3
   \+ 2
   \- 2})

(def operator-token?
  #{\+ \- \* \/})

(defn handle-token [{:keys [token
                            output-queue
                            operator-stack]}]
  (loop [q output-queue
         o operator-stack]
    (let [top (peek o)]
      (if (and top
               (not= \( top)
               (> (get operator->precedence top)
                  (get operator->precedence token)))
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
      (let [t (first tokens)]
        (cond (number-token? t)
              (recur (rest tokens)
                     (conj output-queue t)
                     operator-stack)

              (operator-token? t)
              (let [[queue stack] (handle-token {:token          t
                                                 :output-queue   output-queue
                                                 :operator-stack operator-stack})]
                (recur (rest tokens)
                       queue
                       stack))

              (= \( t)
              (recur (rest tokens)
                     output-queue
                     (conj operator-stack t))

              (= \) t)
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
      (let [op (first (drop-while integer? i))
            nums ((juxt (comp last butlast) last) (take-while integer? i))
            result (apply op nums)]
        (recur (concat (drop-last 2 (take-while integer? i))
                       [result]
                       (drop 1 (drop-while integer? i))))))))

(defn calculate [expression]
  (->> expression
       tokenize
       infix->reverse-polish
       parse
       calculate-reverse-polish))