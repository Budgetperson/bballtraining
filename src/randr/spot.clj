(ns randr.spot
  (:require [clojure.math.numeric-tower :as math]))

(def spot-radius 10)

(def spots ; x, y, perimeter
  { :left-corner  {:x 50 :y 90, :perimeter true}
    :right-corner   {:x 950 :y 50 :perimeter true}
    :top            {:x 500 :y 560 :perimeter true}
    :right-wing     {:x 900 :y 390 :perimeter true}
    :left-wing      {:x 100 :y 390 :perimeter true}
    :high-post-left {:x 380 :y 385 :perimeter false}
    :high-post-right {:x 620 :y 385 :perimeter false}
    :mid-post-right {:x 650 :y 230 :perimeter false}
    :mid-post-left {:x 350 :y 230 :perimeter false}
    :low-post-left  {:x 350 :y 140 :perimeter false}
    :low-post-right {:x 650 :y 140 :perimeter false}
    :short-corner-left {:x 270 :y 40 :perimeter false}
    :short-corner-right {:x 730 :y 40 :perimeter false}})

(defn spot-to-coordinate [name] (let [{x :x y :y} (name spots)] [x y]))

(defn on-spot [[center-x center-y] [test-x test-y]]
  (<=
    (+ (math/expt (- test-x center-x) 2) (math/expt (- test-y center-y) 2))
    (math/expt spot-radius 2)))

(defn which-spot [coordinate]
  (first (first (filter (fn [[_ {x :x y :y}]] (on-spot [x y] coordinate)) (seq spots)))))
