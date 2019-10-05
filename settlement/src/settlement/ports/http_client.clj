(ns settlement.ports.http-client
  (:require [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement]))

(def endpoints
  {:get-all-employees
   {:service         :employees
    :path            "/api/employees"
    :method          :get
    :response-schema {:employees [s-settlement/Employee]}}

   :get-transactions
   {:service            :ledger
    :path               "/api/transactions/:employee-id"
    :method             :get
    :path-params-schema {:employee-id s/Uuid}
    :response-schema    {:transactions [s-settlement/Transaction]}}})
