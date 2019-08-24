(ns ledger.adapters.ledger-test
  (:require [ledger.adapters.ledger :as adapters.ledger]
            [ledger.schemata.ledger :as schemata.ledger]
            [midje.sweet :refer :all]
            [schema.core :as s]))

(def employee-id #uuid "6c8a5312-5716-4bb5-8d85-9521d8692e4b")

(s/def ledger-entry :- schemata.ledger/LedgerEntry
  {:control-key    #uuid "f1be17b4-15f2-4287-b861-1b3a13d82c71"
   :type           :time-slot-remuneration
   :amount         100M
   :reference-date #date "2019-08-22"
   :description    "Remuneration for 5 hours of work on day 2019-08-22"})

(s/with-fn-validation
  (fact "ledger-entry<->ledger-entry-document"
    (-> ledger-entry
        (adapters.ledger/ledger-entry->ledger-entry-document employee-id)
        adapters.ledger/ledger-entry-document->ledger-entry)
    => ledger-entry))
