(ns settlement.ports.http-server
  (:require [schema.core :as s]
            [settlement.controllers.settlement :as c.settlement]
            [settlement.schemata.settlement :as s-settlement]))

(defn batch-settle [{{:keys [batch-settlement/as-of]} :body} components]
  {:status 201
   :body   (c.settlement/create-batch-settlement! as-of components)})

(def routes
  {:batch-settle
   {:path               "/settlement/batch"
    :method             :post
    :handler            batch-settle
    :request-schema     s-settlement/BatchSettleRequest
    :response-schema    s-settlement/BatchSettleResponse}})
