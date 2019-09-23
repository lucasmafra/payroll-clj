(ns flows.get-transactions
  (:require [common-clj.test-helpers :refer :all]
            [flows.aux :as aux]
            [ledger.schemata.ledger :as s-ledger]
            [ledger.system :as sys]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [selvage.midje.flow :refer [*world* flow]]
            [common-clj.generators :as gen]))

(def employee-a (gen/generate s/Uuid))
(def employee-b (gen/generate s/Uuid))
(def transaction-a1 (gen/generate s-ledger/Transaction))
(def transaction-a2 (gen/generate s-ledger/Transaction))
(def transaction-b1 (gen/generate s-ledger/Transaction))

(s/with-fn-validation
  (flow "get employee transactions"
    (partial init! sys/test-system)

    (partial aux/mock-transactions!
             employee-a transaction-a1
             employee-a transaction-a2
             employee-b transaction-b1)

    (partial aux/get-transactions-request-arrived! employee-a)

    (partial aux/get-transactions-request-arrived! employee-b)

    (fact "returns employee transactions"
      (-> *world* :http-responses :get-transactions first :body)
      => {:ledger/employee-id  employee-a
          :ledger/transactions [transaction-a1 transaction-a2]}

      (-> *world* :http-responses :get-transactions second :body)
      => {:ledger/employee-id employee-b
          :ledger/transactions [transaction-b1]})))
