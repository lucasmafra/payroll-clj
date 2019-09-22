(ns settlement.domain.settlement-test
  (:require [schema.core :as s]
            [midje.sweet :refer :all]
            [settlement.domain.settlement :as settlement]
            [common-clj.test-helpers :as th]))

(def last-day-of-current-month #date "2019-07-31")
(def last-day-of-current-month-9am #date-time "2019-07-31T09:00:00")
(def last-day-of-previous-month #date "2019-06-30")
(def last-day-of-previous-month-9am #date-time "2019-06-30T09:00:00")
(def a-week-ago #date "2019-07-24")
(def not-last-day-of-month #date "2019-07-30")
(def friday #date "2019-09-06")
(def not-friday #date "2019-09-05")
(def last-friday #date "2019-09-06")
(def last-friday-9am #date-time "2019-09-06T09:00:00")
(def this-friday #date "2019-09-13")
(def next-friday #date "2019-09-20")

(def current-salary
  {:transaction/control-key    (th/random-uuid)
   :transaction/category       :salary
   :transaction/amount         2000M
   :transaction/reference-date last-day-of-current-month})

(def previous-salary
  {:transaction/control-key    (th/random-uuid)
   :transaction/category       :salary
   :transaction/amount         2000M
   :transaction/reference-date last-day-of-previous-month
   :transaction/settled-at     last-day-of-previous-month-9am})

(def service-charge
  {:transaction/control-key    (th/random-uuid)
   :transaction/category       :service-charge
   :transaction/amount         -300M
   :transaction/reference-date a-week-ago})

(def current-salary-settled
  (assoc current-salary :transaction/settled-at last-day-of-current-month-9am))

(def service-charge-settled
  (assoc service-charge :transaction/settled-at last-day-of-current-month-9am))

(def transactions [current-salary service-charge previous-salary])

(def last-settlement
  {:settlement/balance      0M
   :settlement/transactions []
   :settlement/created-at   last-friday-9am})

(s/with-fn-validation
  (facts "settle"
    (fact "only not-settled transactions are considered"
      (-> transactions
          (settlement/settle last-day-of-current-month-9am)
          :settlement/transactions)
      => [current-salary-settled service-charge-settled])
    (fact "balance is the sum of all transactions to be settled"
      (-> transactions
          (settlement/settle last-day-of-current-month-9am)
          :settlement/balance)
      => 1700M))

  (facts "is-pay-day?"
    (fact "salaried employees get paid in the last day of month"
      (settlement/is-pay-day? :contract-type/salary last-day-of-current-month) => true
      (settlement/is-pay-day? :contract-type/salary not-last-day-of-month) => false)

    (fact "hourly employees get paid on fridays"
      (settlement/is-pay-day? :contract-type/hourly-rate friday) => true
      (settlement/is-pay-day? :contract-type/hourly-rate not-friday) => false)

    (facts "commissioned employees get paid every two fridays"
      (fact "when no last settlement is informed"
        (settlement/is-pay-day? :contract-type/sales-commission last-friday)
        => true

        (settlement/is-pay-day? :contract-type/sales-commission last-friday nil)
        => true)

      (fact "when last settlement happened less than two weeks ago"
        (settlement/is-pay-day? :contract-type/sales-commission
                                last-friday
                                last-settlement)
        => false

        (settlement/is-pay-day? :contract-type/sales-commission
                                this-friday
                                last-settlement)
        => false)

      (fact "when last settlement happened at least two weeks ago"
        (settlement/is-pay-day? :contract-type/sales-commission
                                next-friday
                                last-settlement)
        => true))))
