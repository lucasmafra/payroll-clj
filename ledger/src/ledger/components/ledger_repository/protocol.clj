(ns ledger.components.ledger-repository.protocol)

(defprotocol LedgerRepository
  (get-ledger [component employee-id])
  (add-new-entry! [component employee-id ledger-entry]))
