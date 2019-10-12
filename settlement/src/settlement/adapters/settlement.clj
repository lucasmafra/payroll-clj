(ns settlement.adapters.settlement
  (:require [schema.core :as s]
            [settlement.schemata.settlement :as s-settlement]
            [common-clj.generators :as gen]))

(s/defn ->execute-payment-message :- s-settlement/ExecutePaymentMessage
  [{:keys [employee/id]} :- s-settlement/Employee
   {:keys [settlement/balance]} :- s-settlement/Settlement
   payment-method :- s-settlement/PaymentMethod]
  #:payment
  {:recipient id
   :amount    balance
   :method    payment-method
   :control-key (gen/generate s/Uuid)})
