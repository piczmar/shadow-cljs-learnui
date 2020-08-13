(ns decimals.core
  (:require
   ["decimal.js" :as dc])
  (:refer-clojure :exclude [> >= < <= neg? pos? integer? zero? = / - + * max min mod]))

(def ^:static +decimal+ (dc/Decimal.noConflict))
(def ^:dynamic *decimal* +decimal+)

(def round-mapping
  {:round-up 0
   :round-down 1
   :round-ceil 2
   :round-floor 3
   :round-half-up 4
   :round-half-down 5
   :round-half-even 6
   :round-half-ceil 7
   :round-half-floor 8
   :euclid 9})

(def modulo-mapping
  {:round-up 0
   :round-down 1
   :round-floor 3
   :round-half-even 6
   :euclid 9})

(defn config!
  "Set the global configuration for the decimal constructor.
  Possible options:
  - `precision`: The maximum number of significant digits of
    the result of an operation (integer 1 to 1e+9 inclusive,
    default: 20).
  - `rounding`: The default rounding mode used when rounding
    the result of an operation (integer 0 to 8 inclusive,
    default: :round-half-up).
  - `min-e`: The negative exponent limit, i.e. the exponent value below
    which underflow to zero occurs (integer, -9e15 to 0 inclusive, default:
    -9e15).
  - `max-e`: The positive exponent limit, i.e. the exponent value above
    which overflow to Infinity occurs (integer, 0 to 9e15 inclusive, default:
    9e15).
  - `to-exp-neg`: The negative exponent value at and below which `toString`
    returns exponential notation. (integer, -9e15 to 0 inclusive, default: -7)
  - `to-exp-pos`: The positive exponent value at and above which `toString`
    returns exponential notation. (integer, 0 to 9e15 inclusive, default: 20)
  - `modulo`: The modulo mode used when calculating the modulus: `a mod n`.
    (integer, 0 to 9 inclusive, default: :round-down)
  - `crypto`: The value that determines whether cryptographically-secure
    pseudo-random number generation is used. (boolean, default: false)
  **Rounding modes**
  Rounding modes are:
  Keyword           |  Description
  ------------------|-------------
  :round-up         |  Rounds away from zero
  :round-down       |  Rounds towards zero
  :round-ceil       |  Rounds towards Infinity
  :round-floor      |  Rounds towards -Infinity
  :round-half-up    |  Rounds towards nearest neighbour. If equidistant, rounds away from zero
  :round-half-down  |  Rounds towards nearest neighbour. If equidistant, rounds towards zero
  :round-half-even  |  Rounds towards nearest neighbour. If equidistant, rounds towards even neighbour
  :round-half-ceil  |  Rounds towards nearest neighbour. If equidistant, rounds towards Infinity
  :round-half-floor |  Rounds towards nearest neighbour. If equidistant, rounds towards -Infinity
  :euclid           |  Not a rounding mode, see modulo
  **Modulo modes**
  The modes that are most commonly used for the modulus/remainder operation
  are shown in the following table. Although the other rounding modes can be used,
  they may not give useful results.
  Keyword           | Description
  ------------------|------------
  :round-up         | The remainder is positive if the dividend is negative, else is negative
  :round-down       | The remainder has the same sign as the dividend. This uses truncating division and matches the behaviour of JavaScript's remainder operator %.
  :round-floor      | The remainder has the same sign as the divisor. (This matches Python's % operator)
  :round-half-even  | The IEEE 754 remainder function
  :euclid           | The remainder is always positive.
  **Other options**
  The underlying library supports more options that and this
  function also accepts. You can read more about here:
  http://mikemcl.github.io/decimal.js/#Dconfig"
  [options]
  (let [opts #js {:precision (:precision options (.-precision +decimal+))
                  :rounding ((:rounding options) round-mapping (.-rounding +decimal+))
                  :modulo ((:modulo options :round-down) modulo-mapping (.-modulo +decimal+))
                  :minE (:min-e options (.-minE +decimal+))
                  :maxE (:max-e options (.-maxE +decimal+))
                  :toExpNeg (:to-exp-neg options (.-toExpNeg +decimal+))
                  :toExpPos (:to-exp-pos options (.-toExpPos +decimal+))
                  :crypto (:crypto options (.-crypto +decimal+))}]
    (.set *decimal* opts)
    nil))

(defn config
  "The same as `config` but returns an constructor
  of decimals that can be used for create new instances
  with provided configuration."
  [options]
  (let [opts #js {:precision (:precision options (.-precision +decimal+))
                  :rounding ((:rounding options) round-mapping (.-rounding +decimal+))
                  :modulo ((:modulo options :round-down) modulo-mapping (.-modulo +decimal+))
                  :minE (:min-e options (.-minE +decimal+))
                  :maxE (:max-e options (.-maxE +decimal+))
                  :toExpNeg (:to-exp-neg options (.-toExpNeg +decimal+))
                  :toExpPos (:to-exp-pos options (.-toExpPos +decimal+))
                  :crypto (:crypto options (.-crypto +decimal+))}]
    (.clone +decimal+ opts)))

(defprotocol IDecimal
  (-decimal [v] "return a decimal instance."))

(defn decimal
  "Create a new Decimal instance from `v` value."
  [v]
  (-decimal v))

(defn ^boolean decimal?
  "Return true if `v` is a instance of Decimal."
  [v]
  (instance? *decimal* v))

(defn pow
  "Returns a new Decimal whose value is the value of this Decimal raised to the
  power x, rounded to precision significant digits using rounding mode
  rounding.
  The performance of this method degrades exponentially with increasing digits.
  For non-integer exponents in particular, the performance of this method may
  not be adequate."
  [v x]
  (.toPower (-decimal v) x)) 