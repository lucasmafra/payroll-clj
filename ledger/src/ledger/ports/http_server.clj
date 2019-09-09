(ns ledger.ports.http-server
  (:require [ledger.controllers.ledger :as controllers.ledger]
            [ledger.schemata.ledger :as schemata.ledger]
            [schema.core :as s]))

(defn get-ledger [{{:keys [employee-id]} :path-params} components]
  {:status 200
   :body   {:employee-id employee-id
            :entries     (controllers.ledger/get-ledger employee-id components)}})

(def routes
  {:get-ledger
   {:path               "/ledger/:employee-id"
    :method             :get
    :handler            get-ledger
    :path-params-schema {:employee-id s/Uuid}
    :response-schema    schemata.ledger/GetLedgerResponse}})
