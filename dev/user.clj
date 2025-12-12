(ns user
  (:require [portal.api :as p] 
            [mapacama.core :as core]
            [nextjournal.beholder :as beholder]
            [com.brunobonacci.mulog :as u]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :refer [run-jetty]]
            [org.httpkit.server :refer [run-server]]))

(def p (p/open {:launcher :vs-code})) 

(add-tap #'p/submit)

(u/start-publisher! {:type :console
                     :pretty? true})

(defn get-dev-handler
  []
  (wrap-reload #'core/app))

(defn start [server]
  (if (= server :kit)
    (core/run-server run-server #'core/app)
    (core/run-server run-jetty (get-dev-handler) {:port 9000 :join? false :async? true})))

(defn restart-kit-server
  [{:keys [type path]}]
  (u/log ::reiniciando-servicio :evento {:tipo type
                                         :path path}) 
  (core/stop-server)
  (Thread/sleep 200)
  (core/run-server run-server #'core/app))

(defn watcher [] 
  (beholder/watch restart-kit-server "src"))

(def start-jetty (partial start :jetty))

(def start-kit (partial start :kit))

(comment
  
  (start-jetty)

  (start-kit)

  (def watch (watcher))

  (beholder/stop watch)

  (restart-kit-server {})

  (clojure.repl.deps/sync-deps)
  :rcf)