(ns settlement.ports.http-client
  (:require [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement]
            [common-clj.components.http-client.protocol :as hc-pro]))

(defn fetch-employees [{:keys [http-client]}]
  (:employees (hc-pro/request http-client :get-all-employees)))

(defn fetch-all-transactions [{:keys [employee/id]} {:keys [http-client]}]
  (:transactions (hc-pro/request http-client :get-transactions {:employee-id id})))

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
