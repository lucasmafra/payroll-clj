(ns flows.batch-settlement
  (:require [common-clj.test-helpers :refer :all]
            [midje.sweet :refer :all]
            [selvage.midje.flow :refer [flow]]
            [settlement.system :as sys]
            [flows.aux :as aux]))

(def employee-a (random-uuid))
(def employee-b (random-uuid))
(def salary-a 2000M)
(def salary-b 3000M)

(def five-minutes-ago #date-time "2019-09-19T11:55:00")
(def now #date-time "2019-09-19T12:00:00")
(def five-minutes-from-now #date-time "2019-09-19T12:05:00")

(as-of now
  (flow "batch-settlement"
    (partial init! sys/test-system)
        
    (partial aux/mock-ledgers!
             employee-a ledger-a
             employee-b ledger-b)

    (partial aux/schedule-settlements!
             employee-a five-minutes-ago
             employee-b now
             employee-c five-minutes-from-now)
    
    (partial request-arrived! :batch-settlement)

    (fact "only past/now unhandled scheduled settlements are handled")

    (fact "transactions are settled")

    (fact "payment orders are generated")

    (fact "a report of the batch run is created")))
