package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.MarcacionArchivoDto;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Service
public class MarcacionRawService {

    private final SimpleJdbcCall spInsMarcacionRaw;
    private final SimpleJdbcCall spMarcarDuplicadas;
    private final SimpleJdbcCall spMarcarObservadas;

    public MarcacionRawService(DataSource dataSource) {
        this.spInsMarcacionRaw = new SimpleJdbcCall(dataSource)
                .withSchemaName("dbo")
                .withProcedureName("SP_INS_MarcacionRaw");

        this.spMarcarDuplicadas = new SimpleJdbcCall(dataSource)
                .withSchemaName("dbo")
                .withProcedureName("SP_UPD_MarcacionRaw_MarcarDuplicadas");

        this.spMarcarObservadas = new SimpleJdbcCall(dataSource)
                .withSchemaName("dbo")
                .withProcedureName("SP_UPD_MarcacionRaw_MarcarObservadas");
    }

    public MarcacionRawResultado guardarMarcacion(Long idBatch, MarcacionArchivoDto fila) {
        Map<String, Object> in = new HashMap<>();
        in.put("id_batch", idBatch);
        in.put("legajo", fila.getLegajo());
        in.put("legajo_normalizado", fila.getLegajoNormalizado());
        in.put("fecha_hora", fila.getFechaHora());
        in.put("mchn", fila.getMchn());
        in.put("mode", fila.getMode());
        in.put("io_mode", fila.getIoMode());
        in.put("nombre_en_archivo", fila.getNombreEnArchivo());

        spInsMarcacionRaw.execute(in);

        MarcacionRawResultado r = new MarcacionRawResultado();
        r.setDuplicada(false);
        r.setObservada(false);
        return r;
    }

    public void marcarDuplicadas(Long idBatch) {
        Map<String, Object> in = new HashMap<>();
        in.put("id_batch", idBatch);
        spMarcarDuplicadas.execute(in);
    }

    public void marcarObservadas(Long idBatch) {
        Map<String, Object> in = new HashMap<>();
        in.put("id_batch", idBatch);
        spMarcarObservadas.execute(in);
    }
}