(ns randr.core
	(:use seesaw.core)
	(:use seesaw.graphics)
	(:use seesaw.color)
	(:use [randr.spot :as spot]))
(import javax.imageio.ImageIO)
(import java.io.File)
(import java.awt.Image)
(import java.awt.image.BufferedImage)

(def my-canvas nil)
(def court-drawn false)
(def player-radius 20)
(def ball-radius 5)

(defn perimeter-spot-to-right [spot-name]
	(case spot-name
		:top :right-wing
		:right-wing :right-corner
		:right-corner :left-corner
		:left-corner :left-wing
		:left-wing :top))
(defn perimeter-spot-to-left [spot-name]
	(case spot-name
		:top :left-wing
		:left-wing :left-corner
		:left-corner :right-corner
		:right-corner :right-wing
		:right-wing :top))

(defrecord Ball [player-current player-last player-to state]) ; :dribbling, passing, holding
(defrecord Player [position destination userControlled location])

(def players (atom [	
				(Player. 1 :top 					true 	(spot/spot-to-coordinate :top)) 
				(Player. 2 :right-wing 		false (spot/spot-to-coordinate :right-wing))
				(Player. 3 :left-wing 		false (spot/spot-to-coordinate :left-wing))
				(Player. 4 :left-corner 	false (spot/spot-to-coordinate :left-corner))
				(Player. 5 :right-corner 	false (spot/spot-to-coordinate :right-corner))]))

(def game-ball (atom (Ball. 0 nil nil :dribbling)))

(defn ball-coords [ball]
	(update-in 
		(case (:state ball)
			:holding		(:location (nth @players (get-in ball [:player-current])))
			:dribbling 	(:location (nth @players (get-in ball [:player-current]))))
		[0]
		(fn [x] (+ (+ x player-radius) ball-radius))))

(defn render-ball [ball c g]
	(let [[x y] (ball-coords ball)]
		(draw g (circle x y ball-radius) (style :background :orange))))

(defn spot-to-circle [spot-name radius]
	(let [{x :x y :y} (spot-name spot/spots)]
		(circle x y radius)))

(defn basketball-court [] (ImageIO/read (File. "img/basketballcourt.jpg")))

(defn mouse-move-action [me]
	(let [x (.getX me) y (.getY me)]
		(swap! players update-in [0] merge {:location [x y]}))
	(repaint! my-canvas))

(defn basketball-court-content [c g]
	(.drawImage g (basketball-court) 100 0 nil)
	(.setBackground c (color "white"))
	(doseq [player @players
			:let [ {[x y] :location position :position} player] ]
		(draw g (circle x y player-radius) (style :foreground :red))
		(.drawString g (str position) (- x 2) (- y 5)))
	(render-ball @game-ball c g)
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