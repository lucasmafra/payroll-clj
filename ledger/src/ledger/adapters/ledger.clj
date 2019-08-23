(ns ledger.adapters.ledger
  (:require [schema.core :as s]
            [ledger.schemata.ledger :as schemata.ledger]
            [java-time :refer [local-date]])
  (:import (java.util UUID)))

(defn assoc-if
  [m key value]
  (if value (assoc m key value) m))


(s/defn ledger-entry-document->ledger-entry :- schemata.ledger/LedgerEntry
  [ledger-entry-document :- schemata.ledger/LedgerEntryDocument]
  (-> {:control-key    (-> ledger-entry-document :control-key UUID/fromString)
       :type           (-> ledger-entry-document :type keyword)
       :amount         (-> ledger-entry-document :amount bigdec)
       :reference-date (-> ledger-entry-document :reference-date local-date)}
      (assoc-if :description (-> ledger-entry-document :description))))

(s/defn ledger-entry->ledger-entry-document :- schemata.ledger/LedgerEntryDocument
  [ledger-entry :- schemata.ledger/LedgerEntry
   employee-id  :- s/Uuid]
  (-> {:employee-id    (.toString employee-id)
       :control-key    (-> ledger-entry :control-key .toString)
       :type           (-> ledger-entry :type name)
       :amount         (-> ledger-entry :amount)
       :reference-date (-> ledger-entry :reference-date .toString)}
      (assoc-if :description (-> ledger-entry :description))))
