(ns mapacama.core
  (:require [org.httpkit.server :as server]
            [com.brunobonacci.mulog :as µ]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [muuntaja.core :as m]
            [reitit.ring.coercion :as coercion]
            [mapacama.handlers.camas :as camas] 
            [ring.adapter.jetty :refer [run-jetty]])
  (:import (java.time LocalDateTime ZoneId))
  (:gen-class))

(def router (ring/router [["/eventos" {:get #_#'camas/poll-db #'camas/poll-db-ring}]]
                         {:data {:muuntaja m/instance
                                 :middleware [muuntaja/format-middleware
                                              coercion/coerce-exceptions-middleware
                                              coercion/coerce-request-middleware
                                              coercion/coerce-response-middleware]}}))

(def app (ring/ring-handler
          router
          (ring/create-resource-handler {:path "/"})
          (ring/create-default-handler)))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (if (fn? @server)
      (do
        (µ/log ::deteniendo-servicio-httpkit :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
        (@server :timeout 100))
      (do
        (µ/log ::deteniendo-servicio-jetty :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
        (.stop @server)))
    (reset! server nil)))

(defn run-server 
  ([server-start-fn handler]
   (run-server server-start-fn handler nil))
  ([server-start-fn handler opts] 
   (reset! server (server-start-fn handler (merge {:port 8000} opts)))))  

(defn -main [& args]
  (µ/log ::iniciando-servicio :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
  #_(run-server server/run-server #'app)
  (run-server run-jetty #'app))


(comment
  
  (-main)
  
  @server

  (stop-server)

  (def jetty (run-jetty #'app {:port 4000 :join? false}))

  (type jetty)

  (def kit (server/run-server #'app {:port 4500}))

  (type kit)

  (fn? kit)

  (.stop jetty)
  
  (kit)
  :rcf)