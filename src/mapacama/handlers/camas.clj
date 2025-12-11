(ns mapacama.handlers.camas
  (:require [dev.onionpancakes.chassis.core :as html]
            [com.brunobonacci.mulog :as µ]
            [mapacama.componentes :as componentes]
            [mapacama.repositorio.sql :as sql]
            [overtone.at-at :as at]
            [org.httpkit.server :as server]
            [org.httpkit.timer :as timer])
  (:import (java.time LocalDateTime ZoneId)))

(defonce pool (at/mk-pool))

(defn obtener-tabla-de-camas 
  []
  (-> (sql/cargar-ocupacion-camas)
       componentes/crear-tabla
      html/html))

(defn poll-db [request] 
  (server/as-channel request {:on-open (fn [ch]
                                         (µ/log ::stream-abierto :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
                                         (at/every
                                          5000
                                          (fn []
                                            (µ/log ::recuperando-datos)
                                            (server/send! ch (obtener-tabla-de-camas) false)
                                            {:headers {"Content-Type" "text/event-stream"
                                                       "Cache-Control" "no-cache, no-store"}
                                             :status 200
                                             :body ch})
                                          pool))}))


(comment
  
  (tap> (obtener-tabla-de-camas))  

(timer/schedule-task 100 (println "Hola"))
  
  (at/stop-and-reset-pool! pool)
  
  :rcf)




