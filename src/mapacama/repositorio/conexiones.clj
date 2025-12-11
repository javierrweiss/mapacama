(ns mapacama.repositorio.conexiones
  (:require [next.jdbc :as jdbc]
            [mapacama.pools.maestros]
            [com.potetm.fusebox.timeout :as to]
            [com.brunobonacci.mulog :as µ])
  (:import (java.time LocalDateTime ZoneId)
           org.apache.commons.pool2.impl.GenericObjectPool
           mapacama.pools.maestros.MaestrosPool))

(def timeout (to/init {::to/timeout-ms 2000}))
 
(defonce maestros-pool (GenericObjectPool. (MaestrosPool.)))

(defn ejecutar!
  [pool sentencia]
  (let [conn (.borrowObject pool)]
    (try
      (to/with-timeout timeout
        (jdbc/execute! conn sentencia))
      (finally (.returnObject pool conn)))))

(defn con-maestros!
  [sentencia]
  (try
    (ejecutar! maestros-pool sentencia)
    (catch Exception e (let [msj (ex-message e)] 
                         (µ/log ::error-al-ejecutar-con-maestros :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")) :mensaje msj)
                         (throw (ex-info "Hubo un error al ejecutar la sentencia en maestros" {:sentencia sentencia
                                                                                               :mensaje msj}))))))

(comment 
  
  (tap> (con-maestros! ["SELECT * FROM tbc_camas"]))

  (def camas (con-maestros! ["SELECT * FROM tbc_camas"]))

  :rcf)
