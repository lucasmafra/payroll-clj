(ns flows.process-batch
  (:require [common-clj.generators :as gen]
            [flows.aux :as aux]
            [midje.sweet :refer :all]
            [selvage.midje.flow :refer [flow *world*]]
            [settlement.system :as sys]
            [matcher-combinators.midje :refer [match]]
            [settlement.schemata.settlement :as s-settlement]
            [schema.core :as s]
            [common-clj.test-helpers :as th])
  (:import java.time.LocalDateTime))

(def last-day-of-month-8am #date-time "2019-09-30T08:00:00")
(def last-day-of-month-9am #date-time "2019-09-30T09:00:00")

(def batch-id (gen/generate s/Uuid))

; Salaried employees get paid on last day of month
(def employee<pay-day> (gen/complete {:employee/contract-type :contract-type/salary}
                                     s-settlement/Employee))

; Hourly rate employees don't get paid on this day (it's not friday)
(def employee<not-pay-day> (gen/complete {:employee/contract-type :contract-type/hourly-rate}
                                         s-settlement/Employee))

; Will mock a negative transaction for this employee
(def employee<pay-day-but-negative-balance> (gen/generate s-settlement/Employee))

(def unsettled-transaction
  (dissoc
   (gen/complete {:transaction/amount 2000M} s-settlement/Transaction)
   :transaction/settled-at))

(def settled-transaction
  (gen/complete {:transaction/settled-at (gen/generate LocalDateTime)} s-settlement/Transaction))

(def negative-transaction (gen/complete {:transaction/amount -100M} s-settlement/Transaction))

(def batch-settlement-message
  #:batch-settlement {:id batch-id})

(flow "process batch"
  (partial th/init! sys/test-system)
      
  (partial aux/mock-employees!
           employee<pay-day>
           employee<not-pay-day>
           employee<pay-day-but-negative-balance>)     
  
  (partial aux/mock-transactions!
           employee<pay-day>                      [settled-transaction unsettled-transaction]
           employee<not-pay-day>                  [settled-transaction unsettled-transaction]
           employee<pay-day-but-negative-balance> [negative-transaction])

  (partial aux/mock-batch! batch-id last-day-of-month-9am)

  (partial th/message-arrived! :process-batch batch-settlement-message)
      
  (fact "sends a :settle-transactions message for each settlement"
    (th/produced-messages :settle-transactions)
    => [#:settlement {:employee-id  (:employee/id employee<pay-day>)
                      :transactions [(:transaction/control-key unsettled-transaction)]}])
  
  (fact "sends a :execute-payment message for each settlement"
    (th/produced-messages :execute-payment)
    => (just [(match #:payment {:recipient   (:employee/id employee<pay-day>)
                                :amount      2000M
                                :control-key anything
                                :method      :payment-method/deposit})]))
  
  (fact "sends a :create-batch-report message"
    (th/produced-messages :create-batch-report) => [{:batch-settlement/id batch-id}])

  (future-fact "sends a :send-payslip message for each settlement"))

(flow "idempotency check - same message arrives twice"
  (partial th/init! sys/test-system)

  (partial aux/mock-employees! employee<pay-day>)

  (partial aux/mock-transactions! employee<pay-day> [unsettled-transaction])

  (partial aux/mock-batch! batch-id last-day-of-month-9am)

  (partial th/message-arrived! :process-batch batch-settlement-message)

  (partial th/clear-produced-messages!)

  ; Same message arrives again
  (partial th/message-arrived! :process-batch batch-settlement-message)

  (facts "in the second time"
    (fact "no :settle-transactions message is produced"
      (th/produced-messages :settle-transactions) => [])

    (fact "no :execute-payment message is produced"
      (th/produced-messages :execute-payment) => [])))
