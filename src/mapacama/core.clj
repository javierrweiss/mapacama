(ns mapacama.core
  (:require [org.httpkit.server :as server]
            [com.brunobonacci.mulog :as µ]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [muuntaja.core :as m]
            [reitit.ring.coercion :as coercion]
            [mapacama.handlers.camas :as camas])
  (:import (java.time LocalDateTime ZoneId))
  (:gen-class))

(def router (ring/router [["/" {:get #'camas/poll-db}]]
                         {:data {:muuntaja m/instance
                                 :middleware [muuntaja/format-middleware
                                              coercion/coerce-exceptions-middleware
                                              coercion/coerce-request-middleware
                                              coercion/coerce-response-middleware]}}))

(def app (ring/ring-handler router (ring/create-default-handler)))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (µ/log ::deteniendo-servicio :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  (µ/log ::iniciando-servicio :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
  (reset! server (server/run-server #'app {:port 8000})))


(comment
  
  (-main)
  
  @server

  (stop-server)
  :rcf)