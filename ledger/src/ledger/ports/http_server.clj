(ns ledger.ports.http-server
  (:require [ledger.controllers.ledger :as c-ledger]
            [ledger.schemata.ledger :as s-ledger]
            [schema.core :as s]))

(defn get-transactions [{{:keys [employee-id]} :path-params} components]
  {:response/status 200
   :response/body
   {:ledger/employee-id employee-id
    :ledger/transactions (c-ledger/get-transactions employee-id components)}})

(def routes
  {:transactions/get
   {:route/path         "/api/transactions/:employee-id"
    :route/method       :get
    :route/handler      get-transactions
    :path-params/schema {:employee-id s/Uuid}
    :response/schema    s-ledger/GetTransactionsResponse}})
