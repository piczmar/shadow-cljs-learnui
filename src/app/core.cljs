(ns app.core
  (:require
;    [decimal.core :as dc]
   [goog.math :as math]
   [goog.string :as gstring]
   [goog.string.format]
;    [decimals.core :as dc]
   ["decimal.js" :as dc]))

(defn ^:dev/after-load start []
  (js/console.log "Hello cljs"))

(defn ^:export init []
  (start))

(js/console.log "test")

(* 0.1 0.2)


(.log js/console (gstring/format "%.2f" 1.2345))

(math/clamp -1 0 5)

; (dc/* 0.1 0.1)
; (dc/pow 2 4)