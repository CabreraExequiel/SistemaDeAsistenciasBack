//package com.palmares.rrhh_backend.controller;
//
//import com.palmares.rrhh_backend.dto.ImportacionRelojResponse;
//import com.palmares.rrhh_backend.service.ImportacionRelojService;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/api/importaciones")
//public class ImportacionRelojController {
//
//    private final ImportacionRelojService importacionRelojService;
//
//    public ImportacionRelojController(ImportacionRelojService importacionRelojService) {
//        this.importacionRelojService = importacionRelojService;
//    }
//
//    @PostMapping("/reloj")
//    public ImportacionRelojResponse importarArchivoReloj(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("usuario") String usuario
//    ) {
//        return importacionRelojService.importar(file, usuario);
//    }
//}

package com.palmares.rrhh_backend.controller;

import com.palmares.rrhh_backend.dto.ImportacionRelojResponse;
import com.palmares.rrhh_backend.service.ImportacionRelojService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importaciones")
public class ImportacionRelojController {

    private final ImportacionRelojService importacionRelojService;

    public ImportacionRelojController(ImportacionRelojService importacionRelojService) {
        this.importacionRelojService = importacionRelojService;
    }

    @PostMapping("/reloj")
    public ImportacionRelojResponse importarArchivoReloj(
            @RequestParam("file") MultipartFile file,
            @RequestParam("usuario") String usuario
    ) {
        return importacionRelojService.importar(file, usuario);
    }
}