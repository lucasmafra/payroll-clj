(ns settlement.ports.http-server
  (:require [schema.core :as s]
            [settlement.controllers.settlement :as c.settlement]))

(defn batch-settlement [request components]
  {:status 200
   :body   (c.settlement/batch-settle! components)})

(def routes
  {:batch-settlement
   {:path               "/settlement/batch"
    :method             :post
    :handler            batch-settlement
    :response-schema    s/Any}})
