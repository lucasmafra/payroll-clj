(ns settlement.domain.settlement-test
  (:require [fixtures.settlement :as fs]
            [matcher-combinators.midje :refer [match]]
            [midje.sweet :refer :all]
            [schema.core :as s]
            [settlement.domain.settlement :as ds]))

(s/with-fn-validation
  (facts "settle"
    (let [all-transactions              #{fs/transaction-a fs/transaction-b fs/transaction-c}
          settled-transactions          #{fs/transaction-a}
          unsettled-transactions        #{fs/transaction-b fs/transaction-c}
          sum-of-unsettled-transactions (+ fs/amount<transaction-b> fs/amount<transaction-c>)]

      (fact "only unsettled transactions are considered"
        (ds/settle all-transactions settled-transactions fs/as-of-future)
        => (match {:settlement/transactions unsettled-transactions}))

      (fact "balance is the sum of all transactions that should be settled"
        (ds/settle all-transactions settled-transactions fs/as-of-future)
        => (match {:settlement/balance sum-of-unsettled-transactions}))

      (fact "future transactions are not settled"
        (ds/settle #{fs/future-transaction fs/today-transaction fs/yesterday-transaction} #{} fs/as-of-today)
        => (match {:settlement/transactions #{fs/today-transaction fs/yesterday-transaction}}))

      (fact "settlement :as-of is correct"
        (ds/settle all-transactions settled-transactions fs/as-of-future)
        => (match {:settlement/as-of fs/as-of-future}))))

  (facts "pay-day?"
    (fact "salaried employees get paid in the last day of month"
      (ds/pay-day? fs/salary-based-employee fs/last-day-of-month nil) => true
      (ds/pay-day? fs/salary-based-employee fs/not-last-day-of-month nil) => false)

    (fact "hourly employees get paid on fridays"
      (ds/pay-day? fs/hourly-based-employee fs/friday nil) => true
      (ds/pay-day? fs/hourly-based-employee fs/not-friday nil) => false)

    (fact "commissioned employees get paid every two fridays"
      (ds/pay-day? fs/commission-based-employee fs/friday nil) => true
      (ds/pay-day? fs/commission-based-employee fs/not-friday nil) => false
      (ds/pay-day? fs/commission-based-employee fs/this-friday fs/last-settlement<this-friday>) => false
      (ds/pay-day? fs/commission-based-employee fs/this-friday fs/last-settlement<last-friday>) => false
      (ds/pay-day? fs/commission-based-employee fs/this-friday fs/last-settlement<two-fridays-ago>) => true))

  (facts "positive-settlement?"
    (ds/positive-settlement? fs/negative-settlement) => false
    (ds/positive-settlement? fs/zero-settlement) => false
    (ds/positive-settlement? fs/positive-settlement) => true))
