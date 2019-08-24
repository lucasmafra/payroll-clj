(ns ledger.components.ledger-repository.protocol)

(defprotocol LedgerRepository
  (get-ledger [component employee-id])
  (get-entry [component employee-id control-key])
  (add-entry! [component employee-id ledger-entry]))
