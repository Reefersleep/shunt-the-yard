### Just for fun
This is an exercise I did in understanding the [shunting-yard algorithm](https://en.wikipedia.org/wiki/Shunting-yard_algorithm) by implementing it in Clojure.
I wouldn't use this implementation for serious calculations; it is _just_ an exercise, and it has very limited mathematical and numerical capabilities. 
Furthermore, the code is not especially functional; I more or less followed [the imperative pseudocode example from the wikpedia article](https://en.wikipedia.org/wiki/Shunting-yard_algorithm#The_algorithm_in_detail).

The tests in `shunt-the-yard.math-test` should show the current capabilities of the code.

### So what is the deal with the shunting-yard algorithm?

The [shunting-yard algorithm](https://en.wikipedia.org/wiki/Shunting-yard_algorithm) turns a mathematical [infix notation](https://en.wikipedia.org/wiki/Infix_notation) expression into a [Reverse Polish Notation](https://en.wikipedia.org/wiki/Reverse_Polish_notation) (or just "postfix notation") expression.

The intent is to make it easier to evaluate mathematical infix expressions where some parts take precedence, either by the nature of operator precedence, or via expressions nested in parentheses. 

This is done by converting a regular infix expression, such as 

```
5 + 4 * (6 / 3 + 2)
```

, to a "Reverse Polish expression" (or just "postfix expression"),

```
5 4 6 3 / 2 + * +
```
In the conversion, the prior has been changed to the latter in such a way that all operations are ordered both by operator precedence and parentheses precedence. Because the order is correct, parentheses are no longer needed and therefore do not appear as part of the output.

The shunting-yard algorithm is responsible for the infix->postfix conversion. Refer to the excellent explanation of the algoritm at the [wikipedia entry for the algorithm](https://en.wikipedia.org/wiki/Shunting-yard_algorithm).
 
Then, to calculate the full postfix expression, apply the first-coming operator (that is, `+`, `-`, `*` or `/`) to the last two numbers before it, and repeat until only a number is left, i.e.:
```
5 4 6 3 / 2 + * +

=> replace 6 3 / with the result of 6 / 3

5 4 2 2 + * +

=> replace 2 2 + with the result of 2 + 2

5 4 4 * +

=> replace 4 4 * with the result of 4 * 4

5 16 +

=> you know the drill :) 

21
```