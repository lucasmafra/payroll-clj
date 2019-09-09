(ns flows.get-employee-ledger
  (:require [common-clj.test-helpers :refer [init! request-arrived!]]
            [flows.aux :refer [create-entry!]]
            [ledger.schemata.ledger :as schemata.ledger]
            [ledger.system :refer [test-system]]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [selvage.midje.flow :refer [*world* flow]]))

(def employee-id #uuid "9cdf53aa-a6fd-44e8-8d9b-93dde33fec41")

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

(s/with-fn-validation
  (flow "on get-ledger request"
    (partial init! test-system)

    (partial create-entry! employee-id time-slot-remuneration)

    (partial create-entry! employee-id union-service-charge)

    (partial request-arrived! :get-ledger {:path-params
                                           {:employee-id employee-id}})

    (fact "employee ledger is returned"
      (-> *world* :http-responses :get-ledger first :body)
      => {:employee-id employee-id
          :entries     [time-slot-remuneration
                        union-service-charge]})))
