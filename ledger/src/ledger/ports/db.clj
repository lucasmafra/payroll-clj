(ns ledger.ports.db
  (:require [ledger.schemata.ledger :as schemata.ledger]
            [schema.core :as s]
            [common-clj.components.docstore-client.protocol
             :as docstore-client.protocol]))

(s/defn get-ledger :- [schemata.ledger/LedgerEntry]
  [employee-id db]
  (docstore-client.protocol/query db
                                  :ledger
                                  {:employee-id employee-id}
                                  {:schema-resp [schemata.ledger/LedgerEntry]}))

(s/defn get-ledger-entry :- (s/maybe schemata.ledger/LedgerEntry)
  [employee-id control-key db]
  (docstore-client.protocol/get-item db
                                     :ledger
                                     {:employee-id employee-id
                                      :control-key control-key}
                                     {:schema-resp schemata.ledger/LedgerEntry}))

(s/defn save-ledger-entry! :- schemata.ledger/LedgerEntry
  [employee-id ledger-entry db]
    (docstore-client.protocol/put-item! db
                                        :ledger
                                        {:employee-id employee-id}
                                        ledger-entry))
