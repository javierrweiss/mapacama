(ns mapacama.componentes)

(defn crear-fila-tabla
  [datos]
  [:tr (mapv #(vector :td %) datos)])
 
(defn crear-headers-tabla
  [cabeceras]
  (into [:tr] (comp (map name) (map #(vector :th %))) cabeceras))

(defn crear-tabla
  "Recibe un vector de mapas y devuelve una tabla en formato hiccup"
  [datos]
  (let [cols (->> datos first (into (sorted-map)) keys)
        rows (->> datos (mapv #(into (sorted-map) %)) (mapv vals))]
    [:table
     [:tbody
      [:thead
       (crear-headers-tabla cols)]
      (mapv crear-fila-tabla rows)]]))

(comment
  
  (crear-headers-tabla [:a :b :c])

  (crear-fila-tabla [:a :b :c])

  (crear-fila-tabla ["a" "b" "c"])
  
  (crear-headers-tabla (->> mapacama.repositorio.conexiones/camas first (into (sorted-map)) keys))
  
  (tap>
   (into [:table]
         (mapv crear-fila-tabla (->> mapacama.repositorio.conexiones/camas (mapv #(into (sorted-map) %)) (mapv vals)))))
  
  (tap> (crear-tabla mapacama.repositorio.conexiones/camas))

  (tap> (dev.onionpancakes.chassis.core/html (crear-tabla mapacama.repositorio.conexiones/camas)))

  ((fnil name "") :bs)

  :rcf)