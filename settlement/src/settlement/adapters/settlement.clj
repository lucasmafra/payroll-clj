(ns settlement.adapters.settlement
  (:require [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement]))

(s/defn ->settle-transactions-message :- s-settlement/SettleTransactionsMessage
  [{:keys [employee/id]} :- s-settlement/Employee
   {:keys [settlement/transactions]} :- s-settlement/Settlement]
  #:settlement
  {:employee-id  id
   :transactions (map :transaction/control-key transactions)})

(s/defn ->execute-payment-message :- s-settlement/ExecutePaymentMessage
  [{:keys [employee/id]} :- s-settlement/Employee
   {:keys [settlement/balance]} :- s-settlement/Settlement
   payment-method :- s-settlement/PaymentMethod]
  #:payment
  {:recipient id
   :amount    balance
   :method    payment-method})
