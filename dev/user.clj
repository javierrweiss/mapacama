(ns user
  (:require [portal.api :as p]
            [com.brunobonacci.mulog :as μ]
            [mapacama.core :as core]
            [nextjournal.beholder :as beholder]))

(def p (p/open {:launcher :vs-code})) 

(add-tap #'p/submit)

(μ/start-publisher! {:type :console
                     :pretty? true})

(defn restart-server
  [{:keys [type path]}]
  #_(µ/log ::reiniciando-servicio :evento {:tipo type
                                         :path path})
  (println type path)
  (core/stop-server)
  (Thread/sleep 200)
  (core/-main))

(def watcher (beholder/watch restart-server "src"))
 
(comment
  
  (beholder/stop watcher)

  (restart-server {})
  :rcf)