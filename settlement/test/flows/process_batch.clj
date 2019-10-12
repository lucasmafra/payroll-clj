(ns flows.process-batch
  (:require [flows.aux :as aux]
            [midje.sweet :refer :all]
            [selvage.midje.flow :refer [flow *world*]]
            [settlement.system :as sys]
            [matcher-combinators.midje :refer [match]]
            [schema.core :as s]
            [fixtures.settlement :as fs]
            [common-clj.test-helpers :as th :refer [as-of]]))

(as-of fs/last-day-of-month-9am
  (flow "process batch"
    (partial th/init! sys/test-system)
    
    (partial aux/mock-fetch-employees-response!
             [fs/employee<pay-day>
              fs/employee<not-pay-day>
              fs/employee<pay-day-but-negative-balance>])

    (partial aux/mock-fetch-transactions-response!
             fs/employee<pay-day>                      [fs/transaction-a fs/transaction-b]
             fs/employee<not-pay-day>                  [fs/transaction-a fs/transaction-b]
             fs/employee<pay-day-but-negative-balance> [fs/negative-transaction])
    
    (partial aux/mock-batch-request! fs/batch-id)
    
    (partial aux/process-batch-message-arrived! fs/batch-id)
    
    (fact "a :execute-payment message is sent for each settlement made"
      (th/produced-messages :execute-payment)
      => (just [(match #:payment {:recipient   fs/employee-id<pay-day>
                                  :amount      (+ fs/amount<transaction-a> fs/amount<transaction-b>)
                                  :control-key anything
                                  :method      :payment-method/deposit})]))

    (future-fact "a :send-payslip message is sent for each settlement made")
    
    (fact "a :create-batch-report message is sent"
      (th/produced-messages :create-batch-report) => [{:batch-settlement/id fs/batch-id}])))
  
(as-of fs/last-day-of-month-9am
  (flow "idempotency check - same message arrives twice"
    (partial th/init! sys/test-system)
             
    (partial aux/mock-fetch-employees-response! [fs/employee<pay-day>])
             
    (partial aux/mock-fetch-transactions-response!
             fs/employee<pay-day> [fs/transaction-a])
    
    (partial aux/mock-batch-request! fs/batch-id)
    
    ; First time
    (partial aux/process-batch-message-arrived! fs/batch-id)
             
    (partial th/clear-produced-messages!)
          
    ; Second time   
    (partial aux/process-batch-message-arrived! fs/batch-id)

    (facts "in the second time"
      (fact "no :execute-payment message is produced"
        (th/produced-messages :execute-payment) => [])
      (fact "no :create-batch-report message is produced"
        (th/produced-messages :create-batch-report) => []))))

(as-of fs/last-day-of-month-9am
  (flow "two different messages arrive"
    (partial th/init! sys/test-system)
          
    (partial aux/mock-fetch-employees-response! [fs/employee<pay-day>])
          
    ; In the first time the employee has only transaction-a 
    (partial aux/mock-fetch-transactions-response!
             fs/employee<pay-day> [fs/transaction-a])
             
    (partial aux/mock-batch-request! fs/batch-id)

    ; First message arrives
    (partial aux/process-batch-message-arrived! fs/batch-id)
             
    (partial th/clear-produced-messages!)
    
    ; In the second time the employee has transactions a and b
    (partial aux/mock-fetch-transactions-response!
             fs/employee<pay-day> [fs/transaction-b fs/transaction-a])
         
    (partial aux/mock-batch-request! fs/another-batch-id)
             
    ; Second message arrives
    (partial aux/process-batch-message-arrived! fs/another-batch-id)
             
    (facts "in the second message only not settled transactions are considered"
      (fact "a :execute-payment message is produced"
        (th/produced-messages :execute-payment)
        => (just [(match #:payment {:recipient   fs/employee-id<pay-day>
                                    :amount      fs/amount<transaction-b> 
                                    :control-key anything
                                    :method      :payment-method/deposit})]))
               
      (fact "a :create-batch-report message is sent"
        (th/produced-messages :create-batch-report)
        => [{:batch-settlement/id fs/another-batch-id}]))))
