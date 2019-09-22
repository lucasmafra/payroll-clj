(ns settlement.ports.http-client
  (:require [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement]
            [settlement.ports.db :as db]
            [common-clj.time :as time]
            [settlement.domain.settlement :as settlement]))

(def endpoints
  {:employee-ledger
   {:service            :ledger
    :path               "/ledger/:employee-id"
    :method             :get
    :path-params-schema {:employee-id s/Uuid}
    :response-schema    s-settlement/GetLedgerResponse}})
