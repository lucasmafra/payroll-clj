(ns settlement.controllers.settlement)

(defn- fetch-ledger
  [employee-id http-client]
  (hc-pro/request http-client :employee-ledger {:employee-id employee-id}))

(defn- settle-transactions
  [employee-id transactions producer]
  (producer-pro/produce! producer
                         :settle-transactions
                         {:employee-id  employee-id
                          :transactions (map a-s/transaction->settled-transaction
                                             transactions)}))

(defn- create-payment-order!
  [employee-id control-key amount producer]
  (producer-pro/produce! producer
                         :create-payment-order
                         {:payee       employee-id
                          :control-key control-key
                          :amount      amount}))

(defn- update-batch-report
  [batch-settlement-id producer]
  (producer-pro/produce! producer
                         :update-batch-report
                         {:batch-settlement-id batch-settlement-id}))

(defn batch-settle!
  [batch-settlement-id {:keys [db http-client producer]}]
  (doseq [{:keys [employee-id control-key]} (db/unhandled-schedulements (time/now) db)
          :let [ledger (fetch-ledger employee-id http-client)
                {:keys [transactions balance]} (settlement/settle ledger (time/now))]]
    (settle-transactions! employee-id transactions producer)
    (create-payment-order! employee-id control-key balance producer))
  (update-batch-report! batch-settlement-id producer))
