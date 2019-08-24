(ns ledger.components.ledger-repository.ledger-repository
  (:require [com.stuartsierra.component :as component]
            [common-clj.components.docstore-client.protocol :as docstore-client.protocol]
            [ledger.adapters.ledger :refer [ledger-entry->ledger-entry-document
                                            ledger-entry-document->ledger-entry]]
            [ledger.components.ledger-repository.protocol :refer [LedgerRepository]]
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
                   {:employee-id [:eq (str employee-id)]})]
      (map ledger-entry-document->ledger-entry entries)))

  (get-entry [{:keys [docstore-client]} employee-id control-key]
    (docstore-client.protocol/get-item
     docstore-client
     :ledger
     {:employee-id (str employee-id)
      :control-key (str control-key)}))

  (add-entry! [{:keys [docstore-client]} employee-id ledger-entry]
    (docstore-client.protocol/put-item!
         docstore-client
         :ledger
         (ledger-entry->ledger-entry-document ledger-entry employee-id))))

(defn new-ledger-repository []
  (map->LedgerRepositoryImpl {}))
