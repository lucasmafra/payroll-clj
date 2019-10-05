(ns ledger.ports.http-server
  (:require [common-clj.components.http-server.http-server :as hs]
            [ledger.controllers.ledger :as c-ledger]
            [ledger.schemata.ledger :as s-ledger]
            [schema.core :as s]))

(defn get-transactions [{{:keys [employee-id]} :path-params} components]
  (hs/ok
   {:ledger/employee-id  employee-id
    :ledger/transactions (c-ledger/get-transactions employee-id components)}))

(def routes
  {:get-transactions
   {:path               "/api/transactions/:employee-id"
    :method             :get
    :handler            get-transactions
    :path-params-schema {:employee-id s/Uuid}
    :response-schema    s-ledger/GetTransactionsResponse}})
