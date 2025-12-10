(ns mapacama.system
  (:require [lambdaisland.config :as config]))

(def conf (config/create {:prefix "mapacama"}))

(defn get-maestros-ds 
  []
  (-> (config/get conf :db)
      :maestros))   