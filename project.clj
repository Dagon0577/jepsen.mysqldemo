(defproject jepsen.mysqldemo "0.1.0-SNAPSHOT"
  :description "A Jepsen test for mysql"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main jepsen.mysqldemo
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [jepsen "0.1.13"]
                 [verschlimmbesserung "0.1.3"]
                 [org.clojure/java.jdbc "0.2.2"]
                 [mysql/mysql-connector-java "5.1.6"]])
