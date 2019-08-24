(ns ledger.adapters.ledger
  (:require [java-time :refer [local-date]]
            [ledger.schemata.ledger :as schemata.ledger]
            [schema.core :as s])
  (:import (java.util UUID)))

(defn assoc-if
  [m key value]
  (if value (assoc m key value) m))

(s/defn ledger-entry-document->ledger-entry :- schemata.ledger/LedgerEntry
  [ledger-entry-document :- schemata.ledger/LedgerEntryDocument]
  (assoc-if
   {:amount (-> ledger-entry-document :amount bigdec)
    :type (-> ledger-entry-document :type keyword)
    :control-key (-> ledger-entry-document :control-key UUID/fromString)
    :reference-date (-> ledger-entry-document :reference-date local-date)}
   :description (:description ledger-entry-document)))

(s/defn ledger-entry->ledger-entry-document :- schemata.ledger/LedgerEntryDocument
  [ledger-entry :- schemata.ledger/LedgerEntry
   employee-id  :- s/Uuid]
  (assoc-if
   {:amount (:amount ledger-entry)
    :type (-> ledger-entry :type name)
    :control-key (-> ledger-entry :control-key str)
    :reference-date (-> ledger-entry :reference-date str)
    :employee-id (str employee-id)}
   :description (:description ledger-entry)))
