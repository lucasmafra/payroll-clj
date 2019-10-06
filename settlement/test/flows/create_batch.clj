(ns flows.create-batch
  (:require [selvage.midje.flow :refer [flow]]
            [midje.sweet :refer :all]
            [common-clj.test-helpers :as th]
            [common-clj.test-helpers :refer [as-of]]
            [matcher-combinators.midje :refer [match]]
            [settlement.system :as sys]
            [common-clj.generators :as gen]
            [settlement.controllers.settlement :as c-settlement]
            [schema.core :as s])
  (:import (java.time LocalDateTime)))

(def some-date-time (gen/generate LocalDateTime))
(def now (gen/generate LocalDateTime))
(def batch-id (gen/generate s/Uuid))

(as-of now
  (with-redefs [c-settlement/new-batch-id (constantly batch-id)]
    (flow "create batch"
      (partial th/init! sys/test-system)
          
      (partial th/request-arrived! :batch-settle {:body {:batch-settlement/as-of some-date-time}})

      (fact "creates a batch settlement"
        (th/http-response :batch-settle) => (match
                                             {:status 201
                                              :body #:batch-settlement {:id    batch-id
                                                                        :as-of some-date-time}}))

      (fact "produces a :process-batch message"
        (th/produced-messages :process-batch) => [{:batch-settlement/id batch-id}]))

    (flow "create batch: no :as-of is present on request body"
      (partial th/init! sys/test-system)

      (partial th/request-arrived! :batch-settle)

      (fact "creates a batch settlement as of now"
        (th/http-response :batch-settle) => (match
                                             {:status 201
                                              :body #:batch-settlement {:id    batch-id
                                                                        :as-of now}})))))
