(ns mapacama.pools.maestros
  (:require [mapacama.system :as system]
            [next.jdbc :as jdbc]
            [com.potetm.fusebox.timeout :as to]
            [com.brunobonacci.mulog :as µ])
  (:import org.apache.commons.pool2.impl.DefaultPooledObject
           (java.time LocalDateTime ZoneId))
  (:gen-class 
   :name mapacama.pools.maestros.MaestrosPool
   :extends org.apache.commons.pool2.BasePooledObjectFactory))

(def timeout (to/init {::to/timeout-ms 2000}))

(defn -create
  [this]
  (µ/log ::creando-conexion-maestros :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
  (to/with-timeout timeout (jdbc/get-connection (system/get-maestros-ds))))
 
(defn -wrap
  [this conn]
  (DefaultPooledObject. conn))

(defn -passivateObject
  [this obj]
  (let [conn (.getObject obj)]
    (when (.getAutoCommit conn)
      (.rollback conn))))

(defn -destroyObject
  [this obj]
  (µ/log ::destruyendo-conexion-maestros :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires")))
  (.close (.getObject obj)))


(comment
  
  (with-open [c (jdbc/get-connection (system/get-maestros-ds))]
    (jdbc/execute! c ["SELECT COUNT(*) FROM tbc_camas"]))
  
(compile 'mapacama.pools.maestros)

  :rcf)