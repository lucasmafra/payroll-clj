(ns ledger.flows.create_ledger_entry
  (:require [selvage.midje.flow :refer [*world* flow]]
            [midje.sweet :refer :all]
            [common-clj.components.producer.protocol :as producer.protocol]
            [ledger.components.ledger-repository.protocol :as ledger-repository.protocol]
            [common-clj.test-helpers :refer [init! message-arrived!]]
            [schema.core :as s]
            [ledger.schemata.ledger :as schemata.ledger]
            [ledger.system :refer [test-system]]))

(def employee-id #uuid "6e931327-51b9-47ff-bc0d-622425befb78")

(s/def time-slot-remuneration :- schemata.ledger/LedgerEntry
  {:control-key    #uuid "f1be17b4-15f2-4287-b861-1b3a13d82c71"
   :type           :time-slot-remuneration
   :amount         100M
   :reference-date #date "2019-08-22"
   :description    "Remuneration for 5 hours of work on day 2019-08-22"})

(s/def union-service-charge
  {:control-key    #uuid "6c8a5312-5716-4bb5-8d85-9521d8692e4b"
   :type           :union-service-charge
   :amount         -20M
   :reference-date #date "2019-08-23"})

(def duplicated-entry
  (assoc union-service-charge :control-key (:control-key time-slot-remuneration)))

(defn get-ledger [employee-id]
  (let [ledger-repository (-> *world* :system :ledger-repository)]
    (ledger-repository.protocol/get-ledger ledger-repository employee-id)))

(flow "create ledger entries"
  (partial init! test-system)

  (partial message-arrived! :create-ledger-entry {:employee-id employee-id
                                                  :entry       time-slot-remuneration})

  (partial message-arrived! :create-ledger-entry {:employee-id employee-id
                                                  :entry       union-service-charge})

  (fact "a new entry is created"
    (get-ledger employee-id)
    => (just [time-slot-remuneration union-service-charge] :in-any-order)))

(flow "message with repeated control-key arrives"
  (partial init! test-system)

  (partial message-arrived! :create-ledger-entry {:employee-id employee-id
                                                  :entry       time-slot-remuneration})

  (partial message-arrived! :create-ledger-entry {:employee-id employee-id
                                                  :entry       duplicated-entry})

  (fact "entry is not created"
    (get-ledger employee-id)
    => (just [time-slot-remuneration])))
