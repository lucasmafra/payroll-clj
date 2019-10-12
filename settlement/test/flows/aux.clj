(ns flows.aux
  (:require [common-clj.components.http-client.in-memory-http-client :as im-hc]
            [common-clj.test-helpers :as th]
            [common-clj.uuid :as uuid]))

(defn mock-fetch-employees-response! [employees world]
  (let [http-client (-> world :system :http-client)]
    (im-hc/mock-response! http-client :get-all-employees {:body {:employees employees}})
    world))

(defn mock-fetch-transactions-response!
  [& args]
  (let [world (last args)
        http-client (-> world :system :http-client)
        kvs         (butlast args)]
    (doseq [[{:keys [employee/id]} transactions] (partition 2 kvs)]
      (im-hc/mock-response!
       http-client :get-transactions {:employee-id id} {:body {:transactions transactions}}))
    world))

(defn mock-batch-request! [batch-id world]
  (with-redefs [uuid/random (constantly batch-id)]
    (th/request-arrived! :batch-settle world)))

(defn process-batch-message-arrived! [batch-id world]
  (th/message-arrived! :process-batch {:batch-settlement/id batch-id} world))
