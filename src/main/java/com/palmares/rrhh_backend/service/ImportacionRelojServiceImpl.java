//package com.palmares.rrhh_backend.service;
//
//import com.palmares.rrhh_backend.dto.ImportacionRelojResponse;
//import com.palmares.rrhh_backend.dto.MarcacionArchivoDto;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Service
//public class ImportacionRelojServiceImpl implements ImportacionRelojService {
//
//    private final RelojFileParserService relojFileParserService;
//    private final ImportBatchService importBatchService;
//    private final MarcacionRawService marcacionRawService;
//
//    public ImportacionRelojServiceImpl(
//            RelojFileParserService relojFileParserService,
//            ImportBatchService importBatchService,
//            MarcacionRawService marcacionRawService
//    ) {
//        this.relojFileParserService = relojFileParserService;
//        this.importBatchService = importBatchService;
//        this.marcacionRawService = marcacionRawService;
//    }
//
//    @Override
//    public ImportacionRelojResponse importar(MultipartFile file, String usuario) {
//        if (file == null || file.isEmpty()) {
//            throw new RuntimeException("Debe enviar un archivo");
//        }
//
//        Long idBatch = importBatchService.crearBatch(file.getOriginalFilename(), usuario);
//
//        try {
//            List<MarcacionArchivoDto> filas = relojFileParserService.parsear(file);
//
//            int totalLeidas = filas.size();
//            int totalInsertadas = 0;
//            int totalDuplicadas = 0;
//            int totalObservadas = 0;
//
//            for (MarcacionArchivoDto fila : filas) {
//                MarcacionRawResultado resultado = marcacionRawService.guardarMarcacion(idBatch, fila);
//
//                if (resultado.isDuplicada()) {
//                    totalDuplicadas++;
//                } else {
//                    totalInsertadas++;
//                }
//
//                if (resultado.isObservada()) {
//                    totalObservadas++;
//                }
//            }
//
//            importBatchService.cerrarBatchOk(
//                    idBatch,
//                    totalLeidas,
//                    totalInsertadas,
//                    totalDuplicadas,
//                    totalObservadas
//            );
//
//            ImportacionRelojResponse response = new ImportacionRelojResponse();
//            response.setIdBatch(idBatch);
//            response.setArchivoNombre(file.getOriginalFilename());
//            response.setEstado("OK");
//            response.setTotalLeidas(totalLeidas);
//            response.setTotalInsertadas(totalInsertadas);
//            response.setTotalDuplicadas(totalDuplicadas);
//            response.setTotalObservadas(totalObservadas);
//
//            return response;
//
//        } catch (Exception e) {
//            importBatchService.cerrarBatchError(idBatch, e.getMessage());
//            throw new RuntimeException("Error al importar archivo: " + e.getMessage());
//        }
//    }
//}

package com.palmares.rrhh_backend.service;

import com.palmares.rrhh_backend.dto.ImportacionRelojResponse;
import com.palmares.rrhh_backend.dto.MarcacionArchivoDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImportacionRelojServiceImpl implements ImportacionRelojService {

    private final RelojFileParserService relojFileParserService;
    private final ImportBatchService importBatchService;
    private final MarcacionRawService marcacionRawService;

    public ImportacionRelojServiceImpl(
            RelojFileParserService relojFileParserService,
            ImportBatchService importBatchService,
            MarcacionRawService marcacionRawService
    ) {
        this.relojFileParserService = relojFileParserService;
        this.importBatchService = importBatchService;
        this.marcacionRawService = marcacionRawService;
    }

    @Override
    public ImportacionRelojResponse importar(MultipartFile file, String usuario) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Debe enviar un archivo");
        }

        Long idBatch = null;

        try {
            byte[] bytes = file.getBytes();

            idBatch = importBatchService.crearBatch(
                    file.getOriginalFilename(),
                    bytes,
                    usuario
            );

            List<MarcacionArchivoDto> filas = relojFileParserService.parsear(file);

            for (MarcacionArchivoDto fila : filas) {
                marcacionRawService.guardarMarcacion(idBatch, fila);
            }

            marcacionRawService.marcarDuplicadas(idBatch);
            marcacionRawService.marcarObservadas(idBatch);

            importBatchService.cerrarBatchOk(idBatch);

            return importBatchService.obtenerResumen(idBatch);

        } catch (Exception e) {
            if (idBatch != null) {
                importBatchService.cerrarBatchError(idBatch, e.getMessage());
            }
            throw new RuntimeException("Error al importar archivo: " + e.getMessage(), e);
        }
    }
}