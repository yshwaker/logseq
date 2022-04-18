(ns frontend.worker.parser
  (:require ["mldoc" :refer [Mldoc]]
            ["webworker-promise/lib/register" :as registerWebworker]
            [promesa.core :as p]))

(def parse-json (.-parseJson Mldoc))

(defn- parse
  [content config]
  (if (string? content)
    (parse-json content config)
    (throw (js/Error. (str "Can't parse data: " content)))))

(defn init
  []
  (println "Parser worker initialized!")
  (registerWebworker
   (fn [message _emit]
     (let [[content config] message]
       (parse content config)))))
