(ns randr.core
	(:require [clojure.java.io :as io])
	(:use seesaw.core)
	(:use seesaw.graphics)
	(:use seesaw.color))
;(import javax.swing.JFrame)
;(import javax.swing.SwingUtilities)
(import javax.imageio.ImageIO)
(import java.io.File)
(import java.awt.Image)
(import java.awt.image.BufferedImage)

(def my-canvas nil)
(def mousex (atom 0))
(def mousey (atom 0))


(def spots ; x, y, perimeter
	{	:left-corner	{:x 50 :y 90, :perimeter true}
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

(defn spot-to-coordinate [name]
	(let [spot (name spots) x (:x spot) y (:y spot)]
		[x y]))

(defrecord Ball [location player-current player-last player-to])
(defrecord Player [position destination userControlled location])

(def players [	(Player. 1 :top true (spot-to-coordinate :top)) 
				(Player. 2 :right-wing false (spot-to-coordinate :right-wing))
				(Player. 3 :left-wing false (spot-to-coordinate :left-wing))
				(Player. 4 :left-corner false (spot-to-coordinate :left-corner))
				(Player. 5 :right-corner false (spot-to-coordinate :right-corner))])

(defn spot-to-circle [spot-name radius]
	(let [spot-hash (spot-name spots) x (:x spot-hash) y (:y spot-hash)]
		(circle x y radius)))

(defn basketball-court []
	(ImageIO/read (File. "img/basketballcourt.jpg")))

(defn mouse-move-action [me]
	(let [x (.getX me) y (.getY me)]
		(println (str "(" x ", " y ")"))
		(swap! mousex (fn [arg] x))
		(swap! mousey (fn [arg] y))
		)
	;(filter players #(:userControlled %1))
	(repaint! my-canvas))

(defn basketball-court-content [c g]
	(.drawImage g (basketball-court) 100 0 nil)
	(.setBackground c (color "white"))
	(doseq [player players
			:let [	location (:location player) 
					x (first location) 
					y (second location)]]
		(draw g (circle @mousex y (int 30)) (style :foreground :red))
		(.drawString g (str (:position player)) (- @mousex 2) (- y 5)))
	(listen c :mouse-motion mouse-move-action))

(defn -main [& args]
	(def my-canvas (canvas :paint basketball-court-content))
	(invoke-later
		(-> (frame :title "R&R"
				:content my-canvas
				:width 1000
				:height 600
				:on-close :exit)
			show!)))