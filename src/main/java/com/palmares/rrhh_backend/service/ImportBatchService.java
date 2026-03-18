//package com.palmares.rrhh_backend.service;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class ImportBatchService {
//
//    public Long crearBatch(String archivoNombre, String usuario) {
//        // V1 simple: devuelve un id ficticio
//        return System.currentTimeMillis();
//    }
//
//    public void cerrarBatchOk(Long idBatch, int totalLeidas, int totalInsertadas, int totalDuplicadas, int totalObservadas) {
//        // V1 simple: no hace nada todavía
//    }
//
//    public void cerrarBatchError(Long idBatch, String mensajeError) {
//        // V1 simple: no hace nada todavía
//    }
//}



package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.ImportacionRelojResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class ImportBatchService {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcCall spInsImportBatch;
    private final SimpleJdbcCall spCerrarImportBatch;

    public ImportBatchService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.spInsImportBatch = new SimpleJdbcCall(dataSource)
                .withSchemaName("dbo")
                .withProcedureName("SP_INS_ImportBatch");

        this.spCerrarImportBatch = new SimpleJdbcCall(dataSource)
                .withSchemaName("dbo")
                .withProcedureName("SP_UPD_ImportBatch_Cerrar");
    }

    public Long crearBatch(String archivoNombre, byte[] contenidoArchivo, String usuario) {
        String hash = sha256(contenidoArchivo);

        Map<String, Object> in = new HashMap<>();
        in.put("archivo_nombre", archivoNombre);
        in.put("archivo_hash", hash);
        in.put("usuario", usuario);

        Map<String, Object> out = spInsImportBatch.execute(in);

        Object id = out.get("id_batch");
        if (id == null) {
            throw new RuntimeException("SP_INS_ImportBatch no devolvió id_batch");
        }

        return ((Number) id).longValue();
    }

    public void cerrarBatchOk(Long idBatch) {
        Map<String, Object> in = new HashMap<>();
        in.put("id_batch", idBatch);
        in.put("estado", "OK");
        in.put("log_error", null);

        spCerrarImportBatch.execute(in);
    }

    public void cerrarBatchError(Long idBatch, String error) {
        Map<String, Object> in = new HashMap<>();
        in.put("id_batch", idBatch);
        in.put("estado", "ERROR");
        in.put("log_error", error);

        spCerrarImportBatch.execute(in);
    }

    public ImportacionRelojResponse obtenerResumen(Long idBatch) {
        String sql = """
                SELECT id_batch, archivo_nombre, estado,
                       total_leidas, total_insertadas, total_duplicadas, total_observadas
                FROM dbo.ImportBatch
                WHERE id_batch = ?
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            ImportacionRelojResponse r = new ImportacionRelojResponse();
            r.setIdBatch(rs.getLong("id_batch"));
            r.setArchivoNombre(rs.getString("archivo_nombre"));
            r.setEstado(rs.getString("estado"));
            r.setTotalLeidas(rs.getInt("total_leidas"));
            r.setTotalInsertadas(rs.getInt("total_insertadas"));
            r.setTotalDuplicadas(rs.getInt("total_duplicadas"));
            r.setTotalObservadas(rs.getInt("total_observadas"));
            return r;
        }, idBatch);
    }

    private String sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString().toUpperCase(Locale.ROOT);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo calcular hash SHA-256", e);
        }
    }
}
