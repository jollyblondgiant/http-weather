(ns http-weather.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def alerts
  #_"from https://openweathermap.org/weather-conditions
     maps potentially hazardus weather status codes to their condition"
  {711 "Smoke"
   721 "Haze"
   731 "Dust"
   741 "Fog"
   751 "Sand"
   761 "Dust"
   762 "Ash"
   771 "Squall"
   781 "Tornado"})

(defn parse-weather
  "destructures the result body from get-weather for pertinent data
  and formats them to return weather conditions"
  [{temp :temp
    [{status :id
      weather :description}] :weather}]
  #_"^^ this receives and parses the result body for data"
  (let [resp
        (->
         #_" the thread-first macro evaluates the first expression and
            inserts it as the first parameter of the next expression,
            until eventually the parsed map is returned as resp"
         :weather
         (hash-map weather)
         ;;associates the keyword :weather to the :weather >> :description value
         (assoc :temperature
                (cond ;; like an extended if-else, parsed in order.
                  (> temp 293.15) "Hot"
                  (< temp 273.15) "Wicked Cold"
                  (< temp 283.15) "Chilly"
                  :else "Nice")))] ;;my particular taste in room temps
    (if-let [alerts (get alerts status)]
      #_"this form binds the value of the condition description from alerts
         as long as the value of :weather>>:id exists in that map.
         if it is unable to find the key it returns the resp unassoc'd"
      (assoc resp :alert alert)
      resp)))

(defn get-weather
  "takes an api key, latitude, and longitude, queries the
  openWeather api, unwraps the body of the result, and parses that result
  through parse-weather"
  [api-key lat lng]
  (->
   #_"the thread-first macro takes the result of the first expr (a string)
     and uses it as the first param in the next expr, so on until
     parse-weather is called on the body"
   "https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&appid=%s"
   (format  lat lng api-key) ;;string interpolation on %s
   client/get ;;makes the api call
   :body ;; digs out resp body from headers, etc
   (json/read-str :key-fn keyword) ;; convert json to hash-map
   :current ;; digs out most pertinent data for parsing
   ;; this could also be parsed in the destructuring of parse-weather.
   parse-weather ;; return weather data as concise hash-map
   ))


(defroutes app-routes
  #_"exposes the end-points of our web server"
  (GET "/" [] (str "please navigate to: "
                   "localhost:3000/{your-api-key}/{latitude}/{longitude}"))
  (GET "/:key/:lat/:lng" [api-key lat lng] (get-weather api-key lat lng))
  (route/not-found "Not Found"))

(def app
  #_"initializes the app"
  (wrap-defaults app-routes site-defaults))
