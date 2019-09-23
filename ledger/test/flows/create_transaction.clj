(ns flows.create-transaction
  (:require [common-clj.test-helpers :refer :all]
            [common-clj.generators :as gen]
            [flows.aux :as aux]            
            [ledger.schemata.ledger :as s-ledger]
            [ledger.system :as sys]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [selvage.midje.flow :refer [*world* flow]]))

(def employee-a (gen/generate s/Uuid))
(def employee-b (gen/generate s/Uuid))
(def transaction-a1 (gen/generate s-ledger/Transaction))
(def transaction-a2 (gen/generate s-ledger/Transaction))
(def transaction-b1 (gen/generate s-ledger/Transaction))
(def transaction-a1<duplicated> (gen/complete
                                 {:transaction/control-key
                                  (:transaction/control-key transaction-a1)}
                                 s-ledger/Transaction))

(s/with-fn-validation
  (flow "create transaction"
    (partial init! sys/test-system)

    (partial aux/create-transaction-messages-arrived!
             employee-a transaction-a1
             employee-a transaction-a2
             employee-b transaction-b1)

    (fact "creates new transaction for each message"
      (aux/get-transactions employee-a)
      => (just [transaction-a1 transaction-a2] :in-any-order)
      
      (aux/get-transactions employee-b)
      => [transaction-b1]))

  (flow "duplicated transaction"
    (partial init! sys/test-system)

    (partial aux/create-transaction-messages-arrived!
             employee-a transaction-a1
             employee-a transaction-a1<duplicated>)

    (fact "only first transaction is created"
      (aux/get-transactions employee-a)
      => [transaction-a1])))
