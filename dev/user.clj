(ns user
 (:require [portal.api :as p]
           [com.brunobonacci.mulog :as μ]))

(def p (p/open {:launcher :vs-code})) 

(add-tap #'p/submit)

(μ/start-publisher! {:type :console
                     :pretty? true})