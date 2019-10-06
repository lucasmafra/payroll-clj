(ns settlement.adapters.settlement-test
  (:require [common-clj.generators :as gen]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [matcher-combinators.midje :refer [match]]
            [settlement.adapters.settlement :as a-settlement]
            [settlement.schemata.settlement :as s-settlement]))

(def employee-id (gen/generate s/Uuid))
(def employee (gen/complete {:employee/id employee-id} s-settlement/Employee))
(def transaction-control-key (gen/generate s/Uuid))
(def transaction (gen/complete {:transaction/control-key transaction-control-key}
                               s-settlement/Transaction))
(def balance (gen/generate BigDecimal))
(def settlement (gen/complete {:settlement/transactions [transaction]
                               :settlement/balance balance}
                              s-settlement/Settlement))

(s/with-fn-validation
  (fact "->settle-transactions-message"
    (a-settlement/->settle-transactions-message employee settlement)
    => #:settlement {:employee-id  employee-id
                     :transactions [transaction-control-key]})

  (fact "->execute-payment-message"
    (a-settlement/->execute-payment-message employee settlement :payment-method/deposit)
    => (match #:payment {:recipient   employee-id
                         :amount      balance
                         :control-key anything
                         :method      :payment-method/deposit})))
