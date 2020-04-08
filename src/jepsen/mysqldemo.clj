(ns jepsen.mysqldemo
  (:require [clojure.tools.logging :refer :all]
            [clojure.string :as str]
            [jepsen [checker :as checker] 
	     [cli :as cli]
             [client :as client]
             [control :as c]
             [db :as db]
             [generator :as gen]
             [nemesis :as nemesis]
             [tests :as tests]
             [mysqlClient :as mysqlC]]
            [jepsen.control.util :as cu]
            [jepsen.os.debian :as debian]
            [knossos.model :as model]
            [jepsen.checker.timeline :as timeline]
            [slingshot.slingshot :refer [try+]]))

(def counter
  (let [tick (atom 0)]
       #(swap! tick inc)))


(defn r   [_ _] {:type :invoke, :f :read, :value nil})
(defn w   [_ _] {:type :invoke, :f :write, :value (counter)})
(defn cas [_ _] {:type :invoke, :f :cas, :value [(rand-int 5) (rand-int 5)]})

(def dir     "/opt/mysql")
(def logfile (str dir "/mysql.log"))

(defn node-url
  "An HTTP url for connecting to a node on a particular port."
  [node port]
  (str "http://" (name node) ":" port))

(defn client-url
  "The HTTP url clients use to talk to a node."
  [node]
  (node-url node 3306))

(defrecord Client [conn]
  client/Client
  (open! [this test node]
    (assoc this :conn (mysqlC/connect (client-url node)
                                   {:timeout 5000})))

  (setup! [this test])

  (invoke! [this test op]
    (case (:f op)
      :read (assoc op :type :ok, :value (mysqlC/select conn))
      :write (do (mysqlC/insert conn (:value op))
                 (assoc op :type :ok))
      :cas (try+
	   	(let [[old new] (:value op)]
             (assoc op :type (if (= (mysqlC/cas conn old new) 0)
                               :ok
                               :fail)))
		(catch [:errorCode 100] ex
		  (assoc op :type :fail, :error :not-found)))))

  (teardown! [this test])

  (close! [_ test]
    ; If our connection were stateful, we'd close it here. Verschlimmmbesserung
    ; doesn't actually hold connections, so there's nothing to close.
    ))

(defn db
  "mysql for a particular version."
  [version]
  (reify db/DB
    (setup! [_ test node]
      (info node "installing mysql" version))

    (teardown! [_ test node]
      (info node "tearing down mysql"))

    db/LogFiles
    (log-files [- test node]
      [logfile])))

(defn mysql-test
  "Given an options map from the command line runner (e.g. :nodes, :ssh,
:concurrency ...), constructs a test map."
  [opts]
  (merge tests/noop-test
         opts
         {:name "mysql"
          :os   debian/os
          :db   (db "v5.6")
          :client (Client. nil)
	  :nemesis    (nemesis/partition-random-halves)
          :checker (checker/compose
                     {:perf   (checker/perf)
                      :linear (checker/linearizable {:model     (model/cas-register)
                                                     :algorithm :linear})
                      :timeline (timeline/html)})
          :generator (->> (gen/mix [r w cas])
                          (gen/stagger 1/10)
                          (gen/nemesis
                            (gen/seq (cycle [(gen/sleep 5)
                                             {:type :info, :f :start}
                                             (gen/sleep 5)
                                             {:type :info, :f :stop}])))
                          (gen/time-limit (:time-limit opts)))}))


(defn -main
  "Handles command line arguments. Can either run a test, or a web server for
  browsing results."
  [& args]
  (cli/run! (merge (cli/single-test-cmd {:test-fn mysql-test})
                   (cli/serve-cmd))
            args))
