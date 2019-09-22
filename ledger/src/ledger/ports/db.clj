(ns ledger.ports.db
  (:require [ledger.schemata.ledger :as s-ledger]
            [schema.core :as s]
            [common-clj.components.docstore-client.protocol :as dc-pro]))

(s/defn get-transactions :- [s-ledger/Transaction]
  [employee-id db]
  (dc-pro/query db
                :ledger/transactions
                {:transaction/employee-id employee-id}
                {:response/schema [s-ledger/Transaction]}))

(s/defn get-transaction :- (s/maybe s-ledger/Transaction)
  [employee-id control-key db]
  (dc-pro/maybe-get-item db
                         :ledger/transactions
                         {:transaction/employee-id employee-id
                          :transaction/control-key control-key}
                         {:response/schema s-ledger/Transaction}))

(s/defn save-transaction! :- s-ledger/Transaction
  [employee-id transaction db]
    (dc-pro/put-item! db
                      :ledger/transactions
                      {:transaction/employee-id employee-id}
                      transaction))
