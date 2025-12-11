(ns mapacama.repositorio.sql
  (:require [honey.sql :as hsql]
            [mapacama.repositorio.conexiones :refer [con-maestros!]]
            [com.brunobonacci.mulog :as µ])
  (:import (java.time LocalDateTime ZoneId)))


(defn obtener-consulta-carga-camas
  []
  (hsql/format {:select [:cam_utilstand
                         :cam_est
                         :cam_tipcama
                         :cam_oxi_sn
                         :cam_oxi_hab
                         :cam_histclin
                         :cam_habi
                         :cam_habi
                         :cam_cama 
                         :cam_tiphistclin
                         :cam_utilactual_1
                         :cam_utilactual_2
                         :cam_utilactual_3
                         :cam_utilactual_4
                         :cam_utilactual_5]
                :from :tbc_camas
                :order-by [:cam_habi :cam_cama]}))

(defn cargar-ocupacion-camas
  []
  (try
    (-> (obtener-consulta-carga-camas)
        con-maestros!)
    (catch Exception e (µ/log ::error-al-cargar-ocupacion-camas :mensaje (ex-message e) :fecha (LocalDateTime/now (ZoneId/of "America/Buenos_Aires"))))))


(comment
  
  (tap> (cargar-ocupacion-camas))

  (tap> (hsql/format {:select [:cam_utilstand
                               :cam_est
                               :cam_tipcama
                               :cam_oxi_sn
                               :cam_oxi_hab
                               :cam_histclin
                               :cam_habi
                               :cam_habi
                               :cam_cama
                               :cam_tiphistclin
                               :cam_utilactual_1
                               :cam_utilactual_2
                               :cam_utilactual_3
                               :cam_utilactual_4
                               :cam_utilactual_5]
                      :from :tbc_camas
                      :order-by [[:cam_habi :desc] [:cam_cama :desc]]})) 

  "select Cam_utilstand, Cam_est, Cam_tipcama, Cam_oxi_sn, Cam_oxi_hab, ADM.adm_aislado, Cam_histclin, Cam_habi, Cam_cama, Cam_tiphistclin, "
  + "if (cam_tiphistclin = 1 , HCAB.histcabApellnom, ADM.Adm_ApelNom) Nombre, "
  + "if (cam_tiphistclin = 1 , HCAB.histcabfechanac, ADM.adm_fecnac) Edad, "
  + "if (cam_tiphistclin = 1 , HCAB.histcabsexo, ADM.adm_sexo) Sexo, "
  + "Cam_utilactual_1, Cam_utilactual_2, Cam_utilactual_3, Cam_utilactual_4, Cam_utilactual_5, "
  + "if (cam_tiphistclin = 1 , '', PAT.Pat_Descrip) Patologia, "
  + "if (cam_tiphistclin = 1 , OBRA.Obr_razonsoc, OBR.Obr_razonsoc) Obras, "
  + "if (cam_tiphistclin = 1 , HCAB.histcabintobsamd, ADM.adm_fecing) Fecha_ing, "
  + "if (cam_tiphistclin = 1 , HCAB.histcabintobsamd, ADM.adm_fecaltaadmin) Fecha_alta_adm, "
  + "if (cam_tiphistclin = 1 , HCAB.histcabintobshor, ADM.adm_horing) Hora_ing, "
  + "if (cam_tiphistclin = 1 , HCAB.histcabintobshor, ADM.adm_horaltaadmin) Hora_alta_adm, "
  + "if (cam_tiphistclin = 1 , 0, ADM.adm_fecanulepic) Fec_anul_epic, "
  + "if (cam_tiphistclin = 1 , 0, ADM.adm_ambulancia) Ambulancia, "
  + "if (cam_tiphistclin = 1 , 0, ADM.adm_fecepic) Fecha_epic, "
  + "if (cam_tiphistclin = 1 , 0, ADM.adm_pasepiso) Pase_piso, "
  + "if (cam_tiphistclin = 1 , 0, ADM.adm_fecegresanat) Fecha_egreso, "
  + "if (cam_tiphistclin = 1 , '', UTILC.Utic_UsoAbrev) Utilcama, "
  
  + "if (cam_tiphistclin = 1 , 0, "
  + "        if (ADM.Adm_FecAltaAdmin > 0, "
  + "            if (ADM.Adm_Ambulancia = 1 or ADM.Adm_Ambulancia = 2, 6, 7) "
  + "        ,  "
  + "            if (ADM.Adm_FecAnulEpic > 0, "
  + "               if (ADM.Adm_Ambulancia = 1 or ADM.Adm_Ambulancia = 2, 8, 9) "
  + "            , "
  + "               if (ADM.Adm_FecEpic > 0, "
  + "                   if (ADM.Adm_PasePiso = 0, "
  + "                      if (ADM.Adm_Ambulancia = 1 or ADM.Adm_Ambulancia = 2, "
  + "                          if (ADM.Adm_FecEgreSanat > " + globals.gbl_fecha_hoy_int + ", 11, 12) "
  + "                      , "
  + "                          if (ADM.Adm_FecEgreSanat > " + globals.gbl_fecha_hoy_int + ", 21, 22) "
  + "                      ) "
  + "                   , "
  + "                      if (ADM.Adm_PasePiso = 1, "
  + "                         if (ADM.Adm_Ambulancia = 1 or ADM.Adm_Ambulancia = 2, "
  + "                            if (ADM.Adm_FecEgreSanat > " + globals.gbl_fecha_hoy_int + ", 41, 42) "
  + "                         , "
  + "                            if (ADM.Adm_FecEgreSanat > " + globals.gbl_fecha_hoy_int + ", 31, 32) "
  + "                         ) "
  + "                      ,0 "
  + "                      ) "
  + "                   ) "
  + "               ,0 "
  + "               ) "
  + "            ) "
  + "        ) "
  + "    ) Alta, HABITA.hab_baniodiscapa, HABITA.hab_dialisis, ADM.Adm_PasePiso, "
  + " HABITA.hab_consnumespera, if (PAN.panahiscli > 0 ,PAN.panaestado, 9) Pan, "
  + "if (cam_tiphistclin = 1 , OBRA.Obr_Codigo, OBR.Obr_Codigo) CodObras, '','', "
  + "if (cam_tiphistclin = 1 , OBRA.Obr_EsPami, OBR.Obr_EsPami) Es_Pami ,"
  + "if (cam_tiphistclin = 1 , OBRA.Obr_Ctamadre, OBR.Obr_Ctamadre) Ctamadre ,"
  + "if (cam_tiphistclin = 1 , Cam_utilstand, ADM.adm_utiliza) Utilactual, ADM.adm_telefRespon, ADM.adm_telefefono, ADM.admarm "
  + "from tbc_CAMAS "
  + "left join asistencial.tbc_admision ADM on ADM.adm_histclin = Cam_histclin "
  + "left join tbc_hist_cab_new HCAB  on HCAB.histcabnrounico = Cam_histclin "
  + "left join tbc_patologia PAT on PAT.Pat_Codi = ADM.Adm_PatolIng "
  + "left join tbc_obras OBR on OBR.Obr_Codigo = ADM.Adm_ObrSoc "
  + "left join tbc_obras OBRA  on OBRA.Obr_Codigo = HCAB.HistCabObra "
  + "left join tbc_utilcama UTILC  on UTILC.Utic_Utiliza = ADM.Adm_pasesec "
  + "left join tbc_habita HABITA on HABITA.Hab_nrohabi = Cam_habi "
  + "left join tbc_panalpac PAN on PAN.panahiscli = Cam_histclin "
  
  + "order by Cam_habi asc, Cam_cama asc "
  
  :rcf)