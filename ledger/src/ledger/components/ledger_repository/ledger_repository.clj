(ns ledger.components.ledger-repository.ledger-repository
  (:require [com.stuartsierra.component :as component]
            [ledger.components.ledger-repository.protocol :refer [LedgerRepository]]
            [common-clj.components.docstore-client.protocol :as docstore-client.protocol]
            [ledger.adapters.ledger :refer [ledger-entry-document->ledger-entry
                                            ledger-entry->ledger-entry-document]]
            [schema.core :as s]))

(s/defrecord LedgerRepositoryImpl []
  component/Lifecycle
  (start [{:keys [docstore-client] :as component}]
    (docstore-client.protocol/ensure-table!
     docstore-client
     :ledger
     [:employee-id :s]
     [:control-key :s])
    component)

  LedgerRepository
  (get-ledger [{:keys [docstore-client]} employee-id]
    (let [entries (docstore-client.protocol/query
                   docstore-client
                   :ledger
                   {:employee-id [:eq (.toString employee-id)]})]
      (map ledger-entry-document->ledger-entry entries)))

  (add-new-entry! [{:keys [docstore-client]} employee-id ledger-entry]
    (let [existent-entry (docstore-client.protocol/get-item
                          docstore-client
                          :ledger
                          {:employee-id (.toString employee-id)
                           :control-key (.toString (:control-key ledger-entry))})]
      (when-not existent-entry
        (docstore-client.protocol/put-item!
         docstore-client
         :ledger
         (ledger-entry->ledger-entry-document ledger-entry employee-id))))))

(defn new-ledger-repository []
  (map->LedgerRepositoryImpl {}))
