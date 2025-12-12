(ns mapacama.handlers.camas
  (:require [dev.onionpancakes.chassis.core :as html]
            [com.brunobonacci.mulog :as µ]
            [mapacama.componentes :as componentes]
            [mapacama.repositorio.sql :as sql]
            [overtone.at-at :as at]
            [org.httpkit.server :as server]
            [org.httpkit.timer :as timer]
            [clojure.core.async :as a]
            [ring.core.protocols :refer [StreamableResponseBody]]
            [clojure.java.io :as io])
  (:import (java.time LocalDateTime ZoneId)
           java.io.OutputStream))

(defonce pool (at/mk-pool))

(defn obtener-tabla-de-camas 
  []
  (-> (sql/cargar-ocupacion-camas)
       componentes/crear-tabla
      html/html))

(defn poll-db [request]
  (µ/log ::request-recibido :request request :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
  (server/as-channel request {:on-open (fn [ch]
                                         (µ/log ::stream-abierto :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
                                         (at/every
                                          5000
                                          (fn []
                                            (server/send! ch "data: Chao!! \n\n" #_(obtener-tabla-de-camas) false)
                                            #_{:headers {"Content-Type" "text/event-stream"
                                                       "Cache-Control" "no-cache, no-store"}
                                             :status 200
                                             :body ch})
                                          pool))}))

(defn poll-db-ring
  [request]
  (µ/log ::request-recibido-handler-ring :request request :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
  {:status 200
   :headers {"Content-Type" "text/event-stream"
             "Cache-Control" "no-cache, no-store"}
   :body (let [output (a/chan)]
           (a/go-loop []
             (a/<! (a/timeout 5000)) 
             (a/>! output (str "data: " (obtener-tabla-de-camas) "\n\n"))
             (recur))
           output)})

(extend-type clojure.core.async.impl.channels.ManyToManyChannel
  StreamableResponseBody
  (write-body-to-stream [ch _response ^OutputStream output-stream]
    (with-open [out    output-stream
                writer (io/writer out)]
      (try
        (loop []
          (when-let [^String msg (a/<!! ch)]
            (doto writer (.write msg) (.flush))
            (recur)))
        ;; If the client disconnects writing to the output stream
        ;; throws an IOException.
        (catch java.io.IOException _)
        ;; Close channel after client disconnect.
        (finally (a/close! ch))))))

(comment
  
  (tap> (obtener-tabla-de-camas))  

  (timer/schedule-task 100 (println "Hola"))
  
  (at/stop-and-reset-pool! pool)

  
  
  :rcf)




