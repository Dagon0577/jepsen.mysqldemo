(ns jepsen.mysqlClient
  (:require [clojure.java.jdbc :as sql]))

(def default-timeout "milliseconds" 1000)

(def default-swap-retry-delay
  "How long to wait (approximately) between retrying swap! operations which
  failed. In milliseconds."
  100)

(def mysqldb {:subprotocol "mysql"
              :subname "//127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8"
              :user "root"
              :password "root"})

(defn select [conn]
  (let [testdb {:subprotocol "mysql"
               :subname (str (subs (conn :endpoint) 5) "/test?useUnicode=true&characterEncoding=UTF-8")
               :user "root"
               :password "root"}]
    (sql/with-connection testdb ; 链接数据库
      (sql/with-query-results rows ; 查询结果绑定
        ["select * from customer"] ; 查询用户数据
            ;(do (println rows)); 打印
        (count rows)))))

(defn insert [conn id]
  (let [testdb {:subprotocol "mysql"
               :subname (str (subs (conn :endpoint) 5) "/test?useUnicode=true&characterEncoding=UTF-8")
               :user "root"
               :password "root"}]
    (sql/with-connection testdb ; 链接数据库
      (sql/insert-records :customer
                          {:id id, :name 1, :telephone (+ 13912340000 id), :provinceid 2 :province "Aomen" :city "澳门" :address "某某街某某号"}))))

(defn cas [conn old new]
  (let [testdb {:subprotocol "mysql"
               :subname (str (subs (conn :endpoint) 5) "/test?useUnicode=true&characterEncoding=UTF-8")
               :user "root"
               :password "root"}]
    (sql/with-connection testdb ; 链接数据库
      (sql/with-query-results rows ; 查询结果绑定
        ["select * from customer where id = 1 and name = ?" old] ; 查询用户数据，id自定义，这里只是测试

        (sql/update-values :customer
                           ["id = 1 and name = ?" old]
                           {:name new})

        (sql/with-query-results rowsAfter
          ["select * from customer where id =1 and name = ?" new]
          (if (> (count rows) 0)
            (- (count rows) (count rowsAfter))
            (- (count rows) 1)))))))



(defn connect
  "Creates a new mysql client for the given server URI. Example:
  (def mysqldb (connect \"//127.0.0.1:3306\"))
  Options:
  :timeout            How long, in milliseconds, to wait for requests.
  :swap-retry-delay   Roughly how long to wait between CAS retries in swap!"
  ([server-uri]
   (connect server-uri {}))
  ([server-uri opts]
   (merge {:timeout           default-timeout
           :swap-retry-delay  default-swap-retry-delay
           :endpoint          server-uri}
          opts)))
